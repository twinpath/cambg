package com.example.ui.camera

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.camera.CameraXRecordingManager
import com.example.model.CameraUiState
import com.example.model.RecordingState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    uiState: CameraUiState,
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

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
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
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        } else {
            recordingManager.startRecording(
                context = context,
                isAudioEnabled = uiState.isAudioEnabled,
                onStarted = {
                    onStartRecord()
                },
                onFinished = { file, fileSize ->
                    onVideoSaved(file, fileSize)
                },
                onError = { err ->
                    Log.e("CameraScreen", "Video recording failed: $err")
                }
            )
        }
    }

    val handlePauseRecord = {
        recordingManager.pauseRecording()
        onPauseRecord()
    }

    val handleResumeRecord = {
        recordingManager.resumeRecording()
        onResumeRecord()
    }

    val handleStopRecord = {
        recordingManager.stopRecording()
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
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }
                        },
                        update = { previewView ->
                            recordingManager.bindCamera(
                                context = context,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                isFrontCamera = uiState.isFrontCamera,
                                qualityString = uiState.quality,
                                flashMode = uiState.flashMode,
                                zoomRatio = uiState.zoomRatio
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(bottom = 96.dp, top = 16.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zoom Slider Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1x",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium
                )
                Slider(
                    value = uiState.zoomRatio,
                    onValueChange = onSetZoomRatio,
                    valueRange = 1.0f..5.0f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .testTag("zoom_slider")
                )
                Text(
                    text = "5x",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recording Controls Row (Pause/Resume, Main Record FAB, Stealth Toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Action: Pause / Resume (Visible during recording/pause)
                if (uiState.recordingState != RecordingState.IDLE) {
                    IconButton(
                        onClick = {
                            if (uiState.recordingState == RecordingState.RECORDING) {
                                handlePauseRecord()
                            } else {
                                handleResumeRecord()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .testTag("pause_resume_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.recordingState == RecordingState.RECORDING)
                                Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Pause/Resume",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    // Quick Stealth Toggle button
                    IconButton(
                        onClick = onToggleStealth,
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (uiState.isStealthMode) Color(0xFF1A73E8) else Color.White.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .testTag("stealth_mode_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isStealthMode)
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Stealth Mode",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Center Primary Action FAB
                when (uiState.recordingState) {
                    RecordingState.IDLE -> {
                        ExtendedFloatingActionButton(
                            onClick = handleStartRecord,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Record",
                                    tint = Color.White
                                )
                            },
                            text = {
                                Text(
                                    text = "Record",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            },
                            containerColor = Color(0xFFEA4335), // Google Red
                            modifier = Modifier
                                .height(64.dp)
                                .testTag("start_record_fab")
                        )
                    }

                    RecordingState.RECORDING, RecordingState.PAUSED -> {
                        // Morph into Stop Button
                        FloatingActionButton(
                            onClick = handleStopRecord,
                            containerColor = Color(0xFFEA4335),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(72.dp)
                                .testTag("stop_record_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Recording",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Right Action: Grid lines toggle
                IconButton(
                    onClick = onToggleGrid,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (uiState.showGridOverlay) Color(0xFF1A73E8) else Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .testTag("grid_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Grid4x4,
                        contentDescription = "Grid Overlay",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // --- Quick Settings Modal Bottom Sheet ---
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Quick Recording Controls",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quality Selection Row
                    Text(
                        text = "Video Resolution",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("480p", "720p", "1080p", "4K").forEach { qualityOpt ->
                            val isSelected = uiState.quality == qualityOpt
                            AssistChip(
                                onClick = { onSetQuality(qualityOpt) },
                                label = { Text(qualityOpt) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggleAudio() }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isAudioEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Audio Recording",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (uiState.isAudioEnabled) "Enabled (Camcorder mic)" else "Muted (Video only)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = { showSettingsSheet = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

private fun formatSeconds(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
