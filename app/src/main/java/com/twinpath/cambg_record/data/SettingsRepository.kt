package com.twinpath.cambg_record.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.twinpath.cambg_record.model.AppSettings
import com.twinpath.cambg_record.model.AppThemeMode
import com.twinpath.cambg_record.model.StorageLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cambg_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val RESOLUTION = stringPreferencesKey("resolution")
        val FRAME_RATE = stringPreferencesKey("frame_rate")
        val BITRATE = stringPreferencesKey("bitrate")
        val AUDIO_ENABLED = booleanPreferencesKey("audio_enabled")
        val AUDIO_SOURCE = stringPreferencesKey("audio_source")
        val AUDIO_CHANNELS = stringPreferencesKey("audio_channels")
        val STORAGE_LOCATION = stringPreferencesKey("storage_location")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val MOTION_DETECTION_ENABLED = booleanPreferencesKey("motion_detection_enabled")
        val MOTION_SENSITIVITY = floatPreferencesKey("motion_sensitivity")
        val PERSON_DETECTION_ENABLED = booleanPreferencesKey("person_detection_enabled")
        val PERSON_CONFIDENCE_THRESHOLD = floatPreferencesKey("person_confidence_threshold")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val defaults = AppSettings()
        AppSettings(
            resolution = prefs[Keys.RESOLUTION] ?: defaults.resolution,
            frameRate = prefs[Keys.FRAME_RATE] ?: defaults.frameRate,
            bitrate = prefs[Keys.BITRATE] ?: defaults.bitrate,
            audioEnabled = prefs[Keys.AUDIO_ENABLED] ?: defaults.audioEnabled,
            audioSource = prefs[Keys.AUDIO_SOURCE] ?: defaults.audioSource,
            audioChannels = prefs[Keys.AUDIO_CHANNELS] ?: defaults.audioChannels,
            storageLocation = prefs[Keys.STORAGE_LOCATION]?.let {
                try { StorageLocation.valueOf(it) } catch (_: Exception) { defaults.storageLocation }
            } ?: defaults.storageLocation,
            themeMode = prefs[Keys.THEME_MODE]?.let {
                try { AppThemeMode.valueOf(it) } catch (_: Exception) { defaults.themeMode }
            } ?: defaults.themeMode,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: defaults.dynamicColor,
            motionDetectionEnabled = prefs[Keys.MOTION_DETECTION_ENABLED] ?: defaults.motionDetectionEnabled,
            motionSensitivity = prefs[Keys.MOTION_SENSITIVITY] ?: defaults.motionSensitivity,
            personDetectionEnabled = prefs[Keys.PERSON_DETECTION_ENABLED] ?: defaults.personDetectionEnabled,
            personConfidenceThreshold = prefs[Keys.PERSON_CONFIDENCE_THRESHOLD] ?: defaults.personConfidenceThreshold
        )
    }

    suspend fun updateResolution(value: String) {
        context.dataStore.edit { it[Keys.RESOLUTION] = value }
    }

    suspend fun updateFrameRate(value: String) {
        context.dataStore.edit { it[Keys.FRAME_RATE] = value }
    }

    suspend fun updateBitrate(value: String) {
        context.dataStore.edit { it[Keys.BITRATE] = value }
    }

    suspend fun updateAudioEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.AUDIO_ENABLED] = value }
    }

    suspend fun updateAudioSource(value: String) {
        context.dataStore.edit { it[Keys.AUDIO_SOURCE] = value }
    }

    suspend fun updateAudioChannels(value: String) {
        context.dataStore.edit { it[Keys.AUDIO_CHANNELS] = value }
    }

    suspend fun updateStorageLocation(value: StorageLocation) {
        context.dataStore.edit { it[Keys.STORAGE_LOCATION] = value.name }
    }

    suspend fun updateThemeMode(value: AppThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = value.name }
    }

    suspend fun updateDynamicColor(value: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = value }
    }

    suspend fun updateMotionDetectionEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.MOTION_DETECTION_ENABLED] = value }
    }

    suspend fun updateMotionSensitivity(value: Float) {
        context.dataStore.edit { it[Keys.MOTION_SENSITIVITY] = value }
    }

    suspend fun updatePersonDetectionEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.PERSON_DETECTION_ENABLED] = value }
    }

    suspend fun updatePersonConfidenceThreshold(value: Float) {
        context.dataStore.edit { it[Keys.PERSON_CONFIDENCE_THRESHOLD] = value }
    }
}
