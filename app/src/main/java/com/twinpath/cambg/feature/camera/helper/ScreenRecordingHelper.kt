package com.twinpath.cambg.feature.camera.helper

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import com.twinpath.cambg.core.constant.VideoConstants

class ScreenRecordingHelper(private val context: Context) {

    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var currentOutputFilePath: String? = null
    private var currentPfd: ParcelFileDescriptor? = null

    fun startRecording(
        resultCode: Int,
        data: Intent,
        storageLocation: String,
        customStoragePath: String,
        frameRate: String,
        bitrate: String,
        onStart: (String) -> Unit,
        onError: () -> Unit
    ) {
        try {
            val mpManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mpManager.getMediaProjection(resultCode, data)

            val safUri = com.twinpath.cambg.feature.camera.helper.CameraXRecordingManager.createOutputUri(
                context,
                storageLocation,
                customStoragePath,
                "SCREEN"
            )

            val metrics = context.resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            val dpi = metrics.densityDpi

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            if (safUri != null) {
                val pfd = context.contentResolver.openFileDescriptor(safUri, "rw")
                    ?: throw java.io.IOException("Failed to open file descriptor for SAF URI: $safUri")
                currentPfd = pfd
                recorder.setOutputFile(pfd.fileDescriptor)
                currentOutputFilePath = safUri.toString()
            } else {
                val outputFile = com.twinpath.cambg.feature.camera.helper.CameraXRecordingManager.createOutputFile(
                    context,
                    storageLocation,
                    customStoragePath,
                    "SCREEN"
                )
                recorder.setOutputFile(outputFile.absolutePath)
                currentOutputFilePath = outputFile.absolutePath
            }

            recorder.setVideoSize(width, height)
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            // Map and apply frame rate and bitrate from constants settings
            val mappedFps = VideoConstants.mapFpsStringToVal(frameRate)
            val resolutionTag = when {
                width >= 2160 || height >= 2160 -> VideoConstants.RESOLUTION_4K
                width >= 1080 || height >= 1080 -> VideoConstants.RESOLUTION_1080P
                width >= 720 || height >= 720 -> VideoConstants.RESOLUTION_720P
                else -> VideoConstants.RESOLUTION_480P
            }
            val mappedBitrate = VideoConstants.mapBitrateStringToVal(bitrate, resolutionTag)

            recorder.setVideoEncodingBitRate(mappedBitrate)
            recorder.setVideoFrameRate(mappedFps)
            recorder.prepare()

            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "CamBG_ScreenCapture",
                width,
                height,
                dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.surface,
                null,
                null
            )

            recorder.start()
            mediaRecorder = recorder
            onStart(currentOutputFilePath!!)

            Log.d(TAG, "MediaProjection screen recording started")

        } catch (e: Exception) {
            Log.e(TAG, "Error initiating MediaProjection recording", e)
            try {
                currentPfd?.close()
            } catch (_: Exception) {}
            currentPfd = null
            onError()
        }
    }

    fun pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mediaRecorder != null) {
            mediaRecorder?.pause()
        }
    }

    fun resume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mediaRecorder != null) {
            mediaRecorder?.resume()
        }
    }

    fun stop(): String? {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping MediaRecorder", e)
        }

        try {
            currentPfd?.close()
        } catch (_: Exception) {}
        currentPfd = null

        virtualDisplay?.release()
        virtualDisplay = null

        mediaProjection?.stop()
        mediaProjection = null

        val path = currentOutputFilePath
        currentOutputFilePath = null
        return path
    }

    companion object {
        private const val TAG = "ScreenRecordingHelper"
    }
}
