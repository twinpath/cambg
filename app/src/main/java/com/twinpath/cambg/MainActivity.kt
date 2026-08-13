package com.twinpath.cambg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.twinpath.cambg.feature.settings.model.AppThemeMode
import com.twinpath.cambg.feature.camera.model.CameraViewModel
import com.twinpath.cambg.feature.gallery.model.GalleryViewModel
import com.twinpath.cambg.feature.settings.model.SettingsViewModel
import com.twinpath.cambg.navigation.MainAppNavigation
import com.twinpath.cambg.core.theme.CamBGRecordTheme
import com.twinpath.cambg.core.util.LocaleHelper

class MainActivity : ComponentActivity() {
    private val cameraViewModel: CameraViewModel by viewModels()
    private val galleryViewModel: GalleryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.isSoundEffectsEnabled = false
        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            val context = LocalContext.current

            // Update locale dynamically when settings language changes
            LaunchedEffect(settings.language) {
                LocaleHelper.applyLanguage(context, settings.language)
            }

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
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

