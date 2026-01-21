package com.restaurantclient.ui.cashier

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

import com.restaurantclient.util.AnalyticsUtils
import com.restaurantclient.util.DashboardAnalytics
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.BuildConfig

@HiltViewModel
class CashierDashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<Result<DashboardSummaryDTO>>()
    val dashboardStats: LiveData<Result<DashboardSummaryDTO>> = _dashboardStats

    private val _analytics = MutableLiveData<DashboardAnalytics>()
    val analytics: LiveData<DashboardAnalytics> = _analytics

    private val _currentUser = MutableLiveData<Result<UserDTO>>()
    val currentUser: LiveData<Result<UserDTO>> = _currentUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        // Start WebSocket connection for live updates
        webSocketManager.connect(BuildConfig.BASE_URL)
        
        viewModelScope.launch {
            webSocketManager.events.collect { event ->
                if (event is WebSocketEvent.RefreshRequired) {
                    refreshStats()
                }
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true
            loadCurrentUser()
            loadStats()
            _loading.value = false
        }
    }

    private suspend fun loadStats() {
        try {
            val analyticsResult = orderRepository.getDashboardAnalytics()
            val productsResult = productRepository.getAllProducts(forceRefresh = true)

            if (analyticsResult is Result.Success && productsResult is Result.Success) {
                val analyticsData = analyticsResult.data
                val products = productsResult.data
                
                // Map backend DTO to UI model
                val mappedAnalytics = DashboardAnalytics(
                    totalRevenue = analyticsData.totalRevenue,
                    totalOrders = analyticsData.totalOrders,
                    pendingOrders = analyticsData.pendingOrders,
                    completedOrders = analyticsData.completedOrders,
                    cancelledOrders = analyticsData.cancelledOrders,
                    averageOrderValue = analyticsData.averageOrderValue,
                    dailyRevenue = analyticsData.dailyRevenue
                )
                _analytics.value = mappedAnalytics

                // Update legacy summary DTO for UI components that still use it
                val summary = DashboardSummaryDTO(
                    userCount = 0, 
                    productCount = products.size,
                    orderCount = analyticsData.totalOrders,
                    pendingOrders = analyticsData.pendingOrders,
                    completedOrders = analyticsData.completedOrders,
                    cancelledOrders = analyticsData.cancelledOrders
                )
                _dashboardStats.value = Result.Success(summary)
            } else {
                // Try basic summary endpoint as fallback
                val summaryResult = orderRepository.getDashboardSummary()
                if (summaryResult is Result.Success) {
                     _dashboardStats.value = summaryResult
                     _analytics.value = DashboardAnalytics(0.0, 0, 0, 0, 0, 0.0, emptyMap())
                } else {
                    // Ultimate Fallback: Client-side calculation using getAllOrders
                    // This is necessary if backend removes summary endpoints
                    val ordersResult = orderRepository.getAllOrders(forceRefresh = true)
                    if (ordersResult is Result.Success) {
                        val orders = ordersResult.data
                        val calculated = AnalyticsUtils.calculateStats(orders)
                        
                        _analytics.value = calculated
                        
                        val productCount = if (productsResult is Result.Success) productsResult.data.size else 0
                        
                        val summary = DashboardSummaryDTO(
                            userCount = 0,
                            productCount = productCount,
                            orderCount = orders.size,
                            pendingOrders = calculated.pendingOrders,
                            completedOrders = calculated.completedOrders,
                            cancelledOrders = calculated.cancelledOrders
                        )
                        _dashboardStats.value = Result.Success(summary)
                    } else {
                        _dashboardStats.value = Result.Error(Exception("Failed to load stats"))
                    }
                }
            }
        } catch (e: Exception) {
            _dashboardStats.value = Result.Error(e)
        }
    }

    private suspend fun loadFallbackStats() {
         // merged into loadStats for better analytics support
    }

    private suspend fun loadCurrentUser() {
        try {
            val result = userRepository.getCurrentUser()
            _currentUser.value = result
        } catch (e: Exception) {
            _currentUser.value = Result.Error(e)
        }
    }
    
    private var refreshJob: Job? = null

    // Helper to refresh just stats
    fun refreshStats() {
        // Debounce refresh calls to avoid 429 Too Many Requests
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            delay(1000) // Wait for 1 second of silence
            loadStats()
        }
    }
}
