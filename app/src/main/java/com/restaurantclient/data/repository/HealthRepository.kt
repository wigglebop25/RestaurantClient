package com.restaurantclient.data.repository

import com.restaurantclient.data.Result
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class HealthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun checkHealth(): Result<String> {
        return try {
            val response = apiService.healthCheck()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: "Server is running")
            } else {
                Result.Error(Exception("Health check failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
