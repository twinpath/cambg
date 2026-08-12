package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.model.AppThemeMode
import com.example.model.CameraViewModel
import com.example.model.DetectionViewModel
import com.example.model.GalleryViewModel
import com.example.model.SettingsViewModel
import com.example.ui.navigation.MainAppNavigation
import com.example.ui.theme.CamBGRecordTheme

class MainActivity : ComponentActivity() {
    private val cameraViewModel: CameraViewModel by viewModels()
    private val galleryViewModel: GalleryViewModel by viewModels()
    private val detectionViewModel: DetectionViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsState()

            val darkTheme = when (settings.themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            CamBGRecordTheme(
                darkTheme = darkTheme,
                dynamicColor = settings.dynamicColor
            ) {
                MainAppNavigation(
                    cameraViewModel = cameraViewModel,
                    galleryViewModel = galleryViewModel,
                    detectionViewModel = detectionViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

