package com.twinpath.cambg.model

import com.twinpath.cambg.constant.VideoConstants
import com.twinpath.cambg.constant.AudioConstants
import com.twinpath.cambg.constant.StorageConstants
import com.twinpath.cambg.constant.DetectionConstants

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
    val resolution: String = VideoConstants.DEFAULT_RESOLUTION,
    val frameRate: String = VideoConstants.DEFAULT_FPS,
    val bitrate: String = VideoConstants.DEFAULT_BITRATE,
    val aspectRatio: String = VideoConstants.DEFAULT_ASPECT_RATIO,
    val audioEnabled: Boolean = AudioConstants.DEFAULT_AUDIO_ENABLED,
    val audioSource: String = AudioConstants.DEFAULT_AUDIO_SOURCE,
    val audioChannels: String = AudioConstants.DEFAULT_AUDIO_CHANNELS,
    val storageLocation: StorageLocation = StorageLocation.PUBLIC_DCIM,
    val customStoragePath: String = StorageConstants.DEFAULT_CUSTOM_STORAGE_PATH,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val motionDetectionEnabled: Boolean = DetectionConstants.DEFAULT_MOTION_DETECTION_ENABLED,
    val motionSensitivity: Float = DetectionConstants.DEFAULT_MOTION_SENSITIVITY, // 0.0 - Low, 0.5 - Medium, 1.0 - High
    val personDetectionEnabled: Boolean = DetectionConstants.DEFAULT_PERSON_DETECTION_ENABLED,
    val personConfidenceThreshold: Float = DetectionConstants.DEFAULT_PERSON_CONFIDENCE_THRESHOLD
)
