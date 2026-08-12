package com.twinpath.cambg_record.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.twinpath.cambg_record.model.CameraViewModel
import com.twinpath.cambg_record.model.DetectionViewModel
import com.twinpath.cambg_record.model.GalleryViewModel
import com.twinpath.cambg_record.model.SettingsViewModel
import com.twinpath.cambg_record.ui.camera.CameraScreen
import com.twinpath.cambg_record.ui.detection.DetectionScreen
import com.twinpath.cambg_record.ui.gallery.GalleryScreen
import com.twinpath.cambg_record.ui.settings.SettingsScreen

data class NavTabItem(
    val routeIndex: Int,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainAppNavigation(
    cameraViewModel: CameraViewModel,
    galleryViewModel: GalleryViewModel,
    detectionViewModel: DetectionViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val navTabs = listOf(
        NavTabItem(
            routeIndex = 0,
            title = "Camera",
            selectedIcon = Icons.Filled.Videocam,
            unselectedIcon = Icons.Outlined.Videocam,
            testTag = "nav_camera_tab"
        ),
        NavTabItem(
            routeIndex = 1,
            title = "Gallery",
            selectedIcon = Icons.Filled.VideoLibrary,
            unselectedIcon = Icons.Outlined.VideoLibrary,
            testTag = "nav_gallery_tab"
        ),
        NavTabItem(
            routeIndex = 2,
            title = "Detection",
            selectedIcon = Icons.Filled.Sensors,
            unselectedIcon = Icons.Outlined.Sensors,
            testTag = "nav_detection_tab"
        ),
        NavTabItem(
            routeIndex = 3,
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            testTag = "nav_settings_tab"
        )
    )

    val cameraState by cameraViewModel.uiState.collectAsState()
    val galleryState by galleryViewModel.uiState.collectAsState()
    val detectionState by detectionViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.settings.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            galleryViewModel.loadRecordedVideos(context)
        }
    }

    LaunchedEffect(Unit) {
        galleryViewModel.loadRecordedVideos(context)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                navTabs.forEach { tab ->
                    val isSelected = selectedTab == tab.routeIndex
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab.routeIndex },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> CameraScreen(
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
                    onSetQuality = { cameraViewModel.setQuality(it) },
                    onToggleAudio = { cameraViewModel.toggleAudio() },
                    onSetZoomRatio = { cameraViewModel.setZoomRatio(it) },
                    onToggleGrid = { cameraViewModel.toggleGridOverlay() },
                    onToggleStealth = { cameraViewModel.toggleStealthMode() },
                    onClickGallery = { selectedTab = 1 }
                )

                1 -> GalleryScreen(
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
                    onDeleteVideo = { galleryViewModel.deleteVideo(it) }
                )

                2 -> DetectionScreen(
                    uiState = detectionState,
                    onToggleMotion = { detectionViewModel.toggleMotionDetection() },
                    onSetMotionSensitivity = { detectionViewModel.setMotionSensitivity(it) },
                    onTogglePerson = { detectionViewModel.togglePersonDetection() },
                    onSetPersonConfidence = { detectionViewModel.setPersonConfidence(it) },
                    onSimulateEvent = { detectionViewModel.addTestDetectionEvent(it) },
                    onClearEvents = { detectionViewModel.clearEvents() }
                )

                3 -> SettingsScreen(
                    settings = settingsState,
                    onUpdateResolution = { settingsViewModel.updateResolution(it) },
                    onUpdateFrameRate = { settingsViewModel.updateFrameRate(it) },
                    onUpdateBitrate = { settingsViewModel.updateBitrate(it) },
                    onToggleAudio = { settingsViewModel.toggleAudio() },
                    onUpdateAudioSource = { settingsViewModel.updateAudioSource(it) },
                    onUpdateAudioChannels = { settingsViewModel.updateAudioChannels(it) },
                    onUpdateStorageLocation = { settingsViewModel.updateStorageLocation(it) },
                    onUpdateThemeMode = { settingsViewModel.updateThemeMode(it) },
                    onToggleDynamicColor = { settingsViewModel.toggleDynamicColor() }
                )
            }
        }
    }
}
