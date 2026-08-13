package com.twinpath.cambg.feature.camera.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinpath.cambg.feature.camera.model.CameraUiState
import com.twinpath.cambg.feature.camera.model.RecordingState

@Composable
fun CameraControls(
    uiState: CameraUiState,
    onSetZoomRatio: (Float) -> Unit,
    onToggleStealth: () -> Unit,
    onClickGallery: () -> Unit,
    onStartRecord: () -> Unit,
    onPauseRecord: () -> Unit,
    onResumeRecord: () -> Unit,
    onStopRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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

        // Recording Controls Row (Stealth, Start/Stop Record FAB, Gallery/Pause-Resume)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Action: Quick Stealth Toggle button (Always visible)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
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

            // Center Primary Action FAB (Circular Shutter Button of size 72.dp)
            Box(
                modifier = Modifier.weight(1.2f),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = {
                        if (uiState.recordingState == RecordingState.IDLE) {
                            onStartRecord()
                        } else {
                            onStopRecord()
                        }
                    },
                    containerColor = Color(0xFFEA4335), // Google Red
                    shape = CircleShape,
                    modifier = Modifier
                        .size(72.dp)
                        .testTag(if (uiState.recordingState == RecordingState.IDLE) "start_record_fab" else "stop_record_fab")
                ) {
                    Icon(
                        imageVector = if (uiState.recordingState == RecordingState.IDLE) {
                            Icons.Default.Videocam
                        } else {
                            Icons.Default.Stop
                        },
                        contentDescription = if (uiState.recordingState == RecordingState.IDLE) "Record" else "Stop Recording",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Right Action: Gallery or Pause/Resume
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.recordingState == RecordingState.IDLE) {
                    IconButton(
                        onClick = onClickGallery,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .testTag("gallery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Open Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (uiState.recordingState == RecordingState.RECORDING) {
                                onPauseRecord()
                            } else {
                                onResumeRecord()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (uiState.recordingState == RecordingState.PAUSED) Color(0xFFF9AB00) else Color.White.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .testTag("pause_resume_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.recordingState == RecordingState.RECORDING)
                                Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Pause/Resume",
                            tint = if (uiState.recordingState == RecordingState.PAUSED) Color.Black else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
