package com.orderly.data.repository

import com.orderly.data.Result
import com.orderly.data.dto.RoleDTO
import com.orderly.data.dto.UserDTO
import com.orderly.data.network.ApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getCurrentUserInfo(): Result<UserDTO> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get current user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Fallback method to determine admin status
    suspend fun checkAdminStatus(): Result<Boolean> {
        return try {
            // Try to access admin endpoint - if successful, user is admin
            val response = apiService.getAllUsers()
            Result.Success(response.isSuccessful)
        } catch (e: Exception) {
            // If endpoint fails due to access denied, user is not admin
            Result.Success(false)
        }
    }
}