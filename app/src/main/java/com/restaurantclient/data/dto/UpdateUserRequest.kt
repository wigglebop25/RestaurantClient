package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)
