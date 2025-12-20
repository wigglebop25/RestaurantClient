package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class AssignMultipleRolesRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("role_names")
    val roleNames: List<String>
)
