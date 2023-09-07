package com.example.notifications.notification.channels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.channels.Channel

@RequiresApi(Build.VERSION_CODES.O)
object NotificationChannels {

    fun createSimpleNotificationChannel(context:Context): NotificationChannel {
        val channel = NotificationChannel(
            "simple_notification_channel",
            "Simple Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channel
    }

    fun createExpandableNotificationChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            "expandable_notification_channel",
            "Expandable Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channel
    }

    fun createMessagingNotificationChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            "messaging_notification_channel",
            "Messaging Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channel
    }

    fun createProgressNotificationChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
            "progress_notification_channel",
            "Progress Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channel
    }


}