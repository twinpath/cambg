package com.twinpath.cambg.feature.gallery.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ThumbnailLoader {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    // Use 1/8th of the available memory for this cache
    private val cacheSize = maxMemory / 8

    private val cache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }

    /**
     * Retrieves a thumbnail bitmap for the specified video file path.
     * Checks the memory cache first, then disk cache, and extracts it using MediaMetadataRetriever if not cached.
     */
    suspend fun getThumbnail(context: Context, filePath: String): Bitmap? = withContext(Dispatchers.IO) {
        val cached = cache.get(filePath)
        if (cached != null) {
            return@withContext cached
        }

        val file = File(filePath)
        if (!file.exists()) {
            return@withContext null
        }

        // Check disk cache first
        val cacheDir = File(context.cacheDir, "video_thumbnails")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val thumbFile = File(cacheDir, "${file.nameWithoutExtension}.jpg")
        if (thumbFile.exists()) {
            try {
                val bitmap = BitmapFactory.decodeFile(thumbFile.absolutePath)
                if (bitmap != null) {
                    cache.put(filePath, bitmap)
                    return@withContext bitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            // Retrieve frame at 1 second mark (1000000 microseconds)
            val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime // Fallback to first frame if 1s fails
            
            if (bitmap != null) {
                // Resize bitmap if it's too large to save memory
                val scaledBitmap = scaleBitmapIfNeeded(bitmap, 320, 240)
                cache.put(filePath, scaledBitmap)
                
                // Save to disk cache
                try {
                    FileOutputStream(thumbFile).use { out ->
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@withContext scaledBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever?.release()
            } catch (e: Exception) {
                // Ignore release exceptions
            }
        }
        return@withContext null
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
}
