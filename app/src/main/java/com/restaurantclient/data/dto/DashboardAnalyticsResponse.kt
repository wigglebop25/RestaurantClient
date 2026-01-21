package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class DailyRevenueDTO(
    @SerializedName("date")
    val date: String,
    @SerializedName("revenue")
    val amount: Double,
    @SerializedName("order_count")
    val orderCount: Int
)

data class DashboardAnalyticsResponse(
    @SerializedName("total_revenue")
    val totalRevenue: Double,
    @SerializedName("total_orders")
    val totalOrders: Int,
    @SerializedName("pending_count")
    val pendingCount: Int,
    @SerializedName("daily_revenue")
    val dailyRevenue: List<DailyRevenueDTO>
)
