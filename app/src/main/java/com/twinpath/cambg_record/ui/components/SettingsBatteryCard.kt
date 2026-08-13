package com.twinpath.cambg_record.ui.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Launch
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

fun checkIsBatteryOptimizationIgnored(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        pm?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    } else {
        true
    }
}

fun getOEMBatteryIntents(context: Context): List<Intent> {
    val intents = mutableListOf<Intent>()
    val packageName = context.packageName
    val manufacturer = Build.MANUFACTURER.lowercase()

    when {
        manufacturer.contains("xiaomi") -> {
            intents.add(Intent().apply {
                component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            })
            intents.add(Intent().apply {
                component = ComponentName("com.miui.securitycenter", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity")
                putExtra("package_name", packageName)
                putExtra("package_label", context.applicationInfo.loadLabel(context.packageManager).toString())
            })
        }
        manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> {
            intents.add(Intent().apply {
                component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")
            })
            intents.add(Intent().apply {
                component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")
            })
            intents.add(Intent().apply {
                component = ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")
            })
            intents.add(Intent().apply {
                action = "com.coloros.oppoguardelf.intent.action.APP_STATUS"
                putExtra("package_name", packageName)
            })
        }
        manufacturer.contains("vivo") -> {
            intents.add(Intent().apply {
                component = ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
            })
            intents.add(Intent().apply {
                component = ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")
            })
        }
        manufacturer.contains("huawei") || manufacturer.contains("honor") -> {
            intents.add(Intent().apply {
                component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
            })
            intents.add(Intent().apply {
                component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
            })
        }
        manufacturer.contains("samsung") -> {
            intents.add(Intent().apply {
                component = ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")
            })
            intents.add(Intent().apply {
                component = ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")
            })
        }
    }
    return intents
}

fun launchBatteryOptimizationSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val oemIntents = getOEMBatteryIntents(context)
        for (intent in oemIntents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // Ignore and try next OEM intent
            }
        }

        // Fallback 1: Action Request Ignore Battery Optimizations
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            // Ignore and try next
        }

        // Fallback 2: General Ignore Battery Optimization Settings list
        try {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            // Ignore and try next
        }

        // Fallback 3: App Details Info
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            // Ignore and try next
        }
    }

    // Ultimate Fallback: System Settings
    try {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Last resort
    }
}

fun getBatteryInstructionSteps(): String {
    val manufacturer = Build.MANUFACTURER.lowercase()
    return when {
        manufacturer.contains("xiaomi") -> {
            "1. Tap 'Open System Settings' below.\n" +
            "2. Under Battery Saver, select 'No restrictions'.\n" +
            "3. Enable 'Autostart' in permissions if available.\n" +
            "4. Return to CamBG Record."
        }
        manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> {
            "1. Tap 'Open System Settings' below.\n" +
            "2. Select 'Don't optimize' (or choose 'Unrestricted').\n" +
            "3. Ensure 'Allow background activity' & 'Allow auto-launch' are enabled.\n" +
            "4. Return to CamBG Record."
        }
        manufacturer.contains("samsung") -> {
            "1. Tap 'Open System Settings' below.\n" +
            "2. Go to Battery and choose 'Unrestricted'.\n" +
            "3. Return to CamBG Record."
        }
        manufacturer.contains("vivo") -> {
            "1. Tap 'Open System Settings' below.\n" +
            "2. Set background power consumption to 'Don't restrict background power consumption' or enable 'High background power consumption'.\n" +
            "3. Return to CamBG Record."
        }
        else -> {
            "1. Tap 'Open System Settings' below.\n" +
            "2. Select 'Unrestricted', 'Allow', or 'Don't optimize'.\n" +
            "3. Return to CamBG Record."
        }
    }
}

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
        AlertDialog(
            onDismissRequest = { showBatteryDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.BatterySaver,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Uninterrupted Background Recording",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Android automatically throttles or stops background apps when battery optimization is enabled, which can stop video recordings mid-capture when your screen is locked.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "How to disable optimization:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = getBatteryInstructionSteps(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (!isBatteryOptimized) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (!isBatteryOptimized) Color(0xFF34A853) else Color(0xFFF9AB00),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!isBatteryOptimized)
                                "Current Status: Unrestricted (Good to go!)"
                            else
                                "Current Status: Optimization Enabled (Action Needed)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        launchBatteryOptimizationSettings(context)
                        showBatteryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open System Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatteryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
