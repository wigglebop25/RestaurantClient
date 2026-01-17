package com.restaurantclient.util

import com.google.gson.Gson
import com.restaurantclient.data.dto.ErrorResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {

    fun getHumanFriendlyErrorMessage(exception: Exception): String {
        return when (exception) {
            is HttpException -> {
                try {
                    val errorBody = exception.response()?.errorBody()?.string()
                    if (!errorBody.isNullOrBlank()) {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        val error = errorResponse.error.replaceFirstChar { it.uppercase() }
                        val details = errorResponse.details
                        
                        if (!details.isNullOrBlank()) {
                            "$error: $details"
                        } else {
                            error
                        }
                    } else {
                        getDefaultHttpErrorMessage(exception.code())
                    }
                } catch (e: Exception) {
                    getDefaultHttpErrorMessage(exception.code())
                }
            }
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "The connection timed out. Please try again."
            is IOException -> "Network error. Please check your internet connection and try again."
            else -> {
                val message = exception.message ?: ""
                when {
                    message.contains("400") -> "Invalid request. Please check your data."
                    message.contains("401") -> "Unauthorized access. Please login again."
                    message.contains("403") -> "Access denied. You don't have enough permissions."
                    message.contains("404") -> "Resource not found. Please try again."
                    message.contains("500") -> "Internal server error. Our team has been notified."
                    message.contains("Failed to connect") -> "Could not connect to the server. Please check if you're online."
                    else -> "An unexpected error occurred. Please try again."
                }
            }
        }
    }

    private fun getDefaultHttpErrorMessage(code: Int): String {
        return when (code) {
            400 -> "Invalid request. Please check your information and try again."
            401 -> "Authentication failed. Please check your credentials."
            403 -> "You don't have permission to perform this action."
            404 -> "The requested resource was not found. Please check your connection or contact support."
            409 -> "There's a conflict with the current state of the resource. It might already exist."
            422 -> "Validation failed. Please check your input."
            429 -> "Too many requests. Please slow down."
            500 -> "The server is having some trouble. Please try again later."
            503 -> "Service is temporarily unavailable. Our team is working on it."
            else -> "Something went wrong on our end (Error $code)."
        }
    }
}
