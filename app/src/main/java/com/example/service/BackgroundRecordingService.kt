package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.example.MainActivity
import com.example.model.RecordingState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private var activeRecording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var currentOutputFile: File? = null

    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_START_CAMERA_RECORDING -> {
                val isFrontCamera = intent.getBooleanExtra(EXTRA_IS_FRONT_CAMERA, false)
                val quality = intent.getStringExtra(EXTRA_QUALITY) ?: "1080p"
                val isAudioEnabled = intent.getBooleanExtra(EXTRA_AUDIO_ENABLED, true)
                startCameraRecording(isFrontCamera, quality, isAudioEnabled)
            }

            ACTION_START_MEDIA_PROJECTION -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1)
                val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_PROJECTION_DATA, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_PROJECTION_DATA)
                }
                if (data != null && resultCode != -1) {
                    startMediaProjectionRecording(resultCode, data)
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

    private fun startCameraRecording(isFrontCamera: Boolean, qualityStr: String, isAudioEnabled: Boolean) {
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

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
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

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    videoCapture
                )

                // Output File - save to public DCIM/CamBGRecord
                val usePublicStorage = true
                val camTypeStr = if (isFrontCamera) "FRONT" else "BACK"
                val outputFile = com.example.camera.CameraXRecordingManager.createOutputFile(
                    this@BackgroundRecordingService,
                    usePublicStorage,
                    camTypeStr
                )
                currentOutputFile = outputFile

                val fileOutputOptions = FileOutputOptions.Builder(outputFile).build()

                var prepare = videoCapture?.output?.prepareRecording(this, fileOutputOptions)
                if (isAudioEnabled) {
                    prepare = prepare?.withAudioEnabled()
                }

                activeRecording = prepare?.start(ContextCompat.getMainExecutor(this)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            Log.d(TAG, "CameraX background recording started")
                            startTimer()
                            _serviceState.update {
                                it.copy(
                                    recordingState = RecordingState.RECORDING,
                                    statusMessage = "Recording active..."
                                )
                            }
                        }
                        is VideoRecordEvent.Finalize -> {
                            stopTimer()
                            val fileSize = outputFile.length()
                            if (!event.hasError() && fileSize > 0) {
                                Log.d(TAG, "CameraX recording saved: ${outputFile.absolutePath} ($fileSize bytes)")
                                // Scan to MediaStore so video appears in Gallery/Photos
                                com.example.camera.CameraXRecordingManager.scanFileToMediaStore(
                                    this@BackgroundRecordingService, outputFile
                                )
                                _serviceState.update {
                                    it.copy(
                                        recordingState = RecordingState.IDLE,
                                        lastSavedFilePath = outputFile.absolutePath,
                                        lastSavedFileSize = fileSize,
                                        statusMessage = "Saved: ${outputFile.name}"
                                    )
                                }
                            } else {
                                Log.e(TAG, "CameraX recording error: ${event.error}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start camera background recording", e)
                stopSelf()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startMediaProjectionRecording(resultCode: Int, data: Intent) {
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

        try {
            val mpManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mpManager.getMediaProjection(resultCode, data)

            val outputFile = com.example.camera.CameraXRecordingManager.createOutputFile(
                this@BackgroundRecordingService,
                true, // usePublicStorage
                "SCREEN"
            )
            currentOutputFile = outputFile

            val metrics = resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            val dpi = metrics.densityDpi

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setOutputFile(outputFile.absolutePath)
            recorder.setVideoSize(width, height)
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setVideoEncodingBitRate(5 * 1024 * 1024)
            recorder.setVideoFrameRate(30)
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
            startTimer()

            Log.d(TAG, "MediaProjection screen recording started")

        } catch (e: Exception) {
            Log.e(TAG, "Error initiating MediaProjection recording", e)
            stopSelf()
        }
    }

    private fun pauseRecording() {
        if (activeRecording != null) {
            activeRecording?.pause()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mediaRecorder != null) {
            mediaRecorder?.pause()
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
        if (activeRecording != null) {
            activeRecording?.resume()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mediaRecorder != null) {
            mediaRecorder?.resume()
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

        // Stop CameraX
        activeRecording?.stop()
        activeRecording = null

        // Stop MediaProjection / MediaRecorder
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping MediaRecorder", e)
        }

        virtualDisplay?.release()
        virtualDisplay = null

        mediaProjection?.stop()
        mediaProjection = null

        val savedFile = currentOutputFile
        val fileSize = savedFile?.length() ?: 0L

        _serviceState.update {
            it.copy(
                isServiceRunning = false,
                recordingState = RecordingState.IDLE,
                lastSavedFilePath = savedFile?.absolutePath,
                lastSavedFileSize = fileSize,
                statusMessage = savedFile?.let { f -> "Saved: ${f.name}" } ?: "Recording stopped"
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
        super.onDestroy()
    }

    companion object {
        const val TAG = "BgRecordingService"
        const val CHANNEL_ID = "bg_recording_channel"
        const val NOTIFICATION_ID = 9001

        const val ACTION_START_CAMERA_RECORDING = "com.example.action.START_CAMERA_RECORDING"
        const val ACTION_START_MEDIA_PROJECTION = "com.example.action.START_MEDIA_PROJECTION"
        const val ACTION_PAUSE_RECORDING = "com.example.action.PAUSE_RECORDING"
        const val ACTION_RESUME_RECORDING = "com.example.action.RESUME_RECORDING"
        const val ACTION_STOP_RECORDING = "com.example.action.STOP_RECORDING"

        const val EXTRA_IS_FRONT_CAMERA = "extra_is_front_camera"
        const val EXTRA_QUALITY = "extra_quality"
        const val EXTRA_AUDIO_ENABLED = "extra_audio_enabled"

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_PROJECTION_DATA = "extra_projection_data"

        private val _serviceState = MutableStateFlow(ServiceRecordingState())
        val serviceState: StateFlow<ServiceRecordingState> = _serviceState.asStateFlow()

        fun startCameraService(
            context: Context,
            isFrontCamera: Boolean,
            quality: String,
            isAudioEnabled: Boolean
        ) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_START_CAMERA_RECORDING
                putExtra(EXTRA_IS_FRONT_CAMERA, isFrontCamera)
                putExtra(EXTRA_QUALITY, quality)
                putExtra(EXTRA_AUDIO_ENABLED, isAudioEnabled)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun startMediaProjectionService(
            context: Context,
            resultCode: Int,
            data: Intent
        ) {
            val intent = Intent(context, BackgroundRecordingService::class.java).apply {
                action = ACTION_START_MEDIA_PROJECTION
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_PROJECTION_DATA, data)
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
