package com.restaurantclient.ui.admin

import com.restaurantclient.data.dto.OrderResponse

data class AdminOrderUIModel(
    val order: OrderResponse,
    val username: String
)
