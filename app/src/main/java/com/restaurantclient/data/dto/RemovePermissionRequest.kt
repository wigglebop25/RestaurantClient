package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName

data class RemovePermissionRequest(
    @SerializedName("permission")
    val permission: String
)
