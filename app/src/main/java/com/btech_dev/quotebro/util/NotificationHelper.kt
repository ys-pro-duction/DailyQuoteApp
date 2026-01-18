package com.btech_dev.quotebro.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.btech_dev.quotebro.R
import com.btech_dev.quotebro.data.model.Quote

object NotificationHelper {
    private const val CHANNEL_ID = "daily_quote_channel"
    private const val CHANNEL_NAME = "Daily Quote"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Channel for daily quote notifications"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showQuoteNotification(context: Context, quote: Quote) {
        createNotificationChannel(context)

        try {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Daily Inspiration")
                .setContentText(quote.content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("${quote.content}\n- ${quote.author}")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            try {
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Permission denied
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
