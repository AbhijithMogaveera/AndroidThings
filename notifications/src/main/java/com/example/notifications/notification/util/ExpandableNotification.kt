package com.example.notifications.notification.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.notifications.NotificationActivity
import com.example.notifications.R
import com.example.notifications.notification.channels.NotificationChannels

fun showExpandableNotification(context: Context, id: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannels.createExpandableNotificationChannel(context)
    }

    val builder = NotificationCompat.Builder(context, "expandable_notification_channel")
        .setContentTitle("Expandable Notification")
        .setContentText("This is an expandable notification.")
        .setSmallIcon(R.drawable.ic_notification)

    val inboxStyle = NotificationCompat.InboxStyle()
    inboxStyle.setSummaryText("Summary Text")
    inboxStyle.addLine("Line 1")
    inboxStyle.addLine("Line 2")
    inboxStyle.addLine("Line 3")

    builder.setStyle(inboxStyle)

    val intent = Intent(context, NotificationActivity::class.java)
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
