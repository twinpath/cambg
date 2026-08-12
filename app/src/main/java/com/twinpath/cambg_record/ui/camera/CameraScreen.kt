package com.twinpath.cambg_record.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.twinpath.cambg_record.camera.CameraXRecordingManager
import com.twinpath.cambg_record.model.AppSettings
import com.twinpath.cambg_record.model.CameraUiState
import com.twinpath.cambg_record.model.RecordingState
import com.twinpath.cambg_record.model.StorageLocation
import com.twinpath.cambg_record.ui.components.CameraControls
import com.twinpath.cambg_record.ui.components.CameraSettingsSheet
import com.twinpath.cambg_record.ui.components.CameraTopBar
import com.twinpath.cambg_record.ui.components.CameraViewfinder
import com.twinpath.cambg_record.util.hasPermission
import com.twinpath.cambg_record.util.hasPermissions
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    uiState: CameraUiState,
    appSettings: AppSettings = AppSettings(),
    onStartRecord: () -> Unit,
    onPauseRecord: () -> Unit,
    onResumeRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onVideoSaved: (File, Long) -> Unit = { _, _ -> },
    onToggleCamera: () -> Unit,
    onCycleFlash: () -> Unit,
    onSetQuality: (String) -> Unit,
    onToggleAudio: () -> Unit,
    onSetZoomRatio: (Float) -> Unit,
    onToggleGrid: () -> Unit,
    onToggleStealth: () -> Unit,
    onClickGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val recordingManager = remember { CameraXRecordingManager() }

    var showSettingsSheet by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(context.hasPermission(Manifest.permission.CAMERA))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
    }

    val mediaProjectionManager = remember {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    val mediaProjectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            com.twinpath.cambg_record.service.BackgroundRecordingService.startMediaProjectionService(
                context = context,
                resultCode = result.resultCode,
                data = result.data!!
            )
            onStartRecord()
        } else {
            Log.e("CameraScreen", "MediaProjection authorization denied")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            val perms = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionLauncher.launch(perms.toTypedArray())
        }
    }

    // Pulsing animation for recording state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val handleStartRecord = {
        if (!hasCameraPermission) {
            val perms = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionLauncher.launch(perms.toTypedArray())
        } else {
            com.twinpath.cambg_record.service.BackgroundRecordingService.startCameraService(
                context = context,
                isFrontCamera = uiState.isFrontCamera,
                quality = uiState.quality,
                isAudioEnabled = uiState.isAudioEnabled
            )
            onStartRecord()
        }
    }

    val handleStartScreenCaptureRecord = {
        mediaProjectionLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    val handlePauseRecord = {
        if (com.twinpath.cambg_record.service.BackgroundRecordingService.serviceState.value.isServiceRunning) {
            com.twinpath.cambg_record.service.BackgroundRecordingService.pauseService(context)
        } else {
            recordingManager.pauseRecording()
        }
        onPauseRecord()
    }

    val handleResumeRecord = {
        if (com.twinpath.cambg_record.service.BackgroundRecordingService.serviceState.value.isServiceRunning) {
            com.twinpath.cambg_record.service.BackgroundRecordingService.resumeService(context)
        } else {
            recordingManager.resumeRecording()
        }
        onResumeRecord()
    }

    val handleStopRecord = {
        if (com.twinpath.cambg_record.service.BackgroundRecordingService.serviceState.value.isServiceRunning) {
            com.twinpath.cambg_record.service.BackgroundRecordingService.stopService(context)
        } else {
            recordingManager.stopRecording()
        }
        onStopRecord()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_preview_screen")
    ) {
        // --- Viewfinder Area ---
        CameraViewfinder(
            uiState = uiState,
            hasCameraPermission = hasCameraPermission,
            recordingManager = recordingManager,
            onRequestPermissions = {
                val perms = mutableListOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    perms.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                permissionLauncher.launch(perms.toTypedArray())
            }
        )

        if (uiState.isStealthMode) {
            // Stealth mode preview hidden overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0A0A)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Stealth Mode Active",
                        tint = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Stealth Mode (Preview Hidden)",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Recording Pulse Red Border Animation
        if (uiState.recordingState == RecordingState.RECORDING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 4.dp,
                        color = Color(0xFFEA4335).copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(0.dp)
                    )
            )
        }

        // --- Status Banner (e.g. Saved to internal storage) ---
        AnimatedVisibility(
            visible = uiState.statusMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 96.dp)
        ) {
            uiState.statusMessage?.let { msg ->
                Surface(
                    color = Color(0xFF1A73E8),
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 6.dp
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // --- Top Bar Overlay (Transparent) ---
        CameraTopBar(
            uiState = uiState,
            pulseAlpha = pulseAlpha,
            onToggleCamera = onToggleCamera,
            onCycleFlash = onCycleFlash,
            onClickQuality = { showSettingsSheet = true },
            onClickSettings = { showSettingsSheet = true }
        )

        // --- Paused Blinking Badge ---
        if (uiState.recordingState == RecordingState.PAUSED) {
            Surface(
                color = Color(0xFFF9AB00),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp)
            ) {
                Text(
                    text = "RECORDING PAUSED",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // --- Bottom Controls Section ---
        CameraControls(
            uiState = uiState,
            onSetZoomRatio = onSetZoomRatio,
            onToggleStealth = onToggleStealth,
            onClickGallery = onClickGallery,
            onStartRecord = handleStartRecord,
            onPauseRecord = handlePauseRecord,
            onResumeRecord = handleResumeRecord,
            onStopRecord = handleStopRecord,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // --- Quick Settings Modal Bottom Sheet ---
        if (showSettingsSheet) {
            CameraSettingsSheet(
                uiState = uiState,
                onDismissRequest = { showSettingsSheet = false },
                onSetQuality = onSetQuality,
                onToggleAudio = onToggleAudio,
                onToggleGrid = onToggleGrid,
                onStartScreenCaptureRecord = handleStartScreenCaptureRecord
            )
        }
    }
}

private fun formatSeconds(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
