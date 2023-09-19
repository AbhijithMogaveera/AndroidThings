package com.example.notifications.services

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.common.util.BitmapLoader
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.bumptech.glide.Glide
import com.example.notifications.NotificationActivity
import com.example.notifications.R
import com.example.notifications.notification.provider.MediaNotificationProvider
import com.example.notifications.notification.util.audio.PlayerAction
import com.example.notifications.notification.util.audio.audioPlayerManager
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListenableFutureTask

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlaybackService : MediaSessionService(), MediaSession.Callback, BitmapLoader {
    private var mediaSession: MediaSession? = null
    private var commandButtons = listOf<CommandButton>()

    private var customCommands = listOf(
        CommandButton.Builder()
            .setSessionCommand(
            SessionCommand(ShuffleActions.SHUFFLE_MODE_ON.name, Bundle()))
            .setIconResId(R.drawable.baseline_shuffle_24)
            .setEnabled(true)
            .setDisplayName("shuffle on")
            .build(),
        CommandButton.Builder()
            .setSessionCommand(SessionCommand(ShuffleActions.SHUFFLE_MODE_OFF.name, Bundle()))
            .setIconResId(R.drawable.baseline_shuffle_on_24)
            .setEnabled(true)
            .setDisplayName("shuffle off")
            .build(),
        CommandButton.Builder()
            .setSessionCommand(SessionCommand("action_close", Bundle()))
            .setIconResId(R.drawable.baseline_close_24)
            .setEnabled(true)
            .setDisplayName("close")
            .build()
    )

    enum class ShuffleActions {
        SHUFFLE_MODE_ON,
        SHUFFLE_MODE_OFF
    }

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        if (session.player.playbackState == Player.STATE_BUFFERING)
            return
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    override fun onCreate() {
        this.setMediaNotificationProvider(MediaNotificationProvider(this))
        val openIntent = Intent(this, NotificationActivity::class.java)
        val pOpenIntent =
            PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE)
        mediaSession = MediaSession.Builder(this, audioPlayerManager.player)
            .setSessionActivity(pOpenIntent)
            .setCallback(this)
            .setBitmapLoader(this)
            .setId("randomid2")
            .build()

        commandButtons = if (audioPlayerManager.isInSufferMode) {
            listOf(customCommands[1], customCommands[2])
        } else {
            listOf(customCommands[0], customCommands[2])
        }
        mediaSession?.setCustomLayout(commandButtons)
        super.onCreate()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
        customCommands.forEach { commandButton ->
            commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
        }
        return MediaSession.ConnectionResult.accept(
            availableSessionCommands.build(),
            connectionResult.availablePlayerCommands
        )
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when {
            ShuffleActions.SHUFFLE_MODE_ON.name == customCommand.customAction -> {
                commandButtons = listOf(customCommands[1], customCommands[2])
                session.setCustomLayout(commandButtons)
                audioPlayerManager.updatePlayer(PlayerAction.Shuffle)
            }
            ShuffleActions.SHUFFLE_MODE_OFF.name == customCommand.customAction -> {
                commandButtons = listOf(customCommands[0], customCommands[2])
                session.setCustomLayout(commandButtons)
                audioPlayerManager.updatePlayer(PlayerAction.NoShuffle)
            }
            customCommand.customAction == "action_close" -> {
                stopService()
            }
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        if (commandButtons.isNotEmpty()) {
            mediaSession?.setCustomLayout(controller, commandButtons)
        }
    }

    private fun stopService() {
        audioPlayerManager.updatePlayer(PlayerAction.Pause)
        mediaSession?.release()
    }

    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
        return ListenableFutureTask.create{
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            bitmap
        }.apply {
            run()
        }
    }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> {
        return ListenableFutureTask.create {
            Glide.with(applicationContext).asBitmap().load(uri).submit(170, 170).get()
        }.apply {
            run()
        }
    }

}
