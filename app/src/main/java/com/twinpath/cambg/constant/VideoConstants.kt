package com.twinpath.cambg.constant

object VideoConstants {
    const val RESOLUTION_480P = "480p"
    const val RESOLUTION_720P = "720p"
    const val RESOLUTION_1080P = "1080p"
    const val RESOLUTION_4K = "4K"
    val RESOLUTION_OPTIONS = listOf(RESOLUTION_480P, RESOLUTION_720P, RESOLUTION_1080P, RESOLUTION_4K)
    const val DEFAULT_RESOLUTION = RESOLUTION_720P

    const val FPS_24 = "24 fps"
    const val FPS_30 = "30 fps"
    const val FPS_60 = "60 fps"
    val FPS_OPTIONS = listOf(FPS_24, FPS_30, FPS_60)
    const val DEFAULT_FPS = FPS_30

    const val BITRATE_AUTO = "Auto"
    const val BITRATE_MEDIUM = "Medium"
    const val BITRATE_HIGH = "High"
    val BITRATE_OPTIONS = listOf(BITRATE_AUTO, BITRATE_MEDIUM, BITRATE_HIGH)
    const val DEFAULT_BITRATE = BITRATE_MEDIUM

    const val ASPECT_RATIO_9_16 = "9:16"
    const val ASPECT_RATIO_3_4 = "3:4"
    const val ASPECT_RATIO_FULL = "Full Screen"
    val ASPECT_RATIO_OPTIONS = listOf(ASPECT_RATIO_9_16, ASPECT_RATIO_3_4, ASPECT_RATIO_FULL)
    const val DEFAULT_ASPECT_RATIO = ASPECT_RATIO_9_16

    fun mapFpsStringToVal(fpsStr: String): Int {
        return when (fpsStr) {
            FPS_24 -> 24
            FPS_60 -> 60
            else -> 30
        }
    }

    fun mapBitrateStringToVal(bitrateStr: String, resolution: String): Int {
        val isHigh = bitrateStr == BITRATE_HIGH
        return when (resolution) {
            RESOLUTION_4K -> if (isHigh) 35 * 1024 * 1024 else 20 * 1024 * 1024
            RESOLUTION_1080P -> if (isHigh) 12 * 1024 * 1024 else 6 * 1024 * 1024
            RESOLUTION_720P -> if (isHigh) 6 * 1024 * 1024 else 3 * 1024 * 1024
            else -> if (isHigh) 3 * 1024 * 1024 else 15 * 1024 * 1024 / 10
        }
    }
}
