package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class DashboardSummaryDTO(
    @SerializedName("user_count")
    val userCount: Int,
    
    @SerializedName("product_count")
    val productCount: Int,
    
    @SerializedName("order_count")
    val orderCount: Int,
    
    @SerializedName("pending_orders")
    val pendingOrders: Int,
    
    @SerializedName("completed_orders")
    val completedOrders: Int,
    
    @SerializedName("cancelled_orders")
    val cancelledOrders: Int
)
