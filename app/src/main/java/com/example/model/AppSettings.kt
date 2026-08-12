package com.example.model

enum class AppThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

data class AppSettings(
    val resolution: String = "1080p",
    val frameRate: String = "30 fps",
    val bitrate: String = "High",
    val audioEnabled: Boolean = true,
    val audioSource: String = "Camcorder",
    val audioChannels: String = "Stereo",
    val saveLocation: String = "/storage/emulated/0/Movies/CamBGRecord/",
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val motionDetectionEnabled: Boolean = false,
    val motionSensitivity: Float = 0.5f, // 0.0 - Low, 0.5 - Medium, 1.0 - High
    val personDetectionEnabled: Boolean = false,
    val personConfidenceThreshold: Float = 0.75f
)
