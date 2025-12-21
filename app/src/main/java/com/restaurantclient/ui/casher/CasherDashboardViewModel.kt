package com.restaurantclient.ui.casher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.DashboardSummaryDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.data.repository.ProductRepository
import com.restaurantclient.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasherDashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<Result<DashboardSummaryDTO>>()
    val dashboardStats: LiveData<Result<DashboardSummaryDTO>> = _dashboardStats

    private val _currentUser = MutableLiveData<Result<UserDTO>>()
    val currentUser: LiveData<Result<UserDTO>> = _currentUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var pollingJob: Job? = null

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true
            loadCurrentUser()
            loadStats()
            _loading.value = false
        }
    }

    fun startPollingStats() {
        if (pollingJob?.isActive == true) return
        
        pollingJob = viewModelScope.launch {
            while (true) {
                loadStats()
                delay(5000)
            }
        }
    }

    fun stopPollingStats() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingStats()
    }

    private suspend fun loadStats() {
        try {
            val result = orderRepository.getDashboardSummary()
            if (result is Result.Success) {
                _dashboardStats.value = result
            } else {
                // Fallback: Fetch orders and products manually
                loadFallbackStats()
            }
        } catch (e: Exception) {
            loadFallbackStats()
        }
    }

    private suspend fun loadFallbackStats() {
        try {
            val ordersDeferred = viewModelScope.async { orderRepository.getAllOrders() }
            val productsDeferred = viewModelScope.async { productRepository.getAllProducts() }

            val ordersResult = ordersDeferred.await()
            val productsResult = productsDeferred.await()

            if (ordersResult is Result.Success && productsResult is Result.Success) {
                val orders = ordersResult.data
                val products = productsResult.data

                val summary = DashboardSummaryDTO(
                    userCount = 0, // Unknown
                    productCount = products.size,
                    orderCount = orders.size,
                    pendingOrders = orders.count { it.status.equals("Pending", true) },
                    completedOrders = orders.count { it.status.equals("Completed", true) },
                    cancelledOrders = orders.count { it.status.equals("Cancelled", true) }
                )
                _dashboardStats.value = Result.Success(summary)
            } else {
                _dashboardStats.value = Result.Error(Exception("Failed to load stats (API and fallback)"))
            }
        } catch (e: Exception) {
            _dashboardStats.value = Result.Error(e)
        }
    }

    private suspend fun loadCurrentUser() {
        try {
            val result = userRepository.getCurrentUser()
            _currentUser.value = result
        } catch (e: Exception) {
            _currentUser.value = Result.Error(e)
        }
    }
    
    // Helper to refresh just stats
    fun refreshStats() {
        viewModelScope.launch {
            _loading.value = true
            loadStats()
            _loading.value = false
        }
    }
}
