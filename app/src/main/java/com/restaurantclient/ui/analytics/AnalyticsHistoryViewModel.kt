package com.restaurantclient.ui.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.repository.OrderRepository
import com.restaurantclient.util.AnalyticsUtils
import com.restaurantclient.util.DailyAnalyticsItem
import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _history = MutableLiveData<List<DailyAnalyticsItem>>()
    val history: LiveData<List<DailyAnalyticsItem>> = _history

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        // Start WebSocket connection for live updates
        webSocketManager.connect(BuildConfig.BASE_URL)

        viewModelScope.launch {
            webSocketManager.events.collect { event ->
                if (event is WebSocketEvent.RefreshRequired) {
                    loadHistory(showLoading = false)
                }
            }
        }
    }

    fun loadHistory(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _loading.value = true
            try {
                val result = orderRepository.getAllOrders(forceRefresh = true)
                if (result is Result.Success) {
                    val stats = AnalyticsUtils.getDetailedDailyStats(result.data)
                    _history.value = stats
                } else {
                    _error.value = "Failed to load orders"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                if (showLoading) _loading.value = false
            }
        }
    }
}
