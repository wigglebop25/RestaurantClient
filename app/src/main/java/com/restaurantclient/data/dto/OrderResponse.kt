package com.restaurantclient.data.dto

import java.io.Serializable

data class OrderResponse(
    val order_id: Int,
    val user_id: Int,
    val user: UserDTO?,
    val total_amount: Double, // JSON shows number, could be Double
    val status: String?,
    val products: List<OrderProductResponse>?,
    val created_at: String?,
    val updated_at: String?
) : Serializable
