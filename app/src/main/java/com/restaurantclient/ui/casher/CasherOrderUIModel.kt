package com.restaurantclient.ui.casher

import com.restaurantclient.data.dto.OrderResponse

data class CasherOrderUIModel(
    val order: OrderResponse,
    val username: String
)
