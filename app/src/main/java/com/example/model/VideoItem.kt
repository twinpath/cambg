package com.example.model

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

val mockVideoItems = listOf(
    VideoItem(
        id = "1",
        title = "VID_20260812_101230.mp4",
        duration = "02:34",
        resolution = "1080p",
        dateText = "Today, 10:12 AM",
        timestamp = System.currentTimeMillis() - 3600000,
        cameraType = "Back Camera",
        sizeMb = 145.2,
        gradientColors = listOf(0xFF1A73E8, 0xFF004BA0)
    ),
    VideoItem(
        id = "2",
        title = "VID_20260812_084512.mp4",
        duration = "12:05",
        resolution = "1080p",
        dateText = "Today, 8:45 AM",
        timestamp = System.currentTimeMillis() - 7200000,
        cameraType = "Front Camera",
        sizeMb = 680.0,
        gradientColors = listOf(0xFF00897B, 0xFF004D40)
    ),
    VideoItem(
        id = "3",
        title = "VID_20260811_221500.mp4",
        duration = "05:42",
        resolution = "4K",
        dateText = "Yesterday, 10:15 PM",
        timestamp = System.currentTimeMillis() - 86400000,
        cameraType = "Back Camera",
        sizeMb = 890.5,
        gradientColors = listOf(0xFFF9AB00, 0xFFB26A00)
    ),
    VideoItem(
        id = "4",
        title = "VID_20260810_183011.mp4",
        duration = "01:15",
        resolution = "720p",
        dateText = "Aug 10, 6:30 PM",
        timestamp = System.currentTimeMillis() - 172800000,
        cameraType = "Front Camera",
        sizeMb = 42.0,
        gradientColors = listOf(0xFFEA4335, 0xFFB31412)
    ),
    VideoItem(
        id = "5",
        title = "VID_20260809_140022.mp4",
        duration = "45:10",
        resolution = "1080p",
        dateText = "Aug 9, 2:00 PM",
        timestamp = System.currentTimeMillis() - 259200000,
        cameraType = "Back Camera",
        sizeMb = 2300.0,
        gradientColors = listOf(0xFF673AB7, 0xFF311B92)
    ),
    VideoItem(
        id = "6",
        title = "VID_20260808_091105.mp4",
        duration = "08:18",
        resolution = "1080p",
        dateText = "Aug 8, 9:11 AM",
        timestamp = System.currentTimeMillis() - 345600000,
        cameraType = "Back Camera",
        sizeMb = 480.0,
        gradientColors = listOf(0xFF009688, 0xFF004D40)
    )
)
