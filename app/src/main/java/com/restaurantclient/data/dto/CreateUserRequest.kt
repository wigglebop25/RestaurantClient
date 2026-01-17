package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    val username: String,
    val password: String,
    @SerializedName("role_name")
    val roleName: String
)
