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

data class DailyAnalyticsItem(
    val date: String,
    val revenue: Double,
    val orderCount: Int
)

object AnalyticsUtils {

    fun getDetailedDailyStats(orders: List<OrderResponse>): List<DailyAnalyticsItem> {
        val statsMap = mutableMapOf<String, Pair<BigDecimal, Int>>()
        // Format: "January 18, 2026 07:15 PM"
        val inputFormat = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US)
        // Sortable format: "yyyy-MM-dd"
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Include COMPLETED, READY, PREPARING, and ACCEPTED orders
        orders.filter { 
            val status = it.status
            status?.equals("completed", ignoreCase = true) == true || 
            status?.equals("ready", ignoreCase = true) == true ||
            status?.equals("preparing", ignoreCase = true) == true ||
            status?.equals("accepted", ignoreCase = true) == true
        }.forEach { order ->
            val dateStr = try {
                if (order.created_at != null) {
                    val date = inputFormat.parse(order.created_at)
                    if (date != null) {
                        outputFormat.format(date)
                    } else {
                        "Unknown"
                    }
                } else {
                    "Unknown"
                }
            } catch (e: Exception) {
                order.created_at?.take(10) ?: "Unknown"
            }
            
            val amount = try { BigDecimal(order.total_amount) } catch (e: Exception) { BigDecimal.ZERO }
            
            val current = statsMap.getOrDefault(dateStr, Pair(BigDecimal.ZERO, 0))
            statsMap[dateStr] = Pair(current.first.add(amount), current.second + 1)
        }

        return statsMap.map { 
            DailyAnalyticsItem(
                date = it.key, 
                revenue = it.value.first.toDouble(), 
                orderCount = it.value.second
            ) 
        }.sortedByDescending { it.date }
    }

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
        // Format: "January 18, 2026 07:15 PM"
        val inputFormat = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US)
        // Sortable format: "2026-01-18"
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Include COMPLETED, READY, PREPARING, and ACCEPTED orders for revenue calculation
        orders.filter { 
            val status = it.status
            status?.equals("completed", ignoreCase = true) == true || 
            status?.equals("ready", ignoreCase = true) == true ||
            status?.equals("preparing", ignoreCase = true) == true ||
            status?.equals("accepted", ignoreCase = true) == true
        }.forEach { order ->
            val dateStr = try {
                if (order.created_at != null) {
                    val date = inputFormat.parse(order.created_at)
                    if (date != null) {
                        outputFormat.format(date)
                    } else {
                        "Unknown"
                    }
                } else {
                    "Unknown"
                }
            } catch (e: Exception) {
                // Fallback for unexpected formats
                order.created_at?.take(10) ?: "Unknown"
            }
            
            val amount = try { BigDecimal(order.total_amount) } catch (e: Exception) { BigDecimal.ZERO }
            
            val current = revenueMap.getOrDefault(dateStr, BigDecimal.ZERO)
            revenueMap[dateStr] = current.add(amount)
        }

        return revenueMap.mapValues { it.value.toDouble() }
    }
}
