package com.restaurantclient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _createOrderResult = MutableLiveData<Result<OrderResponse>>()
    val createOrderResult: LiveData<Result<OrderResponse>> = _createOrderResult

    private val _userOrders = MutableLiveData<Result<List<OrderResponse>>>()
    val userOrders: LiveData<Result<List<OrderResponse>>> = _userOrders
    
    private var isCreatingOrder = false
    private var pollingJob: Job? = null

    init {
        // Removed empty init block as we handle collection in startPollingOrders
    }

    private var currentUsername: String? = null

    fun createOrder(createOrderRequest: CreateOrderRequest, username: String) { // Pass username to refresh orders
        if (isCreatingOrder) return
        
        isCreatingOrder = true
        viewModelScope.launch {
            val result = orderRepository.createOrder(createOrderRequest)
            _createOrderResult.postValue(result)
            isCreatingOrder = false
            
            // Force refresh user orders after creating a new order
            if (result is Result.Success) {
                fetchUserOrders(username, forceRefresh = true)
            }
        }
    }

    fun fetchUserOrders(username: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val result = orderRepository.getUserOrders(username, forceRefresh)
            _userOrders.postValue(result)
        }
    }

    fun startPollingOrders(username: String) {
        currentUsername = username
        
        // Connect to WebSocket
        webSocketManager.connect(BuildConfig.BASE_URL)
        
        // Start listening for WS events if not already done
        if (pollingJob?.isActive == true) return
        
        pollingJob = viewModelScope.launch {
            // Initial fetch
            fetchUserOrders(username, forceRefresh = true)
            
            // Launch a separate coroutine for WebSocket events
            launch {
                webSocketManager.events.collect { event ->
                    if (event is WebSocketEvent.RefreshRequired) {
                        // Double-fetch strategy to handle backend race conditions
                        // 1. Fetch immediately (optimistic)
                        fetchUserOrders(username, forceRefresh = true)
                        
                        // 2. Fetch again after 1 second to catch eventual consistency updates
                        delay(1000)
                        fetchUserOrders(username, forceRefresh = true)
                    }
                }
            }
        }
    }

    fun stopPollingOrders() {
        pollingJob?.cancel()
        pollingJob = null
        webSocketManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingOrders()
    }
}
