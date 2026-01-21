package com.restaurantclient.ui.cashier

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
class CashierOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _allOrders = MutableLiveData<List<CashierOrderUIModel>>()
    
    private val _filteredOrders = MutableLiveData<Result<List<CashierOrderUIModel>>>()
    val orders: LiveData<Result<List<CashierOrderUIModel>>> = _filteredOrders

    private val _updateResult = MutableLiveData<Result<OrderResponse>>()
    val updateResult: LiveData<Result<OrderResponse>> = _updateResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var currentFilter: String = "ALL"
    private var pollingJob: Job? = null

    private var currentSearchQuery: String? = null

    fun loadOrders(forceRefresh: Boolean = false, showLoading: Boolean = true, query: String? = null) {
        currentSearchQuery = query
        viewModelScope.launch {
            if (showLoading) _loading.value = true
            try {
                // Pass search query to repository for server-side filtering
                val ordersResult = orderRepository.getAllOrders(
                    forceRefresh = forceRefresh,
                    search = query
                )

                if (ordersResult is Result.Success) {
                    val ordersList = ordersResult.data
                    
                    val uiModels = ordersList.map { order ->
                        CashierOrderUIModel(
                            order = order,
                            username = order.user?.username ?: "User #${order.user_id}"
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

    fun searchOrders(query: String?) {
        loadOrders(forceRefresh = true, showLoading = true, query = query)
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

    private fun filterOrders(uiModels: List<CashierOrderUIModel>, filter: String): List<CashierOrderUIModel> {
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
                if (result is Result.Success) {
                    // Update local list with the fresh order data from backend
                    val updatedOrder = result.data
                    val currentList = _allOrders.value?.toMutableList() ?: mutableListOf()
                    val index = currentList.indexOfFirst { it.order.order_id == orderId }
                    if (index != -1) {
                        currentList[index] = currentList[index].copy(order = updatedOrder)
                        _allOrders.value = currentList
                        applyFilter()
                    }
                }
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
