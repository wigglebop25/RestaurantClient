package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token", alternate = ["access_token"])
    val token: String?,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    val message: String? = null,
    val user: UserDTO? = null
)
