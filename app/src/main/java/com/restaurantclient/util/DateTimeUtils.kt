package com.restaurantclient.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val isoFormatWithoutT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val displayFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    fun formatIsoDate(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return ""
        
        return try {
            val date = if (isoDate.contains("T")) {
                isoFormat.parse(isoDate.substringBefore("."))
            } else {
                isoFormatWithoutT.parse(isoDate.substringBefore("."))
            }
            
            if (date != null) {
                displayFormat.format(date)
            } else {
                isoDate
            }
        } catch (e: Exception) {
            isoDate
        }
    }
}
