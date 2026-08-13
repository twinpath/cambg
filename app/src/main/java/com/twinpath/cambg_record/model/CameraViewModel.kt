package com.twinpath.cambg_record.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.twinpath.cambg_record.util.AppConstants

enum class RecordingState {
    IDLE,
    RECORDING,
    PAUSED
}

data class CameraUiState(
    val recordingState: RecordingState = RecordingState.IDLE,
    val elapsedTimeSeconds: Long = 0,
    val quality: String = AppConstants.DEFAULT_RESOLUTION,

    val isFrontCamera: Boolean = false,
    val flashMode: String = "OFF", // "OFF", "ON", "AUTO"
    val isAudioEnabled: Boolean = true,
    val zoomRatio: Float = 1.0f,
    val showGridOverlay: Boolean = true,
    val isStealthMode: Boolean = false,
    val lastSavedFilePath: String? = null,
    val statusMessage: String? = null
)

class CameraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            com.twinpath.cambg_record.service.BackgroundRecordingService.serviceState.collect { svcState ->
                if (svcState.isServiceRunning) {
                    _uiState.update { current ->
                        current.copy(
                            recordingState = svcState.recordingState,
                            elapsedTimeSeconds = svcState.elapsedTimeSeconds,
                            lastSavedFilePath = svcState.lastSavedFilePath ?: current.lastSavedFilePath,
                            statusMessage = svcState.statusMessage ?: current.statusMessage
                        )
                    }
                } else if (svcState.lastSavedFilePath != null && svcState.lastSavedFilePath != _uiState.value.lastSavedFilePath) {
                    onVideoSaved(svcState.lastSavedFilePath, svcState.lastSavedFileSize)
                }
            }
        }
    }

    fun onVideoSaved(filePath: String, fileSize: Long) {
        val fileName = java.io.File(filePath).name
        val sizeMb = fileSize / (1024.0 * 1024.0)
        val msg = "Saved to internal storage: $fileName (${String.format(java.util.Locale.US, "%.1f", sizeMb)} MB)"
        _uiState.update {
            it.copy(
                lastSavedFilePath = filePath,
                statusMessage = msg
            )
        }
    }

    fun setStatusMessage(msg: String?) {
        _uiState.update { it.copy(statusMessage = msg) }
    }

    fun startRecording() {
        _uiState.update {
            it.copy(
                recordingState = RecordingState.RECORDING
            )
        }
        startTimer()
    }

    fun pauseRecording() {
        if (_uiState.value.recordingState == RecordingState.RECORDING) {
            _uiState.update { it.copy(recordingState = RecordingState.PAUSED) }
            stopTimer()
        }
    }

    fun resumeRecording() {
        if (_uiState.value.recordingState == RecordingState.PAUSED) {
            _uiState.update { it.copy(recordingState = RecordingState.RECORDING) }
            startTimer()
        }
    }

    fun stopRecording(): Long {
        val duration = _uiState.value.elapsedTimeSeconds
        stopTimer()
        _uiState.update {
            it.copy(
                recordingState = RecordingState.IDLE,
                elapsedTimeSeconds = 0
            )
        }
        return duration
    }

    fun toggleCamera() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun cycleFlash() {
        _uiState.update {
            val nextFlash = when (it.flashMode) {
                "OFF" -> "ON"
                "ON" -> "AUTO"
                else -> "OFF"
            }
            it.copy(flashMode = nextFlash)
        }
    }

    fun setQuality(newQuality: String) {
        _uiState.update { it.copy(quality = newQuality) }
    }

    fun toggleAudio() {
        _uiState.update { it.copy(isAudioEnabled = !it.isAudioEnabled) }
    }

    fun setZoomRatio(zoom: Float) {
        _uiState.update { it.copy(zoomRatio = zoom.coerceIn(1.0f, 5.0f)) }
    }

    fun toggleGridOverlay() {
        _uiState.update { it.copy(showGridOverlay = !it.showGridOverlay) }
    }

    fun toggleStealthMode() {
        _uiState.update { it.copy(isStealthMode = !it.isStealthMode) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.recordingState == RecordingState.RECORDING) {
                    _uiState.update { it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1) }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
