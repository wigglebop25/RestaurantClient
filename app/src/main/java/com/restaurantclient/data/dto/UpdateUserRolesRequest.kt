package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserRolesRequest(
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("role_names")
    val roleNames: List<String>
)
