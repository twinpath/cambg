package com.twinpath.cambg.feature.gallery.helper

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.text.format.DateUtils
import android.util.Log
import com.twinpath.cambg.feature.gallery.model.VideoItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cambg_settings")

object RecordedFilesHelper {

    private const val TAG = "RecordedFilesHelper"

    /**
     * Lists all recorded video files (.mp4) from:
     * 1. The public DCIM/CamBGRecord directory (default)
     * 2. The internal app storage directory ("context.filesDir/recordings")
     * 3. The custom SAF directory (if configured with content:// tree URI)
     * and extracts their duration, date, size, and resolution metadata.
     */
    fun getRecordedVideos(context: Context): List<VideoItem> {
        val videoItems = mutableListOf<VideoItem>()

        // 1. Check custom public directory name or SAF tree URI if configured
        var customPath = ""
        try {
            val CUSTOM_STORAGE_PATH = stringPreferencesKey("custom_storage_path")
            val prefs = runBlocking { context.dataStore.data.first() }
            customPath = prefs[CUSTOM_STORAGE_PATH] ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error reading custom storage path from DataStore", e)
        }

        // Handle custom SAF path if configured as content:// URI
        if (customPath.startsWith("content://")) {
            try {
                val treeUri = Uri.parse(customPath)
                val documentId = DocumentsContract.getTreeDocumentId(treeUri)
                val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)
                context.contentResolver.query(
                    childrenUri,
                    arrayOf(
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                        DocumentsContract.Document.COLUMN_SIZE
                    ),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                    val nameCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val modCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                    val sizeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)

                    while (cursor.moveToNext()) {
                        val name = cursor.getString(nameCol)
                        if (name.endsWith(".mp4", ignoreCase = true)) {
                            val docId = cursor.getString(idCol)
                            val lastMod = cursor.getLong(modCol)
                            val sizeBytes = cursor.getLong(sizeCol)
                            val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)

                            val item = extractVideoItem(
                                context = context,
                                filePath = fileUri.toString(),
                                displayName = name,
                                sizeBytesOverride = sizeBytes,
                                lastModifiedOverride = lastMod
                            )
                            videoItems.add(item)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error listing files from SAF directory: $customPath", e)
            }
        } else if (customPath.isNotBlank() && customPath != "CamBGRecord") {
            // Traditional custom path under DCIM
            val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val customPublicDir = File(dcimDir, customPath)
            if (customPublicDir.exists() && customPublicDir.isDirectory) {
                customPublicDir.listFiles()?.filter {
                    it.isFile && it.extension.equals("mp4", ignoreCase = true)
                }?.forEach { file ->
                    videoItems.add(extractVideoItem(context, file.absolutePath))
                }
            }
        }

        // 2. Check public DCIM/CamBGRecord directory
        val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val publicRecordingsDir = File(dcimDir, "CamBGRecord")
        if (publicRecordingsDir.exists() && publicRecordingsDir.isDirectory) {
            publicRecordingsDir.listFiles()?.filter {
                it.isFile && it.extension.equals("mp4", ignoreCase = true)
            }?.forEach { file ->
                videoItems.add(extractVideoItem(context, file.absolutePath))
            }
        }

        // 3. Check primary internal recordings directory
        val recordingsDir = File(context.filesDir, "recordings")
        if (recordingsDir.exists() && recordingsDir.isDirectory) {
            recordingsDir.listFiles()?.filter {
                it.isFile && it.extension.equals("mp4", ignoreCase = true)
            }?.forEach { file ->
                videoItems.add(extractVideoItem(context, file.absolutePath))
            }
        }

        // 4. Check main internal files directory for any orphan mp4 files
        context.filesDir.listFiles()?.filter {
            it.isFile && it.extension.equals("mp4", ignoreCase = true)
        }?.forEach { file ->
            videoItems.add(extractVideoItem(context, file.absolutePath))
        }

        // Deduplicate by file path and sort descending by last modified/timestamp
        val uniqueSorted = videoItems.distinctBy { it.filePath }
            .sortedByDescending { it.timestamp }

        Log.d(TAG, "Found ${uniqueSorted.size} video files")
        return uniqueSorted
    }

