package com.twinpath.cambg.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.twinpath.cambg.feature.settings.model.AppSettings
import com.twinpath.cambg.feature.settings.model.AppThemeMode
import com.twinpath.cambg.feature.settings.model.StorageLocation
import com.twinpath.cambg.feature.settings.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.twinpath.cambg.core.constant.PreferenceKeys
import com.twinpath.cambg.core.constant.UpdateConstants
import com.twinpath.cambg.feature.settings.model.UpdateChannel


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
        val UPDATE_CHANNEL = stringPreferencesKey(UpdateConstants.KEY_UPDATE_CHANNEL)
        val AUTO_CHECK_UPDATES = booleanPreferencesKey(UpdateConstants.KEY_AUTO_CHECK_UPDATES)
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
                 try { AppLanguage.valueOf(it) } catch (_: Exception) { defaults.language }
             } ?: defaults.language,
             updateChannel = prefs[Keys.UPDATE_CHANNEL]?.let {
                 try { UpdateChannel.valueOf(it) } catch (_: Exception) { defaults.updateChannel }
             } ?: defaults.updateChannel,
             autoCheckUpdates = prefs[Keys.AUTO_CHECK_UPDATES] ?: defaults.autoCheckUpdates
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

    suspend fun updateLanguage(value: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = value.name }
    }

    suspend fun updateUpdateChannel(value: UpdateChannel) {
        context.dataStore.edit { it[Keys.UPDATE_CHANNEL] = value.name }
    }

    suspend fun updateAutoCheckUpdates(value: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_CHECK_UPDATES] = value }
    }
}
