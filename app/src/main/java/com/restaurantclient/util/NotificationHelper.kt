package com.restaurantclient.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.restaurantclient.R
import com.restaurantclient.ui.order.MyOrdersActivity

object NotificationHelper {

    private const val CHANNEL_ID = "order_updates_channel"
    private const val CHANNEL_NAME = "Order Updates"
    private const val CHANNEL_DESCRIPTION = "Notifications for order status changes"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showOrderStatusNotification(context: Context, orderId: Int, newStatus: String) {
        // Create intent to open MyOrdersActivity when notification is clicked
        val intent = Intent(context, MyOrdersActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_orders) // Make sure this icon exists or use ic_notification
            .setContentTitle("Order #$orderId Updated")
            .setContentText("Your order is now $newStatus")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                // Use orderId as notificationId to allow multiple distinct notifications
                notify(orderId, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle missing permission for Android 13+
            e.printStackTrace()
        }
    }
}
