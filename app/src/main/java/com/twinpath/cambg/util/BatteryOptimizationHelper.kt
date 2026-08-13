package com.twinpath.cambg.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

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

fun hasOEMBatterySettings(context: Context): Boolean {
    val manufacturer = Build.MANUFACTURER.lowercase()
    return manufacturer.contains("xiaomi") ||
           manufacturer.contains("oppo") ||
           manufacturer.contains("realme") ||
           manufacturer.contains("oneplus") ||
           manufacturer.contains("vivo") ||
           manufacturer.contains("huawei") ||
           manufacturer.contains("honor") ||
           manufacturer.contains("samsung")
}

fun launchStandardBatteryOptimizationSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

fun launchOEMBatterySettings(context: Context) {
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

    // Fallback: App Details Settings
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            // Ignore
        }
    }
}

fun getBatteryInstructionSteps(): String {
    val manufacturer = Build.MANUFACTURER.lowercase()
    return when {
        manufacturer.contains("xiaomi") -> {
            "1. Tap 'Disable Optimization (Standard)' -> Select 'Allow' to bypass Doze (turns status to Active).\n" +
            "2. Tap 'Open Custom OEM Settings' -> Under Battery Saver, select 'No restrictions'. Enable 'Autostart' if available."
        }
        manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> {
            "1. Tap 'Disable Optimization (Standard)' -> Select 'Allow' or set to 'Unrestricted' (turns status to Active).\n" +
            "2. Tap 'Open Custom OEM Settings' -> Ensure 'Allow background activity' & 'Allow auto-launch' are enabled."
        }
        manufacturer.contains("samsung") -> {
            "1. Tap 'Disable Optimization (Standard)' -> Select 'Allow' or set to 'Unrestricted' (turns status to Active).\n" +
            "2. Tap 'Open Custom OEM Settings' -> Go to Battery and choose 'Unrestricted'."
        }
        manufacturer.contains("vivo") -> {
            "1. Tap 'Disable Optimization (Standard)' -> Select 'Allow' (turns status to Active).\n" +
            "2. Tap 'Open Custom OEM Settings' -> Set background power consumption to 'Don't restrict background power consumption' or enable 'High background power consumption'."
        }
        else -> {
            "1. Tap 'Open System Settings' below.\n" +
            "2. Select 'Unrestricted', 'Allow', or 'Don't optimize'.\n" +
            "3. Return to CamBG Record."
        }
    }
}
