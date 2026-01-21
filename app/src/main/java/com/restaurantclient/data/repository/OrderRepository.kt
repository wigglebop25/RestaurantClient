package com.restaurantclient.data.repository

import android.util.Log
import com.restaurantclient.data.Result
import com.restaurantclient.data.TokenManager
import com.restaurantclient.data.dto.CreateOrderRequest
import com.restaurantclient.data.dto.DashboardAnalyticsResponse
import com.restaurantclient.data.dto.DashboardSummaryDTO
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.data.dto.UpdateOrderRequest
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    private var cachedAllOrders: List<OrderResponse>? = null
    private val cachedUserOrders: MutableMap<String, List<OrderResponse>> = mutableMapOf()

    fun clearCache() {
        cachedAllOrders = null
        cachedUserOrders.clear()
        Log.d("OrderRepository", "Order caches cleared.")
    }

    suspend fun createOrder(createOrderRequest: CreateOrderRequest): Result<OrderResponse> {
        return try {
            val response = apiService.createOrder(createOrderRequest)
            if (response.isSuccessful) {
                clearCache() 
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to create order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserOrders(username: String, forceRefresh: Boolean = false): Result<List<OrderResponse>> {
        if (cachedUserOrders.containsKey(username) && !forceRefresh) {
            Log.d("OrderRepository", "Returning cached orders for user: $username")
            return Result.Success(cachedUserOrders[username]!!)
        }

        return try {
            Log.d("OrderRepository", "Fetching orders for username: $username (forceRefresh: $forceRefresh)")
            
            fun parseBody(responseBody: okhttp3.ResponseBody?): List<OrderResponse> {
                val json = responseBody?.string()
                if (json.isNullOrEmpty()) return emptyList()
                val trimmed = json.trim()
                return if (trimmed.startsWith("[")) {
                    val type = object : com.google.gson.reflect.TypeToken<List<OrderResponse>>() {}.type
                    com.google.gson.Gson().fromJson(json, type)
                } else {
                    Log.w("OrderRepository", "Received object instead of array: $trimmed")
                    emptyList()
                }
            }

            var response = apiService.getMyOrders()
            
            if (!response.isSuccessful || response.code() == 403 || response.code() == 404) {
                Log.w("OrderRepository", "getMyOrders failed, falling back to username-based endpoint")
                val userResponse = apiService.getUserOrders(username)
                if (userResponse.isSuccessful) {
                    val newOrders = parseBody(userResponse.body())
                    cachedUserOrders[username] = newOrders
                    return Result.Success(newOrders)
                }
            }

            if (response.isSuccessful) {
                val newOrders = parseBody(response.body())
                cachedUserOrders[username] = newOrders
                Log.d("OrderRepository", "Successfully fetched and updated ${newOrders.size} orders")
                return Result.Success(newOrders)
            }

            val errorMsg = "Failed to get user orders: HTTP ${response.code()}"
            Log.e("OrderRepository", errorMsg)
            Result.Error(Exception(errorMsg))

        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception fetching user orders", e)
            Result.Error(e)
        }
    }

    suspend fun getOrderById(orderId: Int): Result<OrderResponse> {
        return try {
            val response = apiService.getOrderById(orderId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get order by ID: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getAllOrders(
        forceRefresh: Boolean = false,
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        date: String? = null,
        search: String? = null
    ): Result<List<OrderResponse>> {
        if (cachedAllOrders != null && !forceRefresh && page == null && limit == null && status == null && date == null && search == null) {
            Log.d("OrderRepository", "Returning cached all orders.")
            return Result.Success(cachedAllOrders!!)
        }

        return try {
            Log.d("OrderRepository", "Fetching all orders (forceRefresh: $forceRefresh)")
            val response = apiService.getAllOrders(page, limit, status, date, search)
            if (response.isSuccessful) {
                val newOrders = response.body() ?: emptyList()
                if (page == null && limit == null && status == null && date == null && search == null) {
                    cachedAllOrders = newOrders
                }
                Result.Success(newOrders)
            } else {
                Result.Error(Exception("Failed to fetch orders: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): Result<OrderResponse> {
        return try {
            val response = apiService.updateOrder(orderId, UpdateOrderRequest(status))
            if (response.isSuccessful && response.body() != null) {
                clearCache()
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getOrdersByRole(roleName: String): Result<List<OrderResponse>> {
        return try {
            val response = apiService.getOrdersByRole(roleName)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get orders by role: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getDashboardSummary(): Result<DashboardSummaryDTO> {
        return try {
            val response = apiService.getDashboardSummary()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get dashboard summary: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getDashboardAnalytics(): Result<DashboardAnalyticsResponse> {
        return try {
            val response = apiService.getDashboardAnalytics()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get dashboard analytics: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
