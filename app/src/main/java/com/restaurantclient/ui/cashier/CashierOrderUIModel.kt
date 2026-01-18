package com.restaurantclient.ui.cashier

import com.restaurantclient.data.dto.OrderResponse

data class CashierOrderUIModel(
    val order: OrderResponse,
    val username: String
)
