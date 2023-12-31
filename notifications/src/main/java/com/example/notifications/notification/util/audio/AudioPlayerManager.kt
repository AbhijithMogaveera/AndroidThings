package com.example.notifications.notification.util.audio

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.media3.common.*
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.example.notifications.data.songs.audios
import kotlinx.coroutines.flow.*

val Fragment.audioPlayerManager get() = AudioPlayerManager.getInstance(requireActivity().application)
val Activity.audioPlayerManager get() = AudioPlayerManager.getInstance(application)
val Context.audioPlayerManager get() = AudioPlayerManager.getInstance(this.applicationContext as Application)
val Service.audioPlayerManager get() = AudioPlayerManager.getInstance(application)


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class AudioPlayerManager private constructor(
    val application: Application
) {

    var isSessionActive = false

    var isInSufferMode = false
    val player: ExoPlayer = run {
        ExoPlayer.Builder(application).build().also { player ->
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()
            player.setAudioAttributes(audioAttributes, true)
            player.repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    init {
        preparePlayList(player = player)

    }


    fun updatePlayer(action: PlayerAction) {
        when (action) {
            PlayerAction.Play ->  {
                player.playWhenReady = true
                player.play()
            }
            PlayerAction.Next -> player.seekToNextMediaItem()
            PlayerAction.Previous -> player.seekToPreviousMediaItem()
            PlayerAction.Pause -> player.pause()
            PlayerAction.Shuffle-> {
                preparePlayList(player, true)
            }

            PlayerAction.NoShuffle -> {
                preparePlayList(player, false)
            }
        }
    }


    companion object {
        const val SESSION_INTENT_REQUEST_CODE = 0
        private lateinit var audioPlayerManager: AudioPlayerManager
        fun getInstance(application: Application): AudioPlayerManager {
            if (this::audioPlayerManager.isInitialized) {
                return audioPlayerManager
            }
            audioPlayerManager = AudioPlayerManager(application)
            return audioPlayerManager
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun AudioPlayerManager.preparePlayList(
    player: ExoPlayer,
    shuffled:Boolean = false
) {

    val videoItems: ArrayList<MediaSource> = arrayListOf()
    audios.forEach {

        val mediaMetaData = MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(it.thumbUrl))
            .setTitle(it.title)
            .setAlbumArtist(it.artistName)
            
            .build()

        val trackUri = Uri.parse(it.url)
        val mediaItem = MediaItem.Builder()
            .setUri(trackUri)
            .setMediaId(it.resId)
            .setMediaMetadata(mediaMetaData)
            .build()
        val dataSourceFactory = DefaultDataSource.Factory(application)

        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        videoItems.add(
            mediaSource
        )
    }


    player.playWhenReady = false
    player.setMediaSources(videoItems.let {
        if(shuffled) it.shuffled() else it
    })
    player.prepare()
}

@SuppressLint("UnsafeOptInUsageError")
fun AudioPlayerManager.prepareM3Notification(
    onPrepared: (
        mediaSession: MediaSession,
        manager: PlayerNotificationManager
    ) -> Unit,
    playerNotificationListener: PlayerNotificationManager.NotificationListener,
) {
    val sessionActivityPendingIntent =
        application.packageManager?.getLaunchIntentForPackage(application.packageName)
            ?.let { sessionIntent ->
                PendingIntent.getActivity(
                    application,
                    AudioPlayerManager.SESSION_INTENT_REQUEST_CODE,
                    sessionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

    val mediaSession: MediaSession =
        MediaSession.Builder(application, player)
            .setSessionActivity(sessionActivityPendingIntent!!)
            .build()
    isSessionActive = true
    application.createPlayerNotificationManager(
        mediaSession.token,
        player,
        playerNotificationListener
    ).apply {
        setPlayer(player).apply {
            player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_BUFFERING, Player.STATE_READY -> {
                                setPlayer(player)
                            }

                            else -> {
                                setPlayer(null)
                            }
                        }
                    }
                }
            )
        }
        onPrepared(mediaSession, this)
    }
}
