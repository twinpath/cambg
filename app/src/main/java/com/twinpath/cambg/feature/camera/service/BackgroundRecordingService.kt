package com.twinpath.cambg.feature.camera.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.twinpath.cambg.MainActivity
import com.twinpath.cambg.feature.camera.model.RecordingState
import com.twinpath.cambg.feature.camera.helper.CameraRecordingHelper
import com.twinpath.cambg.feature.camera.helper.ScreenRecordingHelper
import com.twinpath.cambg.feature.camera.helper.CameraXRecordingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import com.twinpath.cambg.core.constant.VideoConstants


data class ServiceRecordingState(
    val isServiceRunning: Boolean = false,
    val recordingState: RecordingState = RecordingState.IDLE,
    val elapsedTimeSeconds: Long = 0L,
    val mode: String = "CAMERA", // "CAMERA" or "MEDIA_PROJECTION"
    val lastSavedFilePath: String? = null,
    val lastSavedFileSize: Long = 0L,
    val statusMessage: String? = null
)

class BackgroundRecordingService : LifecycleService() {

    private lateinit var cameraHelper: CameraRecordingHelper
    private lateinit var screenHelper: ScreenRecordingHelper

    private var currentOutputFile: File? = null
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        serviceInstance = this
        cameraHelper = CameraRecordingHelper(this, this)
        screenHelper = ScreenRecordingHelper(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_START_CAMERA_RECORDING -> {
                val isFrontCamera = intent.getBooleanExtra(EXTRA_IS_FRONT_CAMERA, false)
                val quality = intent.getStringExtra(EXTRA_QUALITY) ?: VideoConstants.DEFAULT_RESOLUTION
                val isAudioEnabled = intent.getBooleanExtra(EXTRA_AUDIO_ENABLED, true)
                val storageLocation = intent.getStringExtra(EXTRA_STORAGE_LOCATION) ?: "PUBLIC_DCIM"
                val customStoragePath = intent.getStringExtra(EXTRA_CUSTOM_STORAGE_PATH) ?: "CamBGRecord"
                val frameRate = intent.getStringExtra(EXTRA_FRAME_RATE) ?: VideoConstants.DEFAULT_FPS
                val bitrate = intent.getStringExtra(EXTRA_BITRATE) ?: VideoConstants.DEFAULT_BITRATE
                startCameraRecording(isFrontCamera, quality, isAudioEnabled, storageLocation, customStoragePath, frameRate, bitrate)
            }

            ACTION_START_MEDIA_PROJECTION -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1)
                val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_PROJECTION_DATA, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_PROJECTION_DATA)
                }
                val storageLocation = intent.getStringExtra(EXTRA_STORAGE_LOCATION) ?: "PUBLIC_DCIM"
                val customStoragePath = intent.getStringExtra(EXTRA_CUSTOM_STORAGE_PATH) ?: "CamBGRecord"
                val frameRate = intent.getStringExtra(EXTRA_FRAME_RATE) ?: VideoConstants.DEFAULT_FPS
                val bitrate = intent.getStringExtra(EXTRA_BITRATE) ?: VideoConstants.DEFAULT_BITRATE
                if (data != null && resultCode != -1) {
                    startMediaProjectionRecording(resultCode, data, storageLocation, customStoragePath, frameRate, bitrate)
                } else {
                    Log.e(TAG, "Invalid MediaProjection intent data")
                    stopSelf()
                }
            }

            ACTION_PAUSE_RECORDING -> pauseRecording()
            ACTION_RESUME_RECORDING -> resumeRecording()
            ACTION_STOP_RECORDING -> stopRecordingAndSelf()
        }

        return START_STICKY
    }

    private fun startCameraRecording(
        isFrontCamera: Boolean,
        qualityStr: String,
        isAudioEnabled: Boolean,
        storageLocation: String,
        customStoragePath: String,
        frameRate: String,
        bitrate: String
    ) {
        val notification = createNotification("CamBG Recording Active (Camera)", "Capturing background camera video...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val type = ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        _serviceState.update {
            it.copy(
                isServiceRunning = true,
                mode = "CAMERA",
                recordingState = RecordingState.RECORDING,
                elapsedTimeSeconds = 0L,
                statusMessage = "Starting background camera recording..."
            )
        }

        cameraHelper.startRecording(
            isFrontCamera = isFrontCamera,
            qualityStr = qualityStr,
            isAudioEnabled = isAudioEnabled,
            storageLocation = storageLocation,
            customStoragePath = customStoragePath,
            onStart = {
                Log.d(TAG, "CameraX background recording started")
                startTimer()
                _serviceState.update {
                    it.copy(
                        recordingState = RecordingState.RECORDING,
                        statusMessage = "Recording active..."
                    )
                }
            },
            onFinalize = { outputFile, fileSize, hasError ->
                stopTimer()
                if (!hasError && fileSize > 0) {
                    Log.d(TAG, "CameraX recording saved: ${outputFile.absolutePath} ($fileSize bytes)")
                    val isPublic = storageLocation == "PUBLIC_DCIM" || storageLocation == "CUSTOM"
                    if (isPublic) {
                        com.twinpath.cambg.feature.camera.helper.CameraXRecordingManager.scanFileToMediaStore(
                            this@BackgroundRecordingService, outputFile
                        )
                    }
                    _serviceState.update {
                        it.copy(
                            recordingState = RecordingState.IDLE,
                            lastSavedFilePath = outputFile.absolutePath,
                            lastSavedFileSize = fileSize,
                            statusMessage = "Saved: ${outputFile.name}"
                        )
                    }
                } else {
                    Log.e(TAG, "CameraX recording error")
                }
            }
        )
    }

    private fun startMediaProjectionRecording(
        resultCode: Int,
        data: Intent,
        storageLocation: String,
        customStoragePath: String,
        frameRate: String,
        bitrate: String
    ) {
        val notification = createNotification("CamBG MediaProjection Active", "Recording screen & background media...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        _serviceState.update {
            it.copy(
                isServiceRunning = true,
                mode = "MEDIA_PROJECTION",
                recordingState = RecordingState.RECORDING,
                elapsedTimeSeconds = 0L,
                statusMessage = "Starting screen capture recording..."
            )
        }

        screenHelper.startRecording(
            resultCode = resultCode,
            data = data,
            storageLocation = storageLocation,
            customStoragePath = customStoragePath,
            frameRate = frameRate,
            bitrate = bitrate,
            onStart = { outputFile ->
                currentOutputFile = outputFile
                startTimer()
                Log.d(TAG, "MediaProjection screen recording started")
            },
            onError = {
                stopSelf()
            }
        )
    }

    private fun pauseRecording() {
        if (_serviceState.value.mode == "CAMERA") {
            cameraHelper.pause()
        } else {
            screenHelper.pause()
        }
        stopTimer()
        _serviceState.update {
            it.copy(
                recordingState = RecordingState.PAUSED,
                statusMessage = "Recording paused"
            )
        }
        updateNotification("Recording Paused", "Tap to resume or stop recording.")
    }

    private fun resumeRecording() {
        if (_serviceState.value.mode == "CAMERA") {
            cameraHelper.resume()
        } else {
            screenHelper.resume()
        }
        startTimer()
        _serviceState.update {
            it.copy(
                recordingState = RecordingState.RECORDING,
                statusMessage = "Recording active..."
            )
        }
        updateNotification("Recording Active", "Background video recording in progress...")
    }

    private fun stopRecordingAndSelf() {
        stopTimer()

        var savedFile: File? = null
        if (_serviceState.value.mode == "CAMERA") {
            cameraHelper.stop()
        } else {
            savedFile = screenHelper.stop()
        }

        val finalFile = savedFile ?: currentOutputFile
        val fileSize = finalFile?.length() ?: 0L

        _serviceState.update {
            it.copy(
                isServiceRunning = false,
                recordingState = RecordingState.IDLE,
                lastSavedFilePath = finalFile?.absolutePath,
                lastSavedFileSize = fileSize,
                statusMessage = finalFile?.let { f -> "Saved: ${f.name}" } ?: "Recording stopped"
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (true) {
                delay(1000)
                _serviceState.update {
                    it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Background Recording Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows ongoing status when CamBG background video recording is active"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, BackgroundRecordingService::class.java).apply {
            action = ACTION_STOP_RECORDING
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_media_pause, "Stop Recording", stopPendingIntent)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, createNotification(title, content))
    }

    override fun onDestroy() {
        stopTimer()
        serviceInstance = null
        super.onDestroy()
    }

    companion object {
        const val TAG = "BgRecordingService"
        const val CHANNEL_ID = "bg_recording_channel"
        const val NOTIFICATION_ID = 9001

        const val ACTION_START_CAMERA_RECORDING = "com.twinpath.cambg.action.START_CAMERA_RECORDING"
        const val ACTION_START_MEDIA_PROJECTION = "com.twinpath.cambg.action.START_MEDIA_PROJECTION"
        const val ACTION_PAUSE_RECORDING = "com.twinpath.cambg.action.PAUSE_RECORDING"
        const val ACTION_RESUME_RECORDING = "com.twinpath.cambg.action.RESUME_RECORDING"
        const val ACTION_STOP_RECORDING = "com.twinpath.cambg.action.STOP_RECORDING"

        const val EXTRA_IS_FRONT_CAMERA = "extra_is_front_camera"
        const val EXTRA_QUALITY = "extra_quality"
        const val EXTRA_AUDIO_ENABLED = "extra_audio_enabled"
        const val EXTRA_STORAGE_LOCATION = "extra_storage_location"
        const val EXTRA_CUSTOM_STORAGE_PATH = "extra_custom_storage_path"

        const val EXTRA_FRAME_RATE = "extra_frame_rate"
        const val EXTRA_BITRATE = "extra_bitrate"

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_PROJECTION_DATA = "extra_projection_data"

        private val _serviceState = MutableStateFlow(ServiceRecordingState())
        val serviceState: StateFlow<ServiceRecordingState> = _serviceState.asStateFlow()

        private var serviceInstance: BackgroundRecordingService? = null

        fun setPreviewView(previewView: PreviewView?) {
            serviceInstance?.cameraHelper?.setPreviewView(previewView)
        }

        fun updateCameraControls(flashMode: String, zoomRatio: Float, isFrontCamera: Boolean) {
            serviceInstance?.cameraHelper?.updateCameraControls(flashMode, zoomRatio, isFrontCamera)
        }

        fun startCameraService(
            context: Context,
            isFrontCamera: Boolean,
            quality: String,
            isAudioEnabled: Boolean,
            storageLocation: String,
            customStoragePath: String,
            frameRate: String,
            bitrate: String
        ) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_START_CAMERA_RECORDING
                putExtra(EXTRA_IS_FRONT_CAMERA, isFrontCamera)
                putExtra(EXTRA_QUALITY, quality)
                putExtra(EXTRA_AUDIO_ENABLED, isAudioEnabled)
                putExtra(EXTRA_STORAGE_LOCATION, storageLocation)
                putExtra(EXTRA_CUSTOM_STORAGE_PATH, customStoragePath)
                putExtra(EXTRA_FRAME_RATE, frameRate)
                putExtra(EXTRA_BITRATE, bitrate)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun startMediaProjectionService(
            context: Context,
            resultCode: Int,
            data: Intent,
            storageLocation: String,
            customStoragePath: String,
            frameRate: String,
            bitrate: String
        ) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_START_MEDIA_PROJECTION
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_PROJECTION_DATA, data)
                putExtra(EXTRA_STORAGE_LOCATION, storageLocation)
                putExtra(EXTRA_CUSTOM_STORAGE_PATH, customStoragePath)
                putExtra(EXTRA_FRAME_RATE, frameRate)
                putExtra(EXTRA_BITRATE, bitrate)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun pauseService(context: Context) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_PAUSE_RECORDING
            }
            context.startService(intent)
        }

        fun resumeService(context: Context) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_RESUME_RECORDING
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
            context.startService(intent)
        }
    }
}
