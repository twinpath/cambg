package com.twinpath.cambg.ui.components

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.twinpath.cambg.camera.CameraXRecordingManager
import com.twinpath.cambg.model.CameraUiState
import com.twinpath.cambg.constant.VideoConstants

@Composable
fun CameraViewfinder(
    uiState: CameraUiState,
    hasCameraPermission: Boolean,
    recordingManager: CameraXRecordingManager,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val serviceState by com.twinpath.cambg.service.BackgroundRecordingService.serviceState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            com.twinpath.cambg.service.BackgroundRecordingService.setPreviewView(null)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (hasCameraPermission) {
            val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }
            val overlayAlpha = remember { Animatable(0f) }

            LaunchedEffect(serviceState.isServiceRunning) {
                // Quick transition fade out and fade in to hide unbind/rebind glitch
                overlayAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 150)
                )
                overlayAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 250)
                )
            }

            val aspectModifier = when (uiState.aspectRatio) {
                VideoConstants.ASPECT_RATIO_9_16 -> Modifier.aspectRatio(9f / 16f)
                VideoConstants.ASPECT_RATIO_3_4 -> Modifier.aspectRatio(3f / 4f)
                else -> Modifier.fillMaxSize()
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            previewViewRef.value = this
                        }
                    },
                    modifier = aspectModifier
                )

                // Black transition overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = overlayAlpha.value))
                )
            }

            // Bind camera only when camera direction, quality, aspect ratio, service running state, or previewView changes
            LaunchedEffect(
                uiState.isFrontCamera,
                uiState.quality,
                uiState.aspectRatio,
                previewViewRef.value,
                serviceState.isServiceRunning
            ) {
                val pv = previewViewRef.value ?: return@LaunchedEffect
                if (serviceState.isServiceRunning) {
                    com.twinpath.cambg.service.BackgroundRecordingService.setPreviewView(pv)
                } else {
                    recordingManager.bindCamera(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        previewView = pv,
                        isFrontCamera = uiState.isFrontCamera,
                        qualityString = uiState.quality,
                        aspectRatioString = uiState.aspectRatio,
                        flashMode = uiState.flashMode,
                        zoomRatio = uiState.zoomRatio
                    )
                }
            }

            // Update only zoom & flash controls without rebinding the camera
            LaunchedEffect(uiState.zoomRatio, uiState.flashMode) {
                if (serviceState.isServiceRunning) {
                    com.twinpath.cambg.service.BackgroundRecordingService.updateCameraControls(
                        flashMode = uiState.flashMode,
                        zoomRatio = uiState.zoomRatio,
                        isFrontCamera = uiState.isFrontCamera
                    )
                } else {
                    recordingManager.updateCameraControls(
                        flashMode = uiState.flashMode,
                        zoomRatio = uiState.zoomRatio,
                        isFrontCamera = uiState.isFrontCamera
                    )
                }
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
                    onClick = onRequestPermissions,
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
