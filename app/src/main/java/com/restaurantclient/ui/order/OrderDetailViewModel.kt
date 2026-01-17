package com.restaurantclient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _order = MutableLiveData<Result<OrderResponse>>()
    val order: LiveData<Result<OrderResponse>> = _order

    private var pollingJob: Job? = null

    fun loadOrderDetails(orderId: Int) {
        viewModelScope.launch {
            fetchOrder(orderId)
        }
    }

    fun startPolling(orderId: Int) {
        if (pollingJob?.isActive == true) return
        
        // Connect to WebSocket
        webSocketManager.connect(BuildConfig.BASE_URL)

        pollingJob = viewModelScope.launch {
            // Initial fetch
            fetchOrder(orderId)

            // Listen for WebSocket events
            launch {
                webSocketManager.events.collect { event ->
                    if (event is WebSocketEvent.RefreshRequired) {
                        fetchOrder(orderId)
                    }
                }
            }

            // Fallback polling loop
            while (true) {
                delay(5000)
                fetchOrder(orderId)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private suspend fun fetchOrder(orderId: Int) {
        try {
            val result = orderRepository.getOrderById(orderId)
            _order.value = result
        } catch (e: Exception) {
            _order.value = Result.Error(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
