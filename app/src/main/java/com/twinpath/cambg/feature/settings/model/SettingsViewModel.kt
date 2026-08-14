package com.twinpath.cambg.feature.settings.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.twinpath.cambg.core.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)
    val updateManager = com.twinpath.cambg.core.helper.UpdateManager(application.applicationContext)

    val updateState = updateManager.updateState

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun checkForUpdates(isManual: Boolean = false) {
        viewModelScope.launch {
            updateManager.checkForUpdates(settings.value.updateChannel, isManual)
        }
    }

    fun downloadAndInstallUpdate(asset: com.twinpath.cambg.core.data.model.UpdateAsset) {
        viewModelScope.launch {
            updateManager.downloadAndInstallApk(asset).collect { progress ->
                // Progress is automatically set on UpdateManager.updateState
            }
        }
    }

    fun triggerInstall(file: java.io.File) {
        updateManager.triggerInstall(file)
    }

    fun setReadyToInstall(file: java.io.File) {
        updateManager.setReadyToInstall(file)
    }

    fun updateUpdateArchPreference(newValue: UpdateArchitecturePreference) {
        viewModelScope.launch { repository.updateUpdateArchPreference(newValue) }
    }

    fun resetUpdateState() {
        updateManager.resetState()
    }

    fun updateUpdateChannel(newChannel: UpdateChannel) {
        viewModelScope.launch { repository.updateUpdateChannel(newChannel) }
    }

    fun toggleAutoCheckUpdates() {
        viewModelScope.launch {
            repository.updateAutoCheckUpdates(!settings.value.autoCheckUpdates)
        }
    }

    fun updateResolution(newRes: String) {
        viewModelScope.launch { repository.updateResolution(newRes) }
    }

    fun updateFrameRate(newFps: String) {
        viewModelScope.launch { repository.updateFrameRate(newFps) }
    }

    fun updateBitrate(newBitrate: String) {
        viewModelScope.launch { repository.updateBitrate(newBitrate) }
    }

    fun updateAspectRatio(newRatio: String) {
        viewModelScope.launch { repository.updateAspectRatio(newRatio) }
    }

    fun toggleAudio() {
        viewModelScope.launch {
            repository.updateAudioEnabled(!settings.value.audioEnabled)
        }
    }

    fun updateAudioSource(newSource: String) {
        viewModelScope.launch { repository.updateAudioSource(newSource) }
    }

    fun updateAudioChannels(newChannels: String) {
        viewModelScope.launch { repository.updateAudioChannels(newChannels) }
    }

    fun updateStorageLocation(newLocation: StorageLocation) {
        viewModelScope.launch { repository.updateStorageLocation(newLocation) }
    }

    fun updateCustomStoragePath(newPath: String) {
        viewModelScope.launch { repository.updateCustomStoragePath(newPath) }
    }

    fun updateThemeMode(newMode: AppThemeMode) {
        viewModelScope.launch { repository.updateThemeMode(newMode) }
    }

    fun toggleDynamicColor() {
        viewModelScope.launch {
            repository.updateDynamicColor(!settings.value.dynamicColor)
        }
    }

    fun updateLanguage(newLanguage: AppLanguage) {
        viewModelScope.launch { repository.updateLanguage(newLanguage) }
    }
}

