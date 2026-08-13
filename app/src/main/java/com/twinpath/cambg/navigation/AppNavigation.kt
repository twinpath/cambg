package com.twinpath.cambg.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.twinpath.cambg.feature.camera.model.CameraViewModel
import com.twinpath.cambg.feature.gallery.model.GalleryViewModel
import com.twinpath.cambg.feature.settings.model.SettingsViewModel
import com.twinpath.cambg.feature.camera.screen.CameraScreen
import com.twinpath.cambg.feature.gallery.screen.GalleryScreen
import com.twinpath.cambg.feature.settings.screen.SettingsScreen

enum class Screen {
    CAMERA,
    GALLERY,
    SETTINGS
}

@Composable
fun MainAppNavigation(
    cameraViewModel: CameraViewModel,
    galleryViewModel: GalleryViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.CAMERA) }

    val cameraState by cameraViewModel.uiState.collectAsState()
    val galleryState by galleryViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.settings.collectAsState()
    val updateState by settingsViewModel.updateState.collectAsState(initial = com.twinpath.cambg.core.helper.UpdateState.Idle)

    val context = LocalContext.current

    LaunchedEffect(currentScreen) {
        if (currentScreen == Screen.GALLERY) {
            galleryViewModel.loadRecordedVideos(context)
        }
    }

    var hasCheckedUpdates by remember { mutableStateOf(false) }
    LaunchedEffect(settingsState.autoCheckUpdates) {
        if (settingsState.autoCheckUpdates && !hasCheckedUpdates) {
            hasCheckedUpdates = true
            settingsViewModel.checkForUpdates()
        }
    }

    LaunchedEffect(Unit) {
        galleryViewModel.loadRecordedVideos(context)
    }

    LaunchedEffect(settingsState.resolution, settingsState.audioEnabled, settingsState.aspectRatio) {
        cameraViewModel.setQuality(settingsState.resolution)
        cameraViewModel.setAspectRatio(settingsState.aspectRatio)
        if (cameraState.isAudioEnabled != settingsState.audioEnabled) {
            cameraViewModel.toggleAudio()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.CAMERA -> CameraScreen(
                    uiState = cameraState,
                    appSettings = settingsState,
                    onStartRecord = { cameraViewModel.startRecording() },
                    onPauseRecord = { cameraViewModel.pauseRecording() },
                    onResumeRecord = { cameraViewModel.resumeRecording() },
                    onStopRecord = {
                        val duration = cameraViewModel.stopRecording()
                        galleryViewModel.addRecordedVideo(
                            durationSeconds = duration,
                            quality = cameraState.quality,
                            isFrontCamera = cameraState.isFrontCamera,
                            filePath = cameraState.lastSavedFilePath
                        )
                    },
                    onVideoSaved = { file, fileSize ->
                        cameraViewModel.onVideoSaved(file.absolutePath, fileSize)
                        galleryViewModel.addRecordedVideo(
                            durationSeconds = cameraState.elapsedTimeSeconds,
                            quality = cameraState.quality,
                            isFrontCamera = cameraState.isFrontCamera,
                            filePath = file.absolutePath,
                            fileSizeBytes = fileSize
                        )
                    },
                    onToggleCamera = { cameraViewModel.toggleCamera() },
                    onCycleFlash = { cameraViewModel.cycleFlash() },
                    onSetQuality = {
                        cameraViewModel.setQuality(it)
                        settingsViewModel.updateResolution(it)
                    },
                    onToggleAudio = {
                        cameraViewModel.toggleAudio()
                        settingsViewModel.toggleAudio()
                    },
                    onSetZoomRatio = { cameraViewModel.setZoomRatio(it) },
                    onToggleGrid = { cameraViewModel.toggleGridOverlay() },
                    onToggleStealth = { cameraViewModel.toggleStealthMode() },
                    onClickGallery = { currentScreen = Screen.GALLERY },
                    onClickSettings = { currentScreen = Screen.SETTINGS }
                )

                Screen.GALLERY -> GalleryScreen(
                    videos = galleryViewModel.getFilteredVideos(),
                    searchQuery = galleryState.searchQuery,
                    isSearchExpanded = galleryState.isSearchExpanded,
                    selectedFilter = galleryState.selectedFilter,
                    isGridView = galleryState.isGridView,
                    selectedVideoForPreview = galleryState.selectedVideoForPreview,
                    onSearchQueryChange = { galleryViewModel.updateSearchQuery(it) },
                    onToggleSearch = { galleryViewModel.toggleSearchExpanded() },
                    onSelectFilter = { galleryViewModel.selectFilter(it) },
                    onToggleViewMode = { galleryViewModel.toggleViewMode() },
                    onSelectVideo = { galleryViewModel.selectVideoForPreview(it) },
                    onDeleteVideo = { galleryViewModel.deleteVideo(context, it) },
                    onBack = { currentScreen = Screen.CAMERA }
                )

                 Screen.SETTINGS -> SettingsScreen(
                    settings = settingsState,
                    updateState = updateState,
                    onUpdateResolution = { settingsViewModel.updateResolution(it) },
                    onUpdateFrameRate = { settingsViewModel.updateFrameRate(it) },
                    onUpdateBitrate = { settingsViewModel.updateBitrate(it) },
                    onUpdateAspectRatio = { settingsViewModel.updateAspectRatio(it) },
                    onToggleAudio = { settingsViewModel.toggleAudio() },
                    onUpdateAudioSource = { settingsViewModel.updateAudioSource(it) },
                    onUpdateAudioChannels = { settingsViewModel.updateAudioChannels(it) },
                    onUpdateStorageLocation = { settingsViewModel.updateStorageLocation(it) },
                    onUpdateCustomStoragePath = { settingsViewModel.updateCustomStoragePath(it) },
                    onUpdateThemeMode = { settingsViewModel.updateThemeMode(it) },
                    onToggleDynamicColor = { settingsViewModel.toggleDynamicColor() },
                    onUpdateLanguage = { settingsViewModel.updateLanguage(it) },
                    onUpdateUpdateChannel = { settingsViewModel.updateUpdateChannel(it) },
                    onToggleAutoCheck = { settingsViewModel.toggleAutoCheckUpdates() },
                    onCheckForUpdates = { settingsViewModel.checkForUpdates() },
                    onDownloadAndInstallUpdate = { settingsViewModel.downloadAndInstallUpdate(it) },
                    onTriggerInstall = { settingsViewModel.triggerInstall(it) },
                    onResetUpdateState = { settingsViewModel.resetUpdateState() },
                    onBack = { currentScreen = Screen.CAMERA }
                )
            }
        }
    }
}
