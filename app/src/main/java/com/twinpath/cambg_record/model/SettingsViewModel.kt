package com.twinpath.cambg_record.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.twinpath.cambg_record.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

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
}

