package com.twinpath.cambg.feature.settings.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twinpath.cambg.R
import com.twinpath.cambg.core.helper.UpdateState
import com.twinpath.cambg.feature.settings.component.SettingsAboutSection
import com.twinpath.cambg.feature.settings.component.SettingsAppearanceSection
import com.twinpath.cambg.feature.settings.component.SettingsAudioSection
import com.twinpath.cambg.feature.settings.component.SettingsBatteryCard
import com.twinpath.cambg.feature.settings.component.SettingsStorageCard
import com.twinpath.cambg.feature.settings.component.SettingsUpdateDialogs
import com.twinpath.cambg.feature.settings.component.SettingsUpdatesSection
import com.twinpath.cambg.feature.settings.component.SettingsVideoSection
import com.twinpath.cambg.feature.settings.model.AppLanguage
import com.twinpath.cambg.feature.settings.model.AppSettings
import com.twinpath.cambg.feature.settings.model.AppThemeMode
import com.twinpath.cambg.feature.settings.model.StorageLocation
import com.twinpath.cambg.feature.settings.model.UpdateChannel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    updateState: UpdateState,
    onUpdateResolution: (String) -> Unit,
    onUpdateFrameRate: (String) -> Unit,
    onUpdateBitrate: (String) -> Unit,
    onUpdateAspectRatio: (String) -> Unit,
    onToggleAudio: () -> Unit,
    onUpdateAudioSource: (String) -> Unit,
    onUpdateAudioChannels: (String) -> Unit,
    onUpdateStorageLocation: (StorageLocation) -> Unit,
    onUpdateCustomStoragePath: (String) -> Unit,
    onUpdateThemeMode: (AppThemeMode) -> Unit,
    onToggleDynamicColor: () -> Unit,
    onUpdateLanguage: (AppLanguage) -> Unit,
    onUpdateUpdateChannel: (UpdateChannel) -> Unit,
    onToggleAutoCheck: () -> Unit,
    onCheckForUpdates: () -> Unit,
    onDownloadAndInstallUpdate: (com.twinpath.cambg.core.data.model.UpdateAsset) -> Unit,
    onTriggerInstall: (java.io.File) -> Unit,
    onResetUpdateState: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.title_settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- BATTERY OPTIMIZATION SECTION ---
            item { SettingsBatteryCard() }

            // --- VIDEO SECTION ---
            item {
                SettingsVideoSection(
                    settings = settings,
                    onUpdateResolution = onUpdateResolution,
                    onUpdateFrameRate = onUpdateFrameRate,
                    onUpdateBitrate = onUpdateBitrate,
                    onUpdateAspectRatio = onUpdateAspectRatio
                )
            }

            // --- AUDIO SECTION ---
            item {
                SettingsAudioSection(
                    settings = settings,
                    onToggleAudio = onToggleAudio,
                    onUpdateAudioSource = onUpdateAudioSource,
                    onUpdateAudioChannels = onUpdateAudioChannels
                )
            }

            // --- STORAGE SECTION ---
            item {
                SettingsStorageCard(
                    settings = settings,
                    onUpdateStorageLocation = onUpdateStorageLocation,
                    onUpdateCustomStoragePath = onUpdateCustomStoragePath
                )
            }

            // --- APPEARANCE SECTION ---
            item {
                SettingsAppearanceSection(
                    settings = settings,
                    onUpdateThemeMode = onUpdateThemeMode,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onUpdateLanguage = onUpdateLanguage
                )
            }

            // --- APPLICATION UPDATES SECTION ---
            item {
                SettingsUpdatesSection(
                    settings = settings,
                    onUpdateUpdateChannel = onUpdateUpdateChannel,
                    onToggleAutoCheck = onToggleAutoCheck,
                    onCheckForUpdates = onCheckForUpdates
                )
            }

            // --- ABOUT SECTION ---
            item { SettingsAboutSection() }

            item { Spacer(modifier = Modifier.height(96.dp)) }
        }
    }

    // --- Update dialogs handling ---
    SettingsUpdateDialogs(
        updateState = updateState,
        onDownloadAndInstallUpdate = onDownloadAndInstallUpdate,
        onTriggerInstall = onTriggerInstall,
        onResetUpdateState = onResetUpdateState
    )
}
