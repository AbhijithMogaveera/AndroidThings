package com.example.notifications.notification.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.notifications.NotificationActivity
import com.example.notifications.R
import com.example.notifications.notification.channels.NotificationChannels

fun showProcessNotification(progress: Int, id: Int, context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannels.createProgressNotificationChannel(context)
    }

    val builder = NotificationCompat.Builder(context, "progress_notification_channel")
        .setContentTitle("Progress Notification")
        .setContentText("Downloading file...")
        .setSmallIcon(R.drawable.ic_notification)
        .setProgress(100, progress, false)

    val intent = Intent(context, NotificationActivity::class.java)
    val pendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    builder.setContentIntent(pendingIntent)

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(id, builder.build())
}

