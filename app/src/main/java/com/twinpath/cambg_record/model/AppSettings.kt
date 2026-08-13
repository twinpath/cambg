package com.twinpath.cambg_record.model

import com.twinpath.cambg_record.util.AppConstants

enum class AppThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class StorageLocation(val displayName: String, val description: String) {
    PUBLIC_DCIM(
        displayName = "DCIM/CamBGRecord (Public)",
        description = "Visible in Gallery, Photos, and File Manager"
    ),
    SD_CARD(
        displayName = "SD Card (External)",
        description = "Save recordings to the external SD card"
    ),
    INTERNAL_PRIVATE(
        displayName = "Internal App Storage (Private)",
        description = "Only visible inside this app"
    ),
    CUSTOM(
        displayName = "Custom Location (Public)",
        description = "Save recordings to a custom folder path"
    )
}

data class AppSettings(
    val resolution: String = AppConstants.DEFAULT_RESOLUTION,
    val frameRate: String = AppConstants.DEFAULT_FPS,
    val bitrate: String = AppConstants.DEFAULT_BITRATE,
    val aspectRatio: String = AppConstants.DEFAULT_ASPECT_RATIO,
    val audioEnabled: Boolean = AppConstants.DEFAULT_AUDIO_ENABLED,
    val audioSource: String = AppConstants.DEFAULT_AUDIO_SOURCE,
    val audioChannels: String = AppConstants.DEFAULT_AUDIO_CHANNELS,
    val storageLocation: StorageLocation = StorageLocation.PUBLIC_DCIM,
    val customStoragePath: String = AppConstants.DEFAULT_CUSTOM_STORAGE_PATH,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val motionDetectionEnabled: Boolean = false,
    val motionSensitivity: Float = 0.5f, // 0.0 - Low, 0.5 - Medium, 1.0 - High
    val personDetectionEnabled: Boolean = false,
    val personConfidenceThreshold: Float = 0.75f
)

