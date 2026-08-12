package com.twinpath.cambg_record.model

enum class DetectionType {
    MOTION,
    PERSON
}

data class DetectionEvent(
    val id: String,
    val type: DetectionType,
    val title: String,
    val timestamp: Long,
    val timeFormatted: String,
    val confidenceText: String
)
