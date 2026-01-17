package com.restaurantclient.ui.casher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.data.repository.UserRepository
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasherOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _allOrders = MutableLiveData<List<CasherOrderUIModel>>()
    
    private val _filteredOrders = MutableLiveData<Result<List<CasherOrderUIModel>>>()
    val orders: LiveData<Result<List<CasherOrderUIModel>>> = _filteredOrders

    private val _updateResult = MutableLiveData<Result<OrderResponse>>()
    val updateResult: LiveData<Result<OrderResponse>> = _updateResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var currentFilter: String = "ALL"
    private var pollingJob: Job? = null

    fun loadOrders(forceRefresh: Boolean = false, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _loading.value = true
            try {
                // Fetch orders and users in parallel to resolve usernames
                val ordersDeferred = async { orderRepository.getAllOrders(forceRefresh) }
                val usersDeferred = async { userRepository.getAllUsers(forceRefresh) }

                val ordersResult = ordersDeferred.await()
                val usersResult = usersDeferred.await()

                if (ordersResult is Result.Success) {
                    val ordersList = ordersResult.data
                    val userMap = if (usersResult is Result.Success) {
                        usersResult.data.associateBy { it.userId }
                    } else {
                        emptyMap()
                    }
                    
                    val uiModels = ordersList.map { order ->
                        CasherOrderUIModel(
                            order = order,
                            username = userMap[order.user_id]?.username ?: "User #${order.user_id}"
                        )
                    }
                    
                    _allOrders.value = uiModels
                    applyFilter()
                } else if (ordersResult is Result.Error) {
                    _filteredOrders.value = Result.Error(ordersResult.exception)
                }
            } catch (e: Exception) {
                _filteredOrders.value = Result.Error(e)
            } finally {
                if (showLoading) _loading.value = false
            }
        }
    }

    fun setFilter(filter: String) {
        currentFilter = filter
        _allOrders.value?.let { uiModels ->
            _filteredOrders.value = Result.Success(filterOrders(uiModels, filter))
        }
    }

    private fun applyFilter() {
        _allOrders.value?.let { uiModels ->
            _filteredOrders.value = Result.Success(filterOrders(uiModels, currentFilter))
        }
    }

    private fun filterOrders(uiModels: List<CasherOrderUIModel>, filter: String): List<CasherOrderUIModel> {
        if (filter == "ALL") return uiModels.sortedByDescending { it.order.created_at }
        
        return uiModels.filter { model ->
            model.order.status?.equals(filter, ignoreCase = true) == true
        }.sortedByDescending { it.order.created_at }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val result = orderRepository.updateOrderStatus(orderId, newStatus)
                _updateResult.value = result
                orderRepository.clearCache() // Invalidate cache after update
                loadOrders(true, showLoading = false) // Refresh orders silently
            } catch (e: Exception) {
                _updateResult.value = Result.Error(e)
            }
        }
    }

    fun startPollingOrders() {
        if (pollingJob?.isActive == true) return

        // Connect to WebSocket
        webSocketManager.connect(BuildConfig.BASE_URL)
        
        pollingJob = viewModelScope.launch {
            // Initial load
            loadOrders(forceRefresh = true, showLoading = false)

            // Listen for WebSocket events
            launch {
                webSocketManager.events.collect { event ->
                    if (event is WebSocketEvent.RefreshRequired) {
                        // Double-fetch strategy
                        loadOrders(forceRefresh = true, showLoading = false)
                        delay(1000)
                        loadOrders(forceRefresh = true, showLoading = false)
                    }
                }
            }
        }
    }

    fun stopPollingOrders() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingOrders()
    }
}
