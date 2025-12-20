package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class AddRolesRequest(
    @SerializedName("role_names")
    val roleNames: List<String>
)
