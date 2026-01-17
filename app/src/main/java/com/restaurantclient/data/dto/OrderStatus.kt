package com.restaurantclient.data.dto

enum class OrderStatus(val value: String) {
    PENDING("pending"),
    PREPARING("preparing"),
    READY("ready"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    override fun toString(): String = value

    companion object {
        fun fromString(value: String): OrderStatus? {
            return values().find { it.value.equals(value, ignoreCase = true) }
        }
    }
}
