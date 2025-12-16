package com.restaurantclient.data.repository

import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.CategoryDTO
import com.restaurantclient.data.dto.CategoryProductRequest
import com.restaurantclient.data.dto.CategoryRequest
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.network.ApiService
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllCategories(): Result<List<CategoryDTO>> {
        return try {
            val response = apiService.getAllCategories()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get categories: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun createCategory(name: String, description: String?): Result<CategoryDTO> {
        return try {
            val request = CategoryRequest(name, description)
            val response = apiService.createCategory(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to create category: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateCategory(categoryId: Int, name: String, description: String?): Result<CategoryDTO> {
        return try {
            val request = CategoryRequest(name, description)
            val response = apiService.updateCategory(categoryId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update category: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteCategory(categoryId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to delete category: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun addProductToCategory(categoryId: Int, productId: Int): Result<Unit> {
        return try {
            val request = CategoryProductRequest(categoryId, productId)
            val response = apiService.addProductToCategory(request)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to add product to category: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun removeProductFromCategory(categoryId: Int, productId: Int): Result<Unit> {
        return try {
            val request = CategoryProductRequest(categoryId, productId)
            val response = apiService.removeProductFromCategory(request)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to remove product from category: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getProductsByCategory(categoryId: Int): Result<List<ProductResponse>> {
        return try {
            val response = apiService.getProductsByCategory(categoryId)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(Exception("Failed to get products by category: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
