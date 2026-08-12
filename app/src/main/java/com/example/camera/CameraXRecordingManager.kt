package com.example.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
        onStarted: () -> Unit,
        onFinished: (File, Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val capture = videoCapture
        if (capture == null) {
            onError("VideoCapture use case not initialized")
            return
        }

        // Prepare output file in app's internal storage directory (context.filesDir)
        val recordingsDir = File(context.filesDir, "recordings")
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val outputFile = File(recordingsDir, "VID_$timeStamp.mp4")

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
                        Log.d("CameraXManager", "Video saved to internal storage: ${outputFile.absolutePath} ($fileSize bytes)")
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
}
