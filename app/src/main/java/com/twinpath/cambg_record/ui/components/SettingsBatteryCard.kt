package com.twinpath.cambg_record.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.twinpath.cambg_record.util.checkIsBatteryOptimizationIgnored

@Composable
fun SettingsBatteryCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isBatteryOptimized by remember { mutableStateOf(!checkIsBatteryOptimizationIgnored(context)) }
    var showBatteryDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isBatteryOptimized = !checkIsBatteryOptimizationIgnored(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Battery & Background Performance", icon = Icons.Default.BatterySaver)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (!isBatteryOptimized) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (!isBatteryOptimized) Color(0xFF34A853) else Color(0xFFF9AB00),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Background Battery Optimization",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (!isBatteryOptimized) "Unrestricted (Recommended)" else "Optimization Enabled (Restricted)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (!isBatteryOptimized) Color(0xFF34A853) else Color(0xFFEA4335)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (!isBatteryOptimized) Color(0xFFE6F4EA) else Color(0xFFFEF7E0),
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = if (!isBatteryOptimized) "Active" else "Action Needed",
                            color = if (!isBatteryOptimized) Color(0xFF137333) else Color(0xFFB06000),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (!isBatteryOptimized)
                        "CamBG Record is exempt from battery saver constraints. Background video recordings will run without interruption when screen is locked."
                    else
                        "Battery optimizations are active for CamBG Record. Android OS may terminate or pause background video recordings when the screen is turned off.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showBatteryDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("battery_optimization_button")
                ) {
                    Icon(
                        imageVector = if (!isBatteryOptimized) Icons.Default.Shield else Icons.Default.BatteryAlert,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!isBatteryOptimized) "View Optimization Details" else "Disable Battery Optimization"
                    )
                }
            }
        }
    }

    if (showBatteryDialog) {
        SettingsBatteryDialog(
            context = context,
            isBatteryOptimized = isBatteryOptimized,
            onDismissRequest = { showBatteryDialog = false }
        )
    }
}

