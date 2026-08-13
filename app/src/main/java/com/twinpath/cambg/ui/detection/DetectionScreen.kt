package com.twinpath.cambg.ui.detection

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twinpath.cambg.model.DetectionType
import com.twinpath.cambg.model.DetectionUiState
import com.twinpath.cambg.ui.components.DetectionConfigCard
import com.twinpath.cambg.ui.components.DetectionEventEmptyState
import com.twinpath.cambg.ui.components.DetectionEventHeader
import com.twinpath.cambg.ui.components.DetectionEventItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    uiState: DetectionUiState,
    onToggleMotion: () -> Unit,
    onSetMotionSensitivity: (Float) -> Unit,
    onTogglePerson: () -> Unit,
    onSetPersonConfidence: (Float) -> Unit,
    onSimulateEvent: (DetectionType) -> Unit,
    onClearEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = uiState.isMotionEnabled || uiState.isPersonEnabled

    val infiniteTransition = rememberInfiniteTransition(label = "activePulse")
    val activeDotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "activeDotAlpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Smart Detection",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Status Indicator Header Card ---
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive)
                            Color(0xFFE6F4EA) // Light green Google tint
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("detection_status_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = if (isActive) Color(0xFF34A853) else Color.Gray,
                                    shape = CircleShape
                                )
                                .alpha(if (isActive) activeDotAlpha else 1.0f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isActive) "Active & Monitoring" else "Inactive",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color(0xFF137333) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isActive)
                                    "Background sensor engine is active and analyzing video frames."
                                else
                                    "Toggle motion or person detection below to enable automated recording.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isActive) Color(0xFF137333).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // --- Configuration Cards ---
            item {
                DetectionConfigCard(
                    uiState = uiState,
                    onToggleMotion = onToggleMotion,
                    onSetMotionSensitivity = onSetMotionSensitivity,
                    onTogglePerson = onTogglePerson,
                    onSetPersonConfidence = onSetPersonConfidence
                )
            }

            // --- Section: Detection Event Log ---
            item {
                DetectionEventHeader(
                    hasEvents = uiState.events.isNotEmpty(),
                    onClearEvents = onClearEvents
                )
            }

            // Test trigger buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSimulateEvent(DetectionType.MOTION) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_motion_button")
                    ) {
                        Text("Test Motion")
                    }
                    OutlinedButton(
                        onClick = { onSimulateEvent(DetectionType.PERSON) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_person_button")
                    ) {
                        Text("Test Person")
                    }
                }
            }

            // Events List or Empty State
            if (uiState.events.isEmpty()) {
                item {
                    DetectionEventEmptyState()
                }
            } else {
                items(uiState.events, key = { it.id }) { event ->
                    DetectionEventItem(event = event)
                }
            }

            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}
