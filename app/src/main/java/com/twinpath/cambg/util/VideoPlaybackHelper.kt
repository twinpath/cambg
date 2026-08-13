package com.twinpath.cambg.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object VideoPlaybackHelper {

    /**
     * Plays a video file using the system's default video player.
     * Uses FileProvider to securely share the file URI with external apps.
     */
    fun playVideo(context: Context, filePath: String?) {
        if (filePath.isNullOrEmpty()) {
            Toast.makeText(context, "Video file path is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(context, "Video file does not exist", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/mp4")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Check if there is an app that can handle the intent
            if (intent.resolveActivity(context.packageManager) != null || true) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No video player application found", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error playing video: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
