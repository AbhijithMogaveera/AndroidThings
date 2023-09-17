package com.example.notifications.notification.util.audio

import android.content.Context
import android.media.session.MediaSession
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import com.example.notifications.R
import kotlinx.coroutines.*

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun Context.createPlayerNotificationManager(
    sessionToken: SessionToken,
    player: Player,
    notificationListener: PlayerNotificationManager.NotificationListener
): PlayerNotificationManager {
    val serviceJob = SupervisorJob()
    val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    val mediaSession = MediaSession(this, "EchoNotification")
    mediaSession.isActive = true
    val mediaController = MediaController.Builder(this, sessionToken).buildAsync()
    return PlayerNotificationManager.Builder(
        this,
        999,
        "$packageName.audio_player"
    )
        .setChannelNameResourceId(R.string.media_notification_channel)
        .setMediaDescriptionAdapter(
            DescriptionAdapter(
                controller = mediaController,
                context = this,
                scope = serviceScope
            )
        )
        .setNotificationListener(notificationListener)
        .setSmallIconResourceId(R.drawable.baseline_headset_24)
        .build()
        .apply {
            setPlayer(player)
            setUseRewindAction(true)
            setUseFastForwardAction(true)
            setUseRewindActionInCompactView(true)
            setUseFastForwardActionInCompactView(true)
            setUseRewindActionInCompactView(true)
            setUseFastForwardActionInCompactView(true)
        }
}

