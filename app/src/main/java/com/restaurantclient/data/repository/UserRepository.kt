package com.restaurantclient.data.repository

import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.AssignRoleRequest
import com.restaurantclient.data.dto.CreateUserRequest
import com.restaurantclient.data.dto.LoginDTO
import com.restaurantclient.data.dto.LoginResponse
import com.restaurantclient.data.dto.NewUserDTO
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(loginDto: LoginDTO): Result<LoginResponse> {
        return try {
            val response = apiService.login(loginDto)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun register(newUserDto: NewUserDTO): Result<LoginResponse> {
        return try {
            val response = apiService.register(newUserDto)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to register: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun refreshToken(): Result<LoginResponse> {
        return try {
            val response = apiService.refreshToken()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to refresh token: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Admin User Management Methods
    suspend fun getAllUsers(): Result<List<UserDTO>> {
        return try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get users: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserById(userId: Int): Result<UserDTO> {
        return try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to get user by ID: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun createUser(username: String, password: String, role: RoleDTO): Result<Unit> {
        return try {
            val roleString = when (role) {
                RoleDTO.Admin -> "Admin"
                RoleDTO.Customer -> "Customer"
            }
            val createUserRequest = CreateUserRequest(username, password, roleString)
            val response = apiService.createUser(createUserRequest)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to create user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateUser(userId: Int, userDTO: UserDTO): Result<UserDTO> {
        return try {
            val response = apiService.updateUser(userId, userDTO)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteUser(userId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to delete user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun assignRole(username: String, role: RoleDTO): Result<Unit> {
        return try {
            val roleName = when (role) {
                RoleDTO.Admin -> "ADMIN"
                RoleDTO.Customer -> "CUSTOMER"
            }
            val response = apiService.assignRole(AssignRoleRequest(username, roleName))
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to assign role: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