    /**
     * Extracts metadata (Duration, Resolution, File Size, Last Modified Date)
     * from a video file path or Uri using MediaMetadataRetriever.
     */
    fun extractVideoItem(
        context: Context,
        filePath: String,
        displayName: String? = null,
        sizeBytesOverride: Long? = null,
        lastModifiedOverride: Long? = null
    ): VideoItem {
        var durationMs = 0L
        var resolution = "1080p"
        val retriever = MediaMetadataRetriever()

        try {
            if (filePath.startsWith("content://")) {
                retriever.setDataSource(context, Uri.parse(filePath))
            } else {
                retriever.setDataSource(filePath)
            }

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (durationStr != null) {
                durationMs = durationStr.toLongOrNull() ?: 0L
            }

            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)

            val height = heightStr?.toIntOrNull() ?: 0
            val width = widthStr?.toIntOrNull() ?: 0
            val maxDim = maxOf(height, width)

            resolution = when {
                maxDim >= 3840 || (height >= 2160 || width >= 2160) -> "4K"
                maxDim >= 1920 || (height >= 1080 || width >= 1080) -> "1080p"
                maxDim >= 1280 || (height >= 720 || width >= 720) -> "720p"
                maxDim > 0 -> "480p"
                else -> "1080p"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting metadata for video: $filePath", e)
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }

        val totalSeconds = durationMs / 1000
        val durationFormatted = formatDuration(totalSeconds)

        val sizeBytes = sizeBytesOverride ?: run {
            if (filePath.startsWith("content://")) {
                try {
                    context.contentResolver.openFileDescriptor(Uri.parse(filePath), "r")?.use {
                        it.statSize
                    } ?: 0L
                } catch (_: Exception) { 0L }
            } else {
                File(filePath).length()
            }
        }

        val sizeMb = sizeBytes / (1024.0 * 1024.0)
        val formattedSizeMb = String.format(Locale.US, "%.1f", sizeMb).toDoubleOrNull() ?: sizeMb

        val lastModified = lastModifiedOverride ?: run {
            if (filePath.startsWith("content://")) {
                System.currentTimeMillis()
            } else {
                File(filePath).lastModified()
            }
        }
        val dateText = formatDateText(lastModified)

        val name = displayName ?: run {
            if (filePath.startsWith("content://")) {
                Uri.parse(filePath).lastPathSegment ?: "video.mp4"
            } else {
                File(filePath).name
            }
        }

        // Generate consistent gradient colors based on filename hash
        val colorPalettes = listOf(
            listOf(0xFF1A73E8, 0xFF004BA0),
            listOf(0xFF00897B, 0xFF004D40),
            listOf(0xFFF9AB00, 0xFFB26A00),
            listOf(0xFFEA4335, 0xFFB31412),
            listOf(0xFF673AB7, 0xFF311B92),
            listOf(0xFF009688, 0xFF004D40)
        )
        val paletteIndex = kotlin.math.abs(name.hashCode()) % colorPalettes.size
        val cameraType = if (name.contains("FRONT", ignoreCase = true)) "Front Camera" else "Back Camera"

        return VideoItem(
            id = filePath,
            title = name,
            duration = durationFormatted,
            resolution = resolution,
            dateText = dateText,
            timestamp = lastModified,
            cameraType = cameraType,
            sizeMb = formattedSizeMb,
            gradientColors = colorPalettes[paletteIndex],
            filePath = filePath
        )
    }

    /**
     * Compatibility helper that loads file based on single File param.
     */
    fun extractVideoItem(file: File): VideoItem {
        // Fallback that doesn't have Context — only used for legacy signatures.
        // We simulate a generic call.
        var durationMs = 0L
        var resolution = "1080p"
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (durationStr != null) {
                durationMs = durationStr.toLongOrNull() ?: 0L
            }
            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val height = heightStr?.toIntOrNull() ?: 0
            val width = widthStr?.toIntOrNull() ?: 0
            val maxDim = maxOf(height, width)
            resolution = when {
                maxDim >= 3840 || (height >= 2160 || width >= 2160) -> "4K"
                maxDim >= 1920 || (height >= 1080 || width >= 1080) -> "1080p"
                maxDim >= 1280 || (height >= 720 || width >= 720) -> "720p"
                else -> "1080p"
            }
        } catch (_: Exception) {
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }

        val totalSeconds = durationMs / 1000
        val sizeBytes = file.length()
        val sizeMb = sizeBytes / (1024.0 * 1024.0)
        val formattedSizeMb = String.format(Locale.US, "%.1f", sizeMb).toDoubleOrNull() ?: sizeMb
        val lastModified = file.lastModified()
        val cameraType = if (file.name.contains("FRONT", ignoreCase = true)) "Front Camera" else "Back Camera"

        return VideoItem(
            id = file.absolutePath,
            title = file.name,
            duration = formatDuration(totalSeconds),
            resolution = resolution,
            dateText = formatDateText(lastModified),
            timestamp = lastModified,
            cameraType = cameraType,
            sizeMb = formattedSizeMb,
            gradientColors = listOf(0xFF1A73E8, 0xFF004BA0),
            filePath = file.absolutePath
        )
    }

    /**
     * Deletes a recorded video file from internal storage or SAF given its file path/Uri,
     * and also removes its cached thumbnail if present.
     */
    fun deleteRecordedFile(context: Context, filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) return false
        try {
            val name = if (filePath.startsWith("content://")) {
                Uri.parse(filePath).lastPathSegment ?: "video.mp4"
            } else {
                File(filePath).name
            }
            val cacheDir = File(context.cacheDir, "video_thumbnails")
            val nameWithoutExtension = name.substringBeforeLast(".")
            val thumbFile = File(cacheDir, "${nameWithoutExtension}.jpg")
            if (thumbFile.exists()) {
                val thumbDeleted = thumbFile.delete()
                Log.d(TAG, "Deleted cached thumbnail for $filePath: $thumbDeleted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete cached thumbnail for $filePath", e)
        }

        return try {
            if (filePath.startsWith("content://")) {
                val deleted = DocumentsContract.deleteDocument(context.contentResolver, Uri.parse(filePath))
                Log.d(TAG, "Deleted SAF document $filePath: $deleted")
                deleted
            } else {
                val file = File(filePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d(TAG, "Deleted file $filePath: $deleted")
                    deleted
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete file $filePath", e)
            false
        }
    }

    private fun formatDuration(totalSeconds: Long): String {
        if (totalSeconds <= 0) return "00:01"
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val hours = minutes / 60
        val remMinutes = minutes % 60
        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, remMinutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", remMinutes, seconds)
        }
    }

    private fun formatDateText(timestamp: Long): String {
        if (timestamp <= 0) return "Just now"
        val date = Date(timestamp)
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp

        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val timeString = timeFormat.format(date)

        val oneDayMs = 86400000L
        return when {
            DateUtils.isToday(timestamp) -> "Today, $timeString"
            diffMs in oneDayMs..(2 * oneDayMs) -> "Yesterday, $timeString"
            else -> {
                val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.US)
                dateFormat.format(date)
            }
        }
    }
}
