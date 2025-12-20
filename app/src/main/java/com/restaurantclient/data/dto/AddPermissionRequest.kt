package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class AddPermissionRequest(
    @SerializedName("role_name")
    val roleName: String,
    
    @SerializedName("permission")
    val permission: String
)
