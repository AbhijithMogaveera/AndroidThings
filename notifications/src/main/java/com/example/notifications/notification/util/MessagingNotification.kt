package com.example.notifications.notification.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.example.notifications.NotificationActivity
import com.example.notifications.broadcast.KEY_TEXT_REPLY
import com.example.notifications.broadcast.ReplyBroadCastListener
import com.example.notifications.data.users.Group
import com.example.notifications.notification.channels.NotificationChannels


fun showMessagingNotification(context: Context, group: Group) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannels.createMessagingNotificationChannel(context)
    }

    val notificationBuilder =
        NotificationCompat.Builder(context, "messaging_notification_channel")
            .setSmallIcon(com.example.notifications.R.drawable.ic_notification)

    val androidGroupMessages =
        NotificationCompat.MessagingStyle(Person.Builder().setName(group.groupName).build())

    androidGroupMessages.conversationTitle = group.groupName

    group.messages.forEach {
        androidGroupMessages.addMessage(
            it.message,
            System.currentTimeMillis(),
            Person.Builder().setName(it.user.userName).build()
        )
    }
    val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
        .setLabel("Reply")
        .build()
    val replyPendingIntent = PendingIntent.getBroadcast(
        context,
        group.id,
        Intent(
            context,
            ReplyBroadCastListener::class.java
        ).apply {
            putExtra("id",group.id)
            putExtra("groupName",group.groupName)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )
    val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
        com.example.notifications.R.drawable.baseline_reply_24,
        "Reply",
        replyPendingIntent
    )
        .addRemoteInput(remoteInput)
        .build()
    notificationBuilder.setStyle(androidGroupMessages).addAction(action)
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

