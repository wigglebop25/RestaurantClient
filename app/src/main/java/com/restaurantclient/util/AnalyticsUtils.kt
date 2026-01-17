package com.restaurantclient.util

import com.restaurantclient.data.dto.OrderResponse
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardAnalytics(
    val totalRevenue: Double,
    val totalOrders: Int,
    val pendingOrders: Int,
    val completedOrders: Int,
    val cancelledOrders: Int,
    val averageOrderValue: Double,
    val dailyRevenue: Map<String, Double> = emptyMap()
)

object AnalyticsUtils {

    fun calculateStats(orders: List<OrderResponse>): DashboardAnalytics {
        var totalRevenue = BigDecimal.ZERO
        var pending = 0
        var completed = 0
        var cancelled = 0

        orders.forEach { order ->
            val amount = try {
                BigDecimal(order.total_amount)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }

            if (order.status?.equals("completed", ignoreCase = true) == true) {
                totalRevenue = totalRevenue.add(amount)
                completed++
            } else if (order.status?.equals("pending", ignoreCase = true) == true) {
                pending++
            } else if (order.status?.equals("cancelled", ignoreCase = true) == true) {
                cancelled++
            }
        }

        val totalOrders = orders.size
        val avgOrderValue = if (completed > 0) totalRevenue.divide(BigDecimal(completed), 2, java.math.RoundingMode.HALF_UP).toDouble() else 0.0

        return DashboardAnalytics(
            totalRevenue = totalRevenue.toDouble(),
            totalOrders = totalOrders,
            pendingOrders = pending,
            completedOrders = completed,
            cancelledOrders = cancelled,
            averageOrderValue = avgOrderValue,
            dailyRevenue = getDailyRevenue(orders)
        )
    }

    fun getDailyRevenue(orders: List<OrderResponse>): Map<String, Double> {
        val revenueMap = mutableMapOf<String, BigDecimal>()

        // Include all non-cancelled orders for chart visibility
        orders.filter { 
            it.status?.equals("cancelled", ignoreCase = true) == false 
        }.forEach { order ->
            val dateStr = try {
                // Assuming created_at is ISO 8601 or similar. If it fails, we skip.
                // Let's assume standard format for now, or just take the first 10 chars
                order.created_at?.take(10) ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
            
            val amount = try { BigDecimal(order.total_amount) } catch (e: Exception) { BigDecimal.ZERO }
            
            val current = revenueMap.getOrDefault(dateStr, BigDecimal.ZERO)
            revenueMap[dateStr] = current.add(amount)
        }

        return revenueMap.mapValues { it.value.toDouble() }
    }
}
