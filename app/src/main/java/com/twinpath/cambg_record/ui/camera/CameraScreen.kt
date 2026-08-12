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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val recordingManager = remember { CameraXRecordingManager() }

    var showSettingsSheet by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
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
        } else {
            // CameraX Live Viewfinder Preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    // Hold a stable reference to the PreviewView
                    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }

                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                previewViewRef.value = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Bind camera only when camera direction or quality changes (not on zoom/flash)
                    LaunchedEffect(uiState.isFrontCamera, uiState.quality) {
                        val pv = previewViewRef.value ?: return@LaunchedEffect
                        recordingManager.bindCamera(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            previewView = pv,
                            isFrontCamera = uiState.isFrontCamera,
                            qualityString = uiState.quality,
                            flashMode = uiState.flashMode,
                            zoomRatio = uiState.zoomRatio
                        )
                    }

                    // Update only zoom & flash controls without rebinding the camera
                    LaunchedEffect(uiState.zoomRatio, uiState.flashMode) {
                        recordingManager.updateCameraControls(
                            flashMode = uiState.flashMode,
                            zoomRatio = uiState.zoomRatio,
                            isFrontCamera = uiState.isFrontCamera
                        )
                    }
                } else {
                    // Fallback Permission Request Card
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(24.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideocamOff,
                            contentDescription = "Camera Permission Needed",
                            tint = Color(0xFFEA4335),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CamBG Record needs camera and microphone permissions to capture video.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.RECORD_AUDIO
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
                        ) {
                            Text("Grant Permission", color = Color.White)
                        }
                    }
                }

                // Grid Overlay
                if (uiState.showGridOverlay && hasCameraPermission) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 2.dp.toPx()
                        val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        val width = size.width
                        val height = size.height

                        // Vertical grid lines
                        drawLine(
                            color = Color.White.copy(alpha = 0.25f),
                            start = androidx.compose.ui.geometry.Offset(width / 3f, 0f),
                            end = androidx.compose.ui.geometry.Offset(width / 3f, height),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPathEffect
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.25f),
                            start = androidx.compose.ui.geometry.Offset(2 * width / 3f, 0f),
                            end = androidx.compose.ui.geometry.Offset(2 * width / 3f, height),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPathEffect
                        )

                        // Horizontal grid lines
                        drawLine(
                            color = Color.White.copy(alpha = 0.25f),
                            start = androidx.compose.ui.geometry.Offset(0f, height / 3f),
                            end = androidx.compose.ui.geometry.Offset(width, height / 3f),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPathEffect
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.25f),
                            start = androidx.compose.ui.geometry.Offset(0f, 2 * height / 3f),
                            end = androidx.compose.ui.geometry.Offset(width, 2 * height / 3f),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPathEffect
                        )
                    }
                }

                // Camera Lens Info Overlay
                if (hasCameraPermission) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 210.dp)
                            .alpha(0.7f)
                    ) {
                        Text(
                            text = if (uiState.isFrontCamera) "FRONT CAMERA" else "BACK CAMERA",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${String.format("%.1f", uiState.zoomRatio)}x Zoom",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Timer & Quality Chip
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Timer badge
                Surface(
                    color = when (uiState.recordingState) {
                        RecordingState.RECORDING -> Color(0xFFEA4335)
                        RecordingState.PAUSED -> Color(0xFFF9AB00)
                        else -> Color.Black.copy(alpha = 0.6f)
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.recordingState == RecordingState.RECORDING) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.White, CircleShape)
                                    .alpha(pulseAlpha)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = formatSeconds(uiState.elapsedTimeSeconds),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Quality chip
                AssistChip(
                    onClick = { showSettingsSheet = true },
                    label = {
                        Text(
                            text = uiState.quality,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    )
                )
            }

            // Right: Controls (Switch Camera, Flash, Settings)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleCamera,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("switch_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onCycleFlash,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("flash_toggle_button")
                ) {
                    val flashIcon = when (uiState.flashMode) {
                        "ON" -> Icons.Default.FlashOn
                        "AUTO" -> Icons.Default.FlashAuto
                        else -> Icons.Default.FlashOff
                    }
                    Icon(
                        imageVector = flashIcon,
                        contentDescription = "Toggle Flash",
                        tint = if (uiState.flashMode != "OFF") Color(0xFFF9AB00) else Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("quick_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }

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
            onToggleGrid = onToggleGrid,
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
