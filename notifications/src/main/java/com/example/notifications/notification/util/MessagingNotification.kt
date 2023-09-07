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
import androidx.core.app.Person
import com.example.notifications.NotificationActivity
import com.example.notifications.R
import com.example.notifications.data.users.Group
import com.example.notifications.notification.channels.NotificationChannels

fun showMessagingNotification(context: Context, group: Group) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannels.createMessagingNotificationChannel(context)
    }

    val notificationBuilder =
        NotificationCompat.Builder(context, "messaging_notification_channel")
            .setSmallIcon(R.drawable.ic_notification)

    val androidGroupMessages =
        NotificationCompat.MessagingStyle(Person.Builder().setName(group.groupName).build())

    androidGroupMessages.conversationTitle = group.groupName

    group.message.forEach {
        androidGroupMessages.addMessage(
            it.message,
            System.currentTimeMillis(),
            Person.Builder().setName(it.user.userName).build()
        )
    }

    notificationBuilder.setStyle(androidGroupMessages)
    val intent = Intent(context, NotificationActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
    notificationBuilder.setContentIntent(pendingIntent)
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(group.id, notificationBuilder.build())
}

