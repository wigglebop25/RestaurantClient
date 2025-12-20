package com.restaurantclient.data.repository

import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.DashboardSummaryDTO
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val apiService: ApiService
) {
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
}
