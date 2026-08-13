package com.twinpath.cambg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinpath.cambg.model.CameraUiState
import com.twinpath.cambg.model.RecordingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTopBar(
    uiState: CameraUiState,
    pulseAlpha: Float,
    onToggleCamera: () -> Unit,
    onCycleFlash: () -> Unit,
    onClickQuality: () -> Unit,
    onClickSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
                onClick = onClickQuality,
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
                onClick = onClickSettings,
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
}

private fun formatSeconds(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
