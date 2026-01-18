package com.restaurantclient.data.dto

enum class RoleDTO {
    Admin,
    Customer,
    Cashier;

    companion object {
        fun fromString(role: String): RoleDTO? {
            return when (role.lowercase()) {
                "admin" -> Admin
                "customer" -> Customer
                "cashier", "casher" -> Cashier
                else -> null
            }
        }
    }
}
