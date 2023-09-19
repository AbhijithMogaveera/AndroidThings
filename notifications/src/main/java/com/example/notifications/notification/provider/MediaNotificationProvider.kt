package com.example.notifications.notification.provider

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.example.notifications.R
import com.example.notifications.notification.util.audio.audioPlayerManager
import com.example.notifications.services.PlaybackService
import com.google.common.collect.ImmutableList

val MediaNotificationProvider.audioPlayerManager get() = context.audioPlayerManager
@UnstableApi
class MediaNotificationProvider(
    val context: Context
) : DefaultMediaNotificationProvider(context) {

    override fun addNotificationActions(mediaSession: MediaSession, mediaButtons: ImmutableList<CommandButton>, builder: NotificationCompat.Builder, actionFactory: MediaNotification.ActionFactory): IntArray {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return super.addNotificationActions(mediaSession, mediaButtons, builder, actionFactory)

        val shuffleOnCommandButton = CommandButton
            .Builder()
            .setSessionCommand(SessionCommand(PlaybackService.ShuffleActions.SHUFFLE_MODE_ON.name, Bundle()))
            .setIconResId(R.drawable.baseline_shuffle_24)
            .setEnabled(true)
            .setDisplayName("shuffle on")
            .build()

        val shuffleOffCommandButton = CommandButton.Builder().setSessionCommand(
            SessionCommand(PlaybackService.ShuffleActions.SHUFFLE_MODE_OFF.name, Bundle()))
            .setIconResId(R.drawable.baseline_shuffle_on_24)
            .setEnabled(true)
            .setDisplayName("shuffle off")
            .build()

        val skipPreviousCommandButton = CommandButton.Builder().setPlayerCommand(
            Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
        )
            .setEnabled(mediaSession.player.hasPreviousMediaItem())
            .setIconResId(androidx.media3.ui.R.drawable.exo_ic_skip_previous)
            .setExtras(Bundle().apply {
                if(mediaSession.player.hasPreviousMediaItem())
                    putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, 0)
            })
            .build()

        val playCommandButton = CommandButton.Builder().setPlayerCommand(
            Player.COMMAND_PLAY_PAUSE
        )
            .setEnabled(true)
            .setIconResId(
                if(mediaSession.player.isPlaying)
                    androidx.media3.ui.R.drawable.exo_icon_pause
                else
                    androidx.media3.ui.R.drawable.exo_icon_play
            )
            .setExtras(Bundle().apply {
                if(mediaSession.player.hasPreviousMediaItem())
                    putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, 1)
                else
                    putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, 0)
            })
            .build()

        val skipNextCommandButton = CommandButton.Builder().setPlayerCommand(
            Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
        )
            .setEnabled(mediaSession.player.hasPreviousMediaItem())
            .setIconResId(androidx.media3.ui.R.drawable.exo_ic_skip_next)
            .setExtras(Bundle().apply {
                if(mediaSession.player.hasNextMediaItem())
                    if(mediaSession.player.hasPreviousMediaItem())
                        putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, 2)
                    else
                        putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, 1)
            })
            .build()

        val closeCommandButton = CommandButton.Builder().setSessionCommand(
            SessionCommand("action_close", Bundle())
        )
            .setIconResId(R.drawable.baseline_close_24)
            .setEnabled(true)
            .setDisplayName("close")
            .build()

        val mediaButtonsList = mutableListOf(
            if(audioPlayerManager.isInSufferMode)
                shuffleOffCommandButton
            else
                shuffleOnCommandButton ,
            skipPreviousCommandButton,
            playCommandButton,
            skipNextCommandButton,
            closeCommandButton
        )

        if(!mediaSession.player.hasNextMediaItem()){
            mediaButtonsList.removeAt(3)
        }
        if(!mediaSession.player.hasPreviousMediaItem()){
            mediaButtonsList.removeAt(1)
        }

        return super.addNotificationActions(mediaSession, ImmutableList.copyOf(mediaButtonsList), builder, actionFactory)
    }

}