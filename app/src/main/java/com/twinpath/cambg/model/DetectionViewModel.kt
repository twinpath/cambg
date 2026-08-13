package com.twinpath.cambg.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DetectionUiState(
    val isMotionEnabled: Boolean = false,
    val motionSensitivity: Float = 0.5f, // 0.0=Low, 0.5=Medium, 1.0=High
    val isPersonEnabled: Boolean = false,
    val personConfidence: Float = 0.75f, // 0.5 to 0.95
    val events: List<DetectionEvent> = emptyList()
)

class DetectionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun toggleMotionDetection() {
        _uiState.update { current ->
            val nextState = !current.isMotionEnabled
            current.copy(isMotionEnabled = nextState)
        }
    }

    fun setMotionSensitivity(value: Float) {
        _uiState.update { it.copy(motionSensitivity = value) }
    }

    fun togglePersonDetection() {
        _uiState.update { current ->
            val nextState = !current.isPersonEnabled
            current.copy(isPersonEnabled = nextState)
        }
    }

    fun setPersonConfidence(value: Float) {
        _uiState.update { it.copy(personConfidence = value) }
    }

    fun addTestDetectionEvent(type: DetectionType) {
        val currentTime = System.currentTimeMillis()
        val timeStr = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val newEvent = DetectionEvent(
            id = currentTime.toString(),
            type = type,
            title = if (type == DetectionType.MOTION) "Motion Detected in Center Zone" else "Person Identified (Human)",
            timestamp = currentTime,
            timeFormatted = "Today at $timeStr",
            confidenceText = if (type == DetectionType.MOTION) "High Sensitivity trigger" else "${(_uiState.value.personConfidence * 100).toInt()}% Confidence"
        )
        _uiState.update { it.copy(events = listOf(newEvent) + it.events) }
    }

    fun clearEvents() {
        _uiState.update { it.copy(events = emptyList()) }
    }
}
