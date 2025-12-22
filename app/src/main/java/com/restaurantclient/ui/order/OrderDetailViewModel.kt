package com.restaurantclient.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
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
        
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchOrder(orderId)
                delay(5000)
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
