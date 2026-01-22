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
            // Fetch all necessary data in parallel
            // We fetch orders to calculate daily revenue locally because the backend endpoint 
            // sometimes returns zero values for daily revenue despite having orders.
            val analyticsDeferred = viewModelScope.async { orderRepository.getDashboardAnalytics() }
            val productsDeferred = viewModelScope.async { productRepository.getAllProducts(forceRefresh = true) }
            val ordersDeferred = viewModelScope.async { orderRepository.getAllOrders(forceRefresh = true) }

            val analyticsResult = analyticsDeferred.await()
            val productsResult = productsDeferred.await()
            val ordersResult = ordersDeferred.await()

            if (analyticsResult is Result.Success && productsResult is Result.Success) {
                val analyticsData = analyticsResult.data
                val products = productsResult.data
                
                // Get the daily revenue map from server (handles both map and trend list formats)
                // Normalize keys to yyyy-MM-dd to match local calculation and prevent duplicates
                val serverDailyRevenueRaw = analyticsData.consolidatedDailyRevenue
                val serverDailyRevenue = serverDailyRevenueRaw.mapKeys { (key, _) ->
                    if (key.length >= 10) key.substring(0, 10) else key
                }
                
                // Calculate daily revenue locally from the full orders list to fix backend bugs
                val finalDailyRevenue = if (ordersResult is Result.Success) {
                    val localDailyRevenue = AnalyticsUtils.getDailyRevenue(ordersResult.data)
                    val merged = serverDailyRevenue.toMutableMap()
                    
                    // Overlay local data onto server data where server is missing data or reports 0
                    localDailyRevenue.forEach { (date, revenue) ->
                        // Since keys are now normalized, this will correctly overwrite/merge
                        if (merged[date] == null || merged[date] == 0.0) {
                            merged[date] = revenue
                        }
                    }
                    merged
                } else {
                    serverDailyRevenue
                }
                
                // Map backend DTO to UI model
                val mappedAnalytics = DashboardAnalytics(
                    totalRevenue = analyticsData.totalRevenue,
                    totalOrders = analyticsData.totalOrders,
                    pendingOrders = analyticsData.pendingOrders,
                    completedOrders = analyticsData.completedOrders,
                    cancelledOrders = analyticsData.cancelledOrders,
                    averageOrderValue = analyticsData.averageOrderValue,
                    dailyRevenue = finalDailyRevenue
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
            } else if (ordersResult is Result.Success) {
                // Fallback: Client-side calculation using getAllOrders
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
                // Ultimate fallback if everything fails
                _dashboardStats.value = Result.Error(Exception("Failed to load stats"))
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
