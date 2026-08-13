package com.twinpath.cambg.model

data class VideoItem(
    val id: String,
    val title: String,
    val duration: String,
    val resolution: String,
    val dateText: String,
    val timestamp: Long,
    val cameraType: String, // "Front Camera" or "Back Camera"
    val sizeMb: Double,
    val gradientColors: List<Long>,
    val filePath: String? = null
)

// No mock data - gallery shows only real recorded files
val mockVideoItems = emptyList<VideoItem>()

