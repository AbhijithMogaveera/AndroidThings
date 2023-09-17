package com.example.notifications.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.notifications.R
import com.example.notifications.notification.util.audio.PlayerAction
import com.example.notifications.notification.util.audio.audioPlayerManager

const val TAG = "MediaPlayerNotificationService"

class MediaPlayerNotificationService : Service(), Player.Listener {

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "com.example.notifications.ACTION_SHOW_NOTIFICATION"
        const val ACTION_HIDE_NOTIFICATION = "com.example.notifications.ACTION_HIDE_NOTIFICATION"
        const val ACTION_PLAY = "com.example.notifications.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.notifications.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.notifications.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.notifications.ACTION_PREVIOUS"
    }

    private val pendingIntentPlayPreviousSong by lazy {
        PendingIntent.getService(
            application,
            0,
            Intent(application, MediaPlayerNotificationService::class.java).apply {
                action = ACTION_PREVIOUS
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    private val pendingIntentPlayNext by lazy {
        PendingIntent.getService(
            application,
            0,
            Intent(application, MediaPlayerNotificationService::class.java).apply {
                action = ACTION_NEXT
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    private val pendingIntentPauseMusic by lazy {
        PendingIntent.getService(
            application,
            0,
            Intent(application, MediaPlayerNotificationService::class.java).apply {
                action = ACTION_PAUSE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    private val pendingIntentPlayMusic by lazy {
        PendingIntent.getService(
            application,
            0,
            Intent(application, MediaPlayerNotificationService::class.java).apply {
                action = ACTION_PLAY
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    private val NOTIFICATION_ID = 888
    private val CHANNEL_ID = "MediaPlayerNotificationService"
    private var isNotificationIsDisplaying: Boolean = false

    private val binder: IBinder = ServiceBinder()

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "Service onCreate")
    }

    private fun buildNotification(mediaItem: MediaItem?): Notification {

        Log.e("buildNotification", "Notificatoin Are  Built")
        val channelId = "custom_player_notification_channel"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Custom Player Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val contentView = RemoteViews(packageName, R.layout.player_notification_small)
        contentView.setImageViewResource(R.id.image, R.drawable.ic_notification)
        contentView.setTextViewText(
            R.id.title,
            mediaItem?.mediaMetadata?.title ?: "Custom notification"
        )
        contentView.setTextViewText(R.id.text, mediaItem?.mediaMetadata?.artist)

        contentView.setOnClickPendingIntent(
            R.id.btnSeekToNext, pendingIntentPlayNext
        )

        contentView.setOnClickPendingIntent(
            R.id.btnSeekToPrevious, pendingIntentPlayPreviousSong
        )
        if (audioPlayerManager.player.isPlaying) {
            contentView.setImageViewResource(R.id.btnPlayPause, R.drawable.ic_pause)
            contentView.setOnClickPendingIntent(R.id.btnPlayPause, pendingIntentPauseMusic)
        } else {
            contentView.setImageViewResource(R.id.btnPlayPause, R.drawable.ic_play)
            contentView.setOnClickPendingIntent(R.id.btnPlayPause, pendingIntentPlayMusic)
        }
        val mBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(contentView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()).apply {
                priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    NotificationManager.IMPORTANCE_HIGH
                } else {
                    Notification.PRIORITY_HIGH
                }
            }
            .setAutoCancel(true)

        return mBuilder.build()
    }

    inner class ServiceBinder : Binder() {
        val service: MediaPlayerNotificationService
            get() = this@MediaPlayerNotificationService
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Service onStartCommand " + intent?.action)
        intent?.let { intent ->
            when (intent.action) {
                ACTION_SHOW_NOTIFICATION -> {
                    if (!isNotificationIsDisplaying) {
                        isNotificationIsDisplaying = true
                        audioPlayerManager.player.addListener(this)
                        updateNotification(
                            audioPlayerManager.player.currentMediaItem
                        )
                        audioPlayerManager.player.getMediaItemAt(0)
                    }
                }

                ACTION_HIDE_NOTIFICATION -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    } else {
                        stopForeground(true)
                    }
                    audioPlayerManager.player.removeListener(this)
                }

                ACTION_NEXT -> {
                    audioPlayerManager.updatePlayer(PlayerAction.Next)
                }

                ACTION_PAUSE -> {
                    audioPlayerManager.updatePlayer(PlayerAction.Pause)
                }

                ACTION_PLAY -> {
                    audioPlayerManager.updatePlayer(PlayerAction.Play)
                }

                ACTION_PREVIOUS -> {
                    audioPlayerManager.updatePlayer(PlayerAction.Previous)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        updateNotification(
            audioPlayerManager.player.currentMediaItem,
        )
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        updateNotification(mediaItem)
        super.onMediaItemTransition(mediaItem, reason)

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        updateNotification(audioPlayerManager.player.currentMediaItem)
    }

    private fun updateNotification(mediaItem: MediaItem?) {
        startForeground(NOTIFICATION_ID, buildNotification(mediaItem))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}