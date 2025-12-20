package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateRolePermissionsRequest(
    @SerializedName("permissions")
    val permissions: List<String>
)
