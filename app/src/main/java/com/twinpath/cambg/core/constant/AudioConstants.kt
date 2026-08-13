package com.twinpath.cambg.core.constant

object AudioConstants {
    const val SOURCE_CAMCORDER = "Camcorder"
    const val SOURCE_MICROPHONE = "Microphone"
    val AUDIO_SOURCE_OPTIONS = listOf(SOURCE_CAMCORDER, SOURCE_MICROPHONE)
    const val DEFAULT_AUDIO_SOURCE = SOURCE_CAMCORDER

    const val CHANNEL_STEREO = "Stereo"
    const val CHANNEL_MONO = "Mono"
    val AUDIO_CHANNEL_OPTIONS = listOf(CHANNEL_STEREO, CHANNEL_MONO)
    const val DEFAULT_AUDIO_CHANNELS = CHANNEL_STEREO

    const val DEFAULT_AUDIO_ENABLED = true
}
