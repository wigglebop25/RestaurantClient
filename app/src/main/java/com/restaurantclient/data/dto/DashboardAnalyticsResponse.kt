package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class DashboardAnalyticsResponse(
    @SerializedName("total_revenue")
    val totalRevenue: Double,
    @SerializedName("total_orders")
    val totalOrders: Int,
    @SerializedName("pending_orders")
    val pendingOrders: Int,
    @SerializedName("completed_orders")
    val completedOrders: Int,
    @SerializedName("cancelled_orders")
    val cancelledOrders: Int,
    @SerializedName("average_order_value")
    val averageOrderValue: Double,
    @SerializedName("daily_revenue")
    val dailyRevenue: Map<String, Double>?,
    @SerializedName("revenue_trend")
    val revenueTrend: List<RevenueTrendItem>?
) {
    val consolidatedDailyRevenue: Map<String, Double>
        get() {
            if (dailyRevenue != null && dailyRevenue.isNotEmpty()) return dailyRevenue
            
            return revenueTrend?.associate { it.date to it.revenue } ?: emptyMap()
        }
}

data class RevenueTrendItem(
    @SerializedName("date")
    val date: String,
    @SerializedName("revenue")
    val revenue: Double,
    @SerializedName("order_count")
    val orderCount: Int
)
