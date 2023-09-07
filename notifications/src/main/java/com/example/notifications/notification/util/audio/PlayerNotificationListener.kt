package com.example.notifications.notification.util.audio

import android.app.Notification
import androidx.media3.ui.PlayerNotificationManager

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {
    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {

    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

    }
}