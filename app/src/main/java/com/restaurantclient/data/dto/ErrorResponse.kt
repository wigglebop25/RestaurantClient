package com.restaurantclient.data.dto

data class ErrorResponse(
    val error: String,
    val details: String? = null
)
