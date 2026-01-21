package com.restaurantclient.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.lang.ref.WeakReference

object ToastManager {

    private var currentToast: WeakReference<Toast>? = null
    private var lastMessage: String? = null
    private var lastToastTime: Long = 0
    private const val DUPLICATE_THRESHOLD_MS = 2000 // 2 seconds

    /**
     * Shows a toast message. Cancels any currently visible toast to prevent stacking.
     * Prevents showing the exact same message twice within a short duration.
     *
     * @param context The context to use.
     * @param message The text to show.
     * @param duration Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val mainHandler = Handler(Looper.getMainLooper())
        
        mainHandler.post {
            val currentTime = System.currentTimeMillis()

            // Check for duplicates
            if (message == lastMessage && (currentTime - lastToastTime) < DUPLICATE_THRESHOLD_MS) {
                return@post // Skip duplicate
            }

            // Cancel previous toast if it exists
            currentToast?.get()?.cancel()

            // Create and show new toast
            val toast = Toast.makeText(context.applicationContext, message, duration)
            toast.show()

            // Update state
            currentToast = WeakReference(toast)
            lastMessage = message
            lastToastTime = currentTime
        }
    }

    /**
     * Shows a toast using a string resource ID.
     */
    fun showToast(context: Context, resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        showToast(context, context.getString(resId), duration)
    }
}
