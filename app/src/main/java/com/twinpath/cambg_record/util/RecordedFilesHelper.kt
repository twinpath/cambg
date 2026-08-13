package com.twinpath.cambg_record.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.text.format.DateUtils
import android.util.Log
import com.twinpath.cambg_record.model.VideoItem
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
     * and extracts their duration, date, size, and resolution metadata.
     */
    fun getRecordedVideos(context: Context): List<VideoItem> {
        val files = mutableListOf<File>()

        // 1. Check public DCIM/CamBGRecord directory
        val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val publicRecordingsDir = File(dcimDir, "CamBGRecord")
        if (publicRecordingsDir.exists() && publicRecordingsDir.isDirectory) {
            publicRecordingsDir.listFiles()?.filter {
                it.isFile && it.extension.equals("mp4", ignoreCase = true)
            }?.let {
                files.addAll(it)
            }
        }

        // 1b. Check custom public directory name if configured
        var customPath = "CamBGRecord"
        try {
            val CUSTOM_STORAGE_PATH = stringPreferencesKey("custom_storage_path")
            val prefs = runBlocking { context.dataStore.data.first() }
            customPath = prefs[CUSTOM_STORAGE_PATH] ?: "CamBGRecord"
        } catch (e: Exception) {
            Log.e(TAG, "Error reading custom storage path from DataStore", e)
        }

        if (customPath.isNotBlank() && customPath != "CamBGRecord") {
            val customPublicDir = File(dcimDir, customPath)
            if (customPublicDir.exists() && customPublicDir.isDirectory) {
                customPublicDir.listFiles()?.filter {
                    it.isFile && it.extension.equals("mp4", ignoreCase = true)
                }?.let {
                    files.addAll(it)
                }
            }
        }

        // 2. Check primary internal recordings directory
        val recordingsDir = File(context.filesDir, "recordings")
        if (recordingsDir.exists() && recordingsDir.isDirectory) {
            recordingsDir.listFiles()?.filter {
                it.isFile && it.extension.equals("mp4", ignoreCase = true)
            }?.let {
                files.addAll(it)
            }
        }

        // 3. Also check main internal files directory for any orphan mp4 files
        context.filesDir.listFiles()?.filter {
            it.isFile && it.extension.equals("mp4", ignoreCase = true)
        }?.let {
            files.addAll(it)
        }

        // 4. Check external SD Card app recordings directory if available
        val dirs = androidx.core.content.ContextCompat.getExternalFilesDirs(context, null)
        if (dirs.size > 1 && dirs[1] != null) {
            val sdRecordingsDir = File(dirs[1], "recordings")
            if (sdRecordingsDir.exists() && sdRecordingsDir.isDirectory) {
                sdRecordingsDir.listFiles()?.filter {
                    it.isFile && it.extension.equals("mp4", ignoreCase = true)
                }?.let {
                    files.addAll(it)
                }
            }
        }

        // Deduplicate by absolute path and sort descending by last modified date
        val uniqueSortedFiles = files.distinctBy { it.absolutePath }
            .sortedByDescending { it.lastModified() }

        Log.d(TAG, "Found ${uniqueSortedFiles.size} video files (public DCIM + internal)")

        return uniqueSortedFiles.map { file ->
            extractVideoItem(file)
        }
    }

    /**
     * Extracts metadata (Duration, Resolution, File Size, Last Modified Date)
     * from a video File using MediaMetadataRetriever and File properties.
     */
    fun extractVideoItem(file: File): VideoItem {
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
                maxDim > 0 -> "480p"
                else -> "1080p"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting metadata for video file ${file.name}", e)
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                // Ignore release exceptions
            }
        }

        val totalSeconds = durationMs / 1000
        val durationFormatted = formatDuration(totalSeconds)

        val sizeBytes = file.length()
        val sizeMb = sizeBytes / (1024.0 * 1024.0)
        val formattedSizeMb = String.format(Locale.US, "%.1f", sizeMb).toDoubleOrNull() ?: sizeMb

        val lastModified = file.lastModified()
        val dateText = formatDateText(lastModified)

        // Generate consistent gradient colors based on filename hash
        val colorPalettes = listOf(
            listOf(0xFF1A73E8, 0xFF004BA0),
            listOf(0xFF00897B, 0xFF004D40),
            listOf(0xFFF9AB00, 0xFFB26A00),
            listOf(0xFFEA4335, 0xFFB31412),
            listOf(0xFF673AB7, 0xFF311B92),
            listOf(0xFF009688, 0xFF004D40)
        )
        val paletteIndex = kotlin.math.abs(file.name.hashCode()) % colorPalettes.size

        val cameraType = if (file.name.contains("FRONT", ignoreCase = true)) "Front Camera" else "Back Camera"

        return VideoItem(
            id = file.absolutePath,
            title = file.name,
            duration = durationFormatted,
            resolution = resolution,
            dateText = dateText,
            timestamp = lastModified,
            cameraType = cameraType,
            sizeMb = formattedSizeMb,
            gradientColors = colorPalettes[paletteIndex],
            filePath = file.absolutePath
        )
    }

    /**
     * Deletes a recorded video file from internal storage given its file path.
     */
    fun deleteRecordedFile(filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) return false
        return try {
            val file = File(filePath)
            if (file.exists()) {
                val deleted = file.delete()
                Log.d(TAG, "Deleted file $filePath: $deleted")
                deleted
            } else {
                false
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
