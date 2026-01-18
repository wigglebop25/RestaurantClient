package com.restaurantclient.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoleDetailsDTO(
    @SerializedName("role_id")
    val roleId: Int,
    
    @SerializedName("name")
    val name: String, // "ADMIN" or "CUSTOMER"
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("permissions")
    val permissions: String?, // Comma-separated string, e.g. "READ,WRITE"
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
) : Serializable {
    // Convert backend role to our app's RoleDTO
    fun toRoleDTO(): RoleDTO? {
        return when (name.uppercase()) {
            "ADMIN" -> RoleDTO.Admin
            "CUSTOMER" -> RoleDTO.Customer
            "USER" -> RoleDTO.Customer // Fallback
            "CASHIER" -> RoleDTO.Cashier
            "CASHER" -> RoleDTO.Cashier // Legacy support
            else -> null
        }
    }
}
