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

fun showSimpleNotification(context: Context, id: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannels.createSimpleNotificationChannel(context)
    }

    val builder = NotificationCompat.Builder(context, "simple_notification_channel")
        .setContentTitle("Simple Notification")
        .setContentText("This is a simple notification.")
        .setSmallIcon(R.drawable.ic_notification)

    val intent = Intent(context, NotificationActivity::class.java)
    intent.action = context.packageName + ".ACTION_VIEW"
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
    builder.setContentIntent(pendingIntent)
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(id, builder.build())
}