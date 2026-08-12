package com.example.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun updateResolution(newRes: String) {
        _settings.update { it.copy(resolution = newRes) }
    }

    fun updateFrameRate(newFps: String) {
        _settings.update { it.copy(frameRate = newFps) }
    }

    fun updateBitrate(newBitrate: String) {
        _settings.update { it.copy(bitrate = newBitrate) }
    }

    fun toggleAudio() {
        _settings.update { it.copy(audioEnabled = !it.audioEnabled) }
    }

    fun updateAudioSource(newSource: String) {
        _settings.update { it.copy(audioSource = newSource) }
    }

    fun updateAudioChannels(newChannels: String) {
        _settings.update { it.copy(audioChannels = newChannels) }
    }

    fun updateThemeMode(newMode: AppThemeMode) {
        _settings.update { it.copy(themeMode = newMode) }
    }

    fun toggleDynamicColor() {
        _settings.update { it.copy(dynamicColor = !it.dynamicColor) }
    }
}
