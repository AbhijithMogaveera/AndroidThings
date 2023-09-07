package com.example.notifications.notification.util.audio

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.notifications.R
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@UnstableApi
class DescriptionAdapter(
    private val controller: ListenableFuture<MediaController>,
    val context: Context,
    val scope: CoroutineScope
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        controller.get().sessionActivity

    override fun getCurrentContentText(player: Player) =
        ""

    override fun getCurrentContentTitle(player: Player) =
        controller.get().mediaMetadata.title.toString()

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        scope.launch {
            withContext(Dispatchers.IO) {
                controller.get()
            }.mediaMetadata.artworkUri?.let { resolveUriAsBitmap(it) }?.let(callback::onBitmap)
        }
        return null
    }

    private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                Glide.with(context).applyDefaultRequestOptions(
                    RequestOptions()
                        .fallback(R.drawable.baseline_music_note_24)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                )
                    .asBitmap()
                    .load(uri)
                    .submit(150, 150)
                    .get()

            } catch (e: Exception) {
                null
            }
        }
    }
}