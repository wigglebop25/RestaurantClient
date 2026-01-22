package com.restaurantclient.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.DashboardSummaryDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.repository.DashboardRepository
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.data.repository.ProductRepository
import com.restaurantclient.data.repository.RoleRepository
import com.restaurantclient.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import com.restaurantclient.util.AnalyticsUtils
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.BuildConfig

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val roleRepository: RoleRepository,
    private val dashboardRepository: DashboardRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<DashboardStats>()
    val dashboardStats: LiveData<DashboardStats> = _dashboardStats
    
    private val _dashboardSummary = MutableLiveData<DashboardSummaryDTO>()
    val dashboardSummary: LiveData<DashboardSummaryDTO> = _dashboardSummary

    private val _recentUsers = MutableLiveData<List<UserDTO>>()
    val recentUsers: LiveData<List<UserDTO>> = _recentUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private var dashboardSummaryAvailable = true // Track if endpoint is available

    init {
        // Start WebSocket connection for live updates
        webSocketManager.connect(BuildConfig.BASE_URL)
        
        viewModelScope.launch {
            webSocketManager.events.collect { event ->
                if (event is WebSocketEvent.RefreshRequired) {
                    loadDashboardData(showLoading = false)
                }
            }
        }
    }

    fun loadDashboardData(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _loading.value = true
            
            try {
                // 1. Fetch advanced analytics from the new backend endpoint
                val analyticsResult = orderRepository.getDashboardAnalytics()
                
                // 2. Fetch basic entity lists for other dashboard needs
                val usersResult = userRepository.getAllUsers(forceRefresh = true)
                val productsResult = productRepository.getAllProducts(forceRefresh = true)
                val rolesResult = roleRepository.getAllRoles(forceRefresh = true)

                if (analyticsResult is Result.Success && usersResult is Result.Success && 
                    productsResult is Result.Success) {
                    
                    val analyticsData = analyticsResult.data
                    val users = usersResult.data
                    val rolesCount = if (rolesResult is Result.Success) rolesResult.data.size else 0
                    
                    // Use server-side calculated stats which are accurate despite pagination
                    val stats = DashboardStats(
                        totalUsers = users.size,
                        totalOrders = analyticsData.totalOrders,
                        totalProducts = productsResult.data.size,
                        totalRoles = rolesCount,
                        newUsersToday = calculateNewUsersToday(users),
                        pendingOrders = analyticsData.pendingOrders,
                        completedOrders = analyticsData.completedOrders,
                        cancelledOrders = analyticsData.cancelledOrders,
                        totalRevenue = analyticsData.totalRevenue,
                        averageOrderValue = analyticsData.averageOrderValue,
                        dailyRevenue = analyticsData.dailyRevenue
                    )
                    
                    _dashboardStats.value = stats
                    _recentUsers.value = users.takeLast(5).reversed()
                    
                    // Maintain summary DTO for legacy observers
                    _dashboardSummary.value = DashboardSummaryDTO(
                        userCount = users.size,
                        productCount = productsResult.data.size,
                        orderCount = analyticsData.totalOrders,
                        pendingOrders = analyticsData.pendingOrders,
                        completedOrders = analyticsData.completedOrders,
                        cancelledOrders = analyticsData.cancelledOrders
                    )
                } else if (analyticsResult is Result.Error) {
                    android.util.Log.w("AdminDashboardVM", "New analytics endpoint failed, falling back to local calculation")
                    loadDashboardDataFallback()
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminDashboardVM", "Error loading data", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                if (showLoading) _loading.value = false
            }
        }
    }

    private suspend fun loadDashboardDataFallback() {
        try {
            val usersResult = userRepository.getAllUsers(forceRefresh = true)
            val ordersResult = orderRepository.getAllOrders(forceRefresh = true)
            val productsResult = productRepository.getAllProducts(forceRefresh = true)
            val rolesResult = roleRepository.getAllRoles(forceRefresh = true)

            if (usersResult is Result.Success && ordersResult is Result.Success && productsResult is Result.Success) {
                val users = usersResult.data
                val rolesCount = if (rolesResult is Result.Success) rolesResult.data.size else 0
                val analytics = AnalyticsUtils.calculateStats(ordersResult.data)
                
                val stats = DashboardStats(
                    totalUsers = users.size,
                    totalOrders = ordersResult.data.size,
                    totalProducts = productsResult.data.size,
                    totalRoles = rolesCount,
                    newUsersToday = calculateNewUsersToday(users),
                    pendingOrders = analytics.pendingOrders,
                    completedOrders = analytics.completedOrders,
                    cancelledOrders = analytics.cancelledOrders,
                    totalRevenue = analytics.totalRevenue,
                    averageOrderValue = analytics.averageOrderValue,
                    dailyRevenue = analytics.dailyRevenue
                )
                _dashboardStats.value = stats
                _recentUsers.value = users.takeLast(5).reversed()
            }
        } catch (e: Exception) {
            android.util.Log.e("AdminDashboardVM", "Fallback loading failed", e)
        }
    }

    private fun calculateNewUsersToday(users: List<UserDTO>): Int {
        val today = LocalDate.now()
        // Format from backend is often "dd/MM/yyyy" or "yyyy-MM-dd"
        return users.count { user ->
            user.createdAt?.let { createdAt ->
                try {
                    // Try dd/MM/yyyy first
                    val date = if (createdAt.contains("/")) {
                        LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } else {
                        // Fallback to yyyy-MM-dd
                        LocalDate.parse(createdAt.substring(0, 10))
                    }
                    date == today
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }
    }

    fun refreshData() {
        userRepository.clearCache()
        orderRepository.clearCache()
        productRepository.clearCache()
        roleRepository.clearCache()
        loadDashboardData()
    }
    
    override fun onCleared() {
        super.onCleared()
        // Connection logic managed by singleton WebSocketManager; explicit disconnection not required here.
    }
}

data class DashboardStats(
    val totalUsers: Int,
    val totalOrders: Int,
    val totalProducts: Int,
    val totalRoles: Int = 0,
    val newUsersToday: Int,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val cancelledOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val averageOrderValue: Double = 0.0,
    val dailyRevenue: Map<String, Double> = emptyMap()
)
