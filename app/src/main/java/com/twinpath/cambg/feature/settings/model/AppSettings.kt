package com.twinpath.cambg.feature.settings.model

import com.twinpath.cambg.core.constant.VideoConstants
import com.twinpath.cambg.core.constant.AudioConstants
import com.twinpath.cambg.core.constant.StorageConstants
import com.twinpath.cambg.core.constant.LanguageConstants

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH(LanguageConstants.LANG_EN, LanguageConstants.DISPLAY_EN),
    INDONESIAN(LanguageConstants.LANG_IN, LanguageConstants.DISPLAY_IN)
}

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
    val language: AppLanguage = AppLanguage.ENGLISH,
    val updateChannel: UpdateChannel = UpdateChannel.STABLE,
    val autoCheckUpdates: Boolean = true
)
