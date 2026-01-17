package com.restaurantclient.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.LocalDateTime
import java.util.Locale

object DateTimeUtils {
    private val manilaZone = ZoneId.of("Asia/Manila")
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    fun formatIsoDate(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return ""
        
        // If the date is already human readable (e.g. "January 17, 2026..."), return it directly
        // The API Guide says: "You can display this string directly to the user."
        if (isoDate.any { it.isLetter() } && isoDate.contains(",")) {
            return isoDate
        }

        return try {
            val zonedDateTime = when {
                isoDate.contains("+") || isoDate.contains("Z") -> {
                    // Has offset or Zulu
                    ZonedDateTime.parse(isoDate)
                }
                isoDate.contains("T") -> {
                    // ISO Local with T, assume Manila
                    val localDateTime = LocalDateTime.parse(isoDate.substringBefore("."))
                    localDateTime.atZone(manilaZone)
                }
                else -> {
                    // Space separated, assume Manila
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val localDateTime = LocalDateTime.parse(isoDate.substringBefore("."), formatter)
                    localDateTime.atZone(manilaZone)
                }
            }
            
            // Display in Manila time as requested by checklist ("display it directly")
            zonedDateTime.withZoneSameInstant(manilaZone).format(displayFormatter)
        } catch (e: Exception) {
            isoDate
        }
    }
}
