package com.restaurantclient.data.dto

data class CategoryDTO(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class CategoryRequest(
    val name: String,
    val description: String? = null
)

data class CategoryProductRequest(
    val category_id: Int,
    val product_id: Int
)
