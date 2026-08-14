package com.twinpath.cambg.feature.camera.helper

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
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
        onFinalize: (outputFilePath: String, fileSize: Long, error: Boolean) -> Unit
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

                // Check if SAF tree URI is chosen
                val safUri = com.twinpath.cambg.feature.camera.helper.CameraXRecordingManager.createOutputUri(
                    context,
                    storageLocation,
                    customStoragePath,
                    camTypeStr
                )

                var outputFile: File? = null
                var pfd: ParcelFileDescriptor? = null

                val prepare = if (safUri != null) {
                    pfd = context.contentResolver.openFileDescriptor(safUri, "rw")
                        ?: throw java.io.IOException("Failed to open file descriptor for SAF URI: $safUri")
                    val fileOutputOptions = FileDescriptorOutputOptions.Builder(pfd).build()
                    videoCapture?.output?.prepareRecording(context, fileOutputOptions)
                } else {
                    val file = com.twinpath.cambg.feature.camera.helper.CameraXRecordingManager.createOutputFile(
                        context,
                        storageLocation,
                        customStoragePath,
                        camTypeStr
                    )
                    outputFile = file
                    val fileOutputOptions = FileOutputOptions.Builder(file).build()
                    videoCapture?.output?.prepareRecording(context, fileOutputOptions)
                }

                var finalPrepare = prepare
                if (isAudioEnabled) {
                    finalPrepare = finalPrepare?.withAudioEnabled()
                }

                activeRecording = finalPrepare?.start(ContextCompat.getMainExecutor(context)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            onStart()
                        }
                        is VideoRecordEvent.Finalize -> {
                            // Close file descriptor if open
                            try {
                                pfd?.close()
                            } catch (_: Exception) {}

                            val finalPath = safUri?.toString() ?: outputFile?.absolutePath ?: ""
                            val fileSize = if (safUri != null) {
                                try {
                                    context.contentResolver.openFileDescriptor(safUri, "r")?.use { it.statSize } ?: 0L
                                } catch (_: Exception) { 0L }
                            } else {
                                outputFile?.length() ?: 0L
                            }
                            val hasError = event.hasError()
                            onFinalize(finalPath, fileSize, hasError)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start camera background recording", e)
                onFinalize("", 0L, true)
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
