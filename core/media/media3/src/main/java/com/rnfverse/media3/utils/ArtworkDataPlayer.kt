package com.rnfverse.media3.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.media3.common.FlagSet
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Wraps the player that is handed to the MediaSession so that the current song's cover is also
 * present as raw image bytes ([MediaMetadata.artworkData]), not only as a link.
 *
 * Some phone UIs (vivo Origin Island / lock-screen player) show a placeholder when the cover has
 * to be fetched from a link. With the bytes inside the metadata, the session and the notification
 * decode the cover straight away. The covers of the current and the next few songs are fetched in
 * the background (through Coil, so its caches are reused) and kept in a tiny in-memory cache.
 */
@UnstableApi
internal class ArtworkDataPlayer(
    player: Player,
    private val context: Context,
) : ForwardingPlayer(player) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val outerListeners = CopyOnWriteArrayList<Player.Listener>()
    private val cache =
        object : LinkedHashMap<String, ByteArray>(8, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ByteArray>?): Boolean = size > MAX_CACHED
        }
    private val inFlight = HashSet<String>()

    init {
        player.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int,
                ) = prefetchAhead()

                override fun onTimelineChanged(
                    timeline: Timeline,
                    reason: Int,
                ) = prefetchAhead()
            },
        )
    }

    override fun addListener(listener: Player.Listener) {
        outerListeners.add(listener)
        super.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        outerListeners.remove(listener)
        super.removeListener(listener)
    }

    override fun getMediaMetadata(): MediaMetadata = withArtwork(super.getMediaMetadata())

    private fun withArtwork(metadata: MediaMetadata): MediaMetadata {
        if (metadata.artworkData != null) return metadata
        val key = metadata.artworkUri?.toString() ?: return metadata
        val bytes = synchronized(cache) { cache[key] }
        if (bytes == null) {
            prefetch(key)
            return metadata
        }
        return metadata
            .buildUpon()
            .setArtworkData(bytes, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .build()
    }

    private fun prefetchAhead() {
        val inner = wrappedPlayer
        val count = inner.mediaItemCount
        val start = inner.currentMediaItemIndex
        if (count == 0 || start < 0) return
        for (i in start until minOf(count, start + AHEAD)) {
            inner
                .getMediaItemAt(i)
                .mediaMetadata.artworkUri
                ?.toString()
                ?.let { prefetch(it) }
        }
    }

    private fun prefetch(uri: String) {
        synchronized(cache) {
            if (cache.containsKey(uri) || !inFlight.add(uri)) return
        }
        scope.launch {
            val bytes = withContext(Dispatchers.IO) { runCatching { loadJpeg(uri) }.getOrNull() }
            synchronized(cache) {
                inFlight.remove(uri)
                if (bytes != null) cache[uri] = bytes
            }
            // The cover of the song that is playing right now arrived late: tell the session.
            if (bytes != null && wrappedPlayer.mediaMetadata.artworkUri?.toString() == uri) {
                notifyMetadataChanged()
            }
        }
    }

    private suspend fun loadJpeg(uri: String): ByteArray? {
        val result =
            context.imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .size(MAX_SIDE, MAX_SIDE)
                    .allowHardware(false)
                    .build(),
            )
        val bitmap = (result as? SuccessResult)?.image?.toBitmap() ?: return null
        val longest = maxOf(bitmap.width, bitmap.height)
        val scaled =
            if (longest > MAX_SIDE) {
                val k = MAX_SIDE.toFloat() / longest
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * k).toInt().coerceAtLeast(1),
                    (bitmap.height * k).toInt().coerceAtLeast(1),
                    true,
                )
            } else {
                bitmap
            }
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        return out.toByteArray()
    }

    private fun notifyMetadataChanged() {
        val metadata = mediaMetadata
        val events =
            Player.Events(
                FlagSet
                    .Builder()
                    .add(Player.EVENT_MEDIA_METADATA_CHANGED)
                    .build(),
            )
        outerListeners.forEach {
            it.onMediaMetadataChanged(metadata)
            it.onEvents(this, events)
        }
    }

    private companion object {
        const val MAX_SIDE = 400
        const val JPEG_QUALITY = 88
        const val MAX_CACHED = 8
        const val AHEAD = 3
    }
}
