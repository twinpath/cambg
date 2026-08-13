package com.twinpath.cambg.service.helper

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File

class CameraRecordingHelper(private val context: Context, private val lifecycleOwner: LifecycleOwner) {

    private var activeRecording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var camera: Camera? = null
    private var currentPreviewUseCase: androidx.camera.core.Preview? = null
    private var activePreviewView: PreviewView? = null

    fun setPreviewView(previewView: PreviewView?) {
        activePreviewView = previewView
        currentPreviewUseCase?.setSurfaceProvider(previewView?.surfaceProvider)
    }

    fun updateCameraControls(flashMode: String, zoomRatio: Float, isFrontCamera: Boolean) {
        try {
            camera?.cameraControl?.setZoomRatio(zoomRatio.coerceIn(1.0f, 5.0f))
            if (!isFrontCamera) {
                camera?.cameraControl?.enableTorch(flashMode == "ON")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating camera controls", e)
        }
    }

    fun startRecording(
        isFrontCamera: Boolean,
        qualityStr: String,
        isAudioEnabled: Boolean,
        storageLocation: String,
        customStoragePath: String,
        onStart: () -> Unit,
        onFinalize: (outputFile: File, fileSize: Long, error: Boolean) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val quality = when (qualityStr) {
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

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(activePreviewView?.surfaceProvider)
                }
                currentPreviewUseCase = preview

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )

                val camTypeStr = if (isFrontCamera) "FRONT" else "BACK"
                val outputFile = com.twinpath.cambg.camera.CameraXRecordingManager.createOutputFile(
                    context,
                    storageLocation,
                    customStoragePath,
                    camTypeStr
                )

                val fileOutputOptions = FileOutputOptions.Builder(outputFile).build()

                var prepare = videoCapture?.output?.prepareRecording(context, fileOutputOptions)
                if (isAudioEnabled) {
                    prepare = prepare?.withAudioEnabled()
                }

                activeRecording = prepare?.start(ContextCompat.getMainExecutor(context)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            onStart()
                        }
                        is VideoRecordEvent.Finalize -> {
                            val fileSize = outputFile.length()
                            val hasError = event.hasError()
                            onFinalize(outputFile, fileSize, hasError)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start camera background recording", e)
                onFinalize(File(""), 0L, true)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun pause() {
        activeRecording?.pause()
    }

    fun resume() {
        activeRecording?.resume()
    }

    fun stop() {
        activeRecording?.stop()
        activeRecording = null
        camera = null
        currentPreviewUseCase = null
    }

    companion object {
        private const val TAG = "CameraRecordingHelper"
    }
}
