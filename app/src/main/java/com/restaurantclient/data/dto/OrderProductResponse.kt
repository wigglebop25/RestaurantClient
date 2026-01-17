package com.restaurantclient.data.dto

import java.io.Serializable

data class OrderProductResponse(
    val order_id: Int,
    val product_id: Int,
    val product: ProductResponse?, // Assuming ProductResponse exists and matches
    val quantity: Int,
    val unit_price: Double,
    val line_total: Double
) : Serializable
