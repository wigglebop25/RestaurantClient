package com.restaurantclient.data.dto

data class ProductRequest(
    val name: String,
    val description: String,
    val price: String,
    val product_image_uri: String,
    val categories: List<String> = emptyList()
)

