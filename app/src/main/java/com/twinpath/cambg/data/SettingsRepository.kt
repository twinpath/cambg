package com.twinpath.cambg.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.twinpath.cambg.model.AppSettings
import com.twinpath.cambg.model.AppThemeMode
import com.twinpath.cambg.model.StorageLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.twinpath.cambg.constant.PreferenceKeys

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cambg_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val RESOLUTION = stringPreferencesKey(PreferenceKeys.KEY_RESOLUTION)
        val FRAME_RATE = stringPreferencesKey(PreferenceKeys.KEY_FRAME_RATE)
        val BITRATE = stringPreferencesKey(PreferenceKeys.KEY_BITRATE)
        val ASPECT_RATIO = stringPreferencesKey(PreferenceKeys.KEY_ASPECT_RATIO)
        val AUDIO_ENABLED = booleanPreferencesKey(PreferenceKeys.KEY_AUDIO_ENABLED)
        val AUDIO_SOURCE = stringPreferencesKey(PreferenceKeys.KEY_AUDIO_SOURCE)
        val AUDIO_CHANNELS = stringPreferencesKey(PreferenceKeys.KEY_AUDIO_CHANNELS)
        val STORAGE_LOCATION = stringPreferencesKey(PreferenceKeys.KEY_STORAGE_LOCATION)
        val CUSTOM_STORAGE_PATH = stringPreferencesKey(PreferenceKeys.KEY_CUSTOM_STORAGE_PATH)
        val THEME_MODE = stringPreferencesKey(PreferenceKeys.KEY_THEME_MODE)
        val DYNAMIC_COLOR = booleanPreferencesKey(PreferenceKeys.KEY_DYNAMIC_COLOR)
        val LANGUAGE = stringPreferencesKey(PreferenceKeys.KEY_LANGUAGE)
        val MOTION_DETECTION_ENABLED = booleanPreferencesKey(PreferenceKeys.KEY_MOTION_DETECTION_ENABLED)
        val MOTION_SENSITIVITY = floatPreferencesKey(PreferenceKeys.KEY_MOTION_SENSITIVITY)
        val PERSON_DETECTION_ENABLED = booleanPreferencesKey(PreferenceKeys.KEY_PERSON_DETECTION_ENABLED)
        val PERSON_CONFIDENCE_THRESHOLD = floatPreferencesKey(PreferenceKeys.KEY_PERSON_CONFIDENCE_THRESHOLD)
    }
 
     val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
         val defaults = AppSettings()
         AppSettings(
             resolution = prefs[Keys.RESOLUTION] ?: defaults.resolution,
             frameRate = prefs[Keys.FRAME_RATE] ?: defaults.frameRate,
             bitrate = prefs[Keys.BITRATE] ?: defaults.bitrate,
             aspectRatio = prefs[Keys.ASPECT_RATIO] ?: defaults.aspectRatio,
             audioEnabled = prefs[Keys.AUDIO_ENABLED] ?: defaults.audioEnabled,
             audioSource = prefs[Keys.AUDIO_SOURCE] ?: defaults.audioSource,
             audioChannels = prefs[Keys.AUDIO_CHANNELS] ?: defaults.audioChannels,
             storageLocation = prefs[Keys.STORAGE_LOCATION]?.let {
                 try { StorageLocation.valueOf(it) } catch (_: Exception) { defaults.storageLocation }
             } ?: defaults.storageLocation,
             customStoragePath = prefs[Keys.CUSTOM_STORAGE_PATH] ?: defaults.customStoragePath,
             themeMode = prefs[Keys.THEME_MODE]?.let {
                try { AppThemeMode.valueOf(it) } catch (_: Exception) { defaults.themeMode }
            } ?: defaults.themeMode,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: defaults.dynamicColor,
            language = prefs[Keys.LANGUAGE]?.let {
                try { com.twinpath.cambg.model.AppLanguage.valueOf(it) } catch (_: Exception) { defaults.language }
            } ?: defaults.language,
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

    suspend fun updateAspectRatio(value: String) {
        context.dataStore.edit { it[Keys.ASPECT_RATIO] = value }
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

    suspend fun updateCustomStoragePath(value: String) {
        context.dataStore.edit { it[Keys.CUSTOM_STORAGE_PATH] = value }
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

    suspend fun updateLanguage(value: com.twinpath.cambg.model.AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = value.name }
    }
}
