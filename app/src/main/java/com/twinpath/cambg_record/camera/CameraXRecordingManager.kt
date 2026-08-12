package com.twinpath.cambg_record.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraXRecordingManager {

    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var camera: Camera? = null

    // Track current binding state to avoid unnecessary rebinds
    private var currentBindingKey: String? = null

    fun bindCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        isFrontCamera: Boolean,
        qualityString: String,
        flashMode: String,
        zoomRatio: Float,
        onError: (Throwable) -> Unit = {}
    ) {
        val newBindingKey = "${isFrontCamera}_${qualityString}"
        if (newBindingKey == currentBindingKey && camera != null) {
            // Only update controls, don't rebind
            updateCameraControls(flashMode, zoomRatio, isFrontCamera)
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val quality = when (qualityString) {
                    "480p" -> Quality.SD
                    "720p" -> Quality.HD
                    "1080p" -> Quality.FHD
                    "4K" -> Quality.UHD
                    else -> Quality.HIGHEST
                }

                val qualitySelector = QualitySelector.from(
                    quality,
                    FallbackStrategy.higherQualityOrLowerThan(quality)
                )

                val recorder = Recorder.Builder()
                    .setQualitySelector(qualitySelector)
                    .build()

                videoCapture = VideoCapture.withOutput(recorder)

                val cameraSelector = if (isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )

                currentBindingKey = newBindingKey

                // Apply zoom and flash control
                camera?.cameraControl?.setZoomRatio(zoomRatio.coerceIn(1.0f, 5.0f))
                if (!isFrontCamera) {
                    camera?.cameraControl?.enableTorch(flashMode == "ON")
                }

            } catch (e: Exception) {
                Log.e("CameraXManager", "Camera binding failed", e)
                onError(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun updateCameraControls(flashMode: String, zoomRatio: Float, isFrontCamera: Boolean) {
        try {
            camera?.cameraControl?.setZoomRatio(zoomRatio.coerceIn(1.0f, 5.0f))
            if (!isFrontCamera) {
                camera?.cameraControl?.enableTorch(flashMode == "ON")
            }
        } catch (e: Exception) {
            Log.e("CameraXManager", "Error updating camera controls", e)
        }
    }

    fun startRecording(
        context: Context,
        isAudioEnabled: Boolean,
        usePublicStorage: Boolean = true,
        onStarted: () -> Unit,
        onFinished: (File, Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val capture = videoCapture
        if (capture == null) {
            onError("VideoCapture use case not initialized")
            return
        }

        val outputFile = createOutputFile(context, usePublicStorage, "BACK")

        val fileOutputOptions = FileOutputOptions.Builder(outputFile).build()

        var pendingRecording = capture.output.prepareRecording(context, fileOutputOptions)

        if (isAudioEnabled && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pendingRecording = pendingRecording.withAudioEnabled()
        }

        activeRecording = pendingRecording.start(ContextCompat.getMainExecutor(context)) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    Log.d("CameraXManager", "Video recording started: ${outputFile.absolutePath}")
                    onStarted()
                }
                is VideoRecordEvent.Pause -> {
                    Log.d("CameraXManager", "Video recording paused")
                }
                is VideoRecordEvent.Resume -> {
                    Log.d("CameraXManager", "Video recording resumed")
                }
                is VideoRecordEvent.Finalize -> {
                    if (!event.hasError()) {
                        val fileSize = outputFile.length()
                        Log.d("CameraXManager", "Video saved: ${outputFile.absolutePath} ($fileSize bytes)")
                        // Notify MediaStore so the video appears in Gallery/Photos
                        if (usePublicStorage) {
                            scanFileToMediaStore(context, outputFile)
                        }
                        onFinished(outputFile, fileSize)
                    } else {
                        activeRecording?.close()
                        activeRecording = null
                        val errorMsg = "Recording error code: ${event.error}"
                        Log.e("CameraXManager", errorMsg, event.cause)
                        onError(errorMsg)
                    }
                }
            }
        }
    }

    fun pauseRecording() {
        activeRecording?.pause()
    }

    fun resumeRecording() {
        activeRecording?.resume()
    }

    fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
    }

    fun isRecording(): Boolean = activeRecording != null

    companion object {
        /**
         * Creates an output file in either the public DCIM/CamBGRecord directory
         * or the internal app storage recordings directory.
         */
        fun createOutputFile(
            context: Context,
            usePublicStorage: Boolean,
            cameraTag: String = ""
        ): File {
            val recordingsDir = if (usePublicStorage) {
                val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                File(dcimDir, "CamBGRecord")
            } else {
                File(context.filesDir, "recordings")
            }
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val prefix = if (cameraTag.isNotEmpty()) "VID_${cameraTag}_" else "VID_"
            return File(recordingsDir, "${prefix}$timeStamp.mp4")
        }

        /**
         * Notifies the Android MediaStore about the new file so it appears
         * in Gallery, Google Photos, and other media apps.
         */
        fun scanFileToMediaStore(context: Context, file: File) {
            try {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf("video/mp4")
                ) { path, uri ->
                    Log.d("CameraXManager", "MediaScanner indexed: $path -> $uri")
                }
            } catch (e: Exception) {
                Log.e("CameraXManager", "MediaScanner failed for ${file.absolutePath}", e)
            }
        }
    }
}

