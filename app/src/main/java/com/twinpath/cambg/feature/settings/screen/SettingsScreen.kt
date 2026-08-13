package com.twinpath.cambg.feature.settings.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.twinpath.cambg.BuildConfig
import com.twinpath.cambg.R
import androidx.compose.ui.res.stringResource
import com.twinpath.cambg.feature.settings.model.AppSettings
import com.twinpath.cambg.feature.settings.model.AppThemeMode
import com.twinpath.cambg.feature.settings.model.AppLanguage
import com.twinpath.cambg.feature.settings.model.StorageLocation
import com.twinpath.cambg.core.component.SettingsSectionHeader
import com.twinpath.cambg.core.component.SwitchSettingItem
import com.twinpath.cambg.core.component.DropdownSettingItem
import com.twinpath.cambg.feature.settings.component.SettingsBatteryCard
import com.twinpath.cambg.feature.settings.component.SettingsStorageCard

import com.twinpath.cambg.core.constant.VideoConstants
import com.twinpath.cambg.core.constant.AudioConstants

import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBack
import com.twinpath.cambg.feature.settings.model.UpdateChannel
import com.twinpath.cambg.core.helper.UpdateState

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
                            imageVector = Icons.Default.ArrowBack,
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
            item {
                SettingsBatteryCard()
            }

            // --- VIDEO SECTION ---
            item {
                SettingsSectionHeader(title = stringResource(id = R.string.section_video_settings), icon = Icons.Default.Videocam)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Resolution Dropdown Item
                        DropdownSettingItem(
                            title = stringResource(id = R.string.video_resolution),
                            subtitle = settings.resolution,
                            options = VideoConstants.RESOLUTION_OPTIONS,
                            selected = settings.resolution,
                            onSelect = onUpdateResolution,
                            testTagPrefix = "res"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Frame Rate Dropdown Item
                        DropdownSettingItem(
                            title = stringResource(id = R.string.video_framerate),
                            subtitle = settings.frameRate,
                            options = VideoConstants.FPS_OPTIONS,
                            selected = settings.frameRate,
                            onSelect = onUpdateFrameRate,
                            testTagPrefix = "fps"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Bitrate Dropdown Item
                        DropdownSettingItem(
                            title = stringResource(id = R.string.video_bitrate),
                            subtitle = settings.bitrate,
                            options = VideoConstants.BITRATE_OPTIONS,
                            selected = settings.bitrate,
                            onSelect = onUpdateBitrate,
                            testTagPrefix = "bitrate"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Aspect Ratio Dropdown Item
                        DropdownSettingItem(
                            title = stringResource(id = R.string.video_aspect_ratio),
                            subtitle = settings.aspectRatio,
                            options = VideoConstants.ASPECT_RATIO_OPTIONS,
                            selected = settings.aspectRatio,
                            onSelect = onUpdateAspectRatio,
                            testTagPrefix = "ratio"
                        )


                    }
                }
            }

            // --- AUDIO SECTION ---
            item {
                SettingsSectionHeader(title = stringResource(id = R.string.section_audio_settings), icon = Icons.Default.Mic)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Toggle Audio Switch
                        SwitchSettingItem(
                            title = stringResource(id = R.string.audio_record),
                            subtitle = if (settings.audioEnabled) stringResource(id = R.string.audio_record_desc) else "Muted video recording",
                            checked = settings.audioEnabled,
                            onCheckedChange = { onToggleAudio() },
                            testTag = "record_audio_switch"
                        )

                        if (settings.audioEnabled) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownSettingItem(
                                title = stringResource(id = R.string.audio_source),
                                subtitle = settings.audioSource,
                                options = AudioConstants.AUDIO_SOURCE_OPTIONS,
                                selected = settings.audioSource,
                                onSelect = onUpdateAudioSource,
                                testTagPrefix = "audio_source"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownSettingItem(
                                title = stringResource(id = R.string.audio_channels),
                                subtitle = settings.audioChannels,
                                options = AudioConstants.AUDIO_CHANNEL_OPTIONS,
                                selected = settings.audioChannels,
                                onSelect = onUpdateAudioChannels,
                                testTagPrefix = "audio_channels"
                            )
                        }
                    }
                }
            }

            item {
                SettingsStorageCard(
                    settings = settings,
                    onUpdateStorageLocation = onUpdateStorageLocation,
                    onUpdateCustomStoragePath = onUpdateCustomStoragePath
                )
            }

            // --- APPEARANCE SECTION ---
            item {
                SettingsSectionHeader(title = stringResource(id = R.string.section_appearance), icon = Icons.Default.Palette)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.theme_preference),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        AppThemeMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onUpdateThemeMode(mode) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.themeMode == mode,
                                    onClick = { onUpdateThemeMode(mode) }
                                )
                                Text(
                                    text = when (mode) {
                                        AppThemeMode.LIGHT -> stringResource(id = R.string.theme_light)
                                        AppThemeMode.DARK -> stringResource(id = R.string.theme_dark)
                                        AppThemeMode.SYSTEM -> stringResource(id = R.string.theme_system)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SwitchSettingItem(
                            title = stringResource(id = R.string.dynamic_color_title),
                            subtitle = stringResource(id = R.string.dynamic_color_desc),
                            checked = settings.dynamicColor,
                            onCheckedChange = { onToggleDynamicColor() },
                            testTag = "dynamic_color_switch"
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = stringResource(id = R.string.language_preference),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        AppLanguage.entries.forEach { lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onUpdateLanguage(lang) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.language == lang,
                                    onClick = { onUpdateLanguage(lang) }
                                )
                                Text(
                                    text = lang.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // --- APPLICATION UPDATES SECTION ---
            item {
                SettingsSectionHeader(title = stringResource(id = R.string.settings_updates_title), icon = Icons.Default.Shield)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DropdownSettingItem(
                            title = stringResource(id = R.string.settings_updates_channel),
                            subtitle = settings.updateChannel.name,
                            options = UpdateChannel.entries.map { it.name },
                            selected = settings.updateChannel.name,
                            onSelect = { onUpdateUpdateChannel(UpdateChannel.valueOf(it)) },
                            testTagPrefix = "update_channel"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SwitchSettingItem(
                            title = stringResource(id = R.string.settings_updates_auto_check),
                            subtitle = if (settings.autoCheckUpdates) "Checking enabled on startup" else "Manual checks only",
                            checked = settings.autoCheckUpdates,
                            onCheckedChange = { onToggleAutoCheck() },
                            testTag = "auto_check_updates_switch"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Button(
                            onClick = onCheckForUpdates,
                            modifier = Modifier.fillMaxWidth().testTag("check_updates_button")
                        ) {
                            Text(text = stringResource(id = R.string.settings_updates_check_button))
                        }
                    }
                }
            }

            // --- ABOUT SECTION ---
            item {
                SettingsSectionHeader(title = "About App", icon = Icons.Default.Info)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CamBG Record",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Package: com.twinpath.cambg",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }

    // --- Update dialogs handling ---
    when (val state = updateState) {
        is UpdateState.Checking -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                dismissButton = {},
                title = { Text(text = "Checking for updates...") },
                text = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            )
        }
        is UpdateState.UpdateAvailable -> {
            AlertDialog(
                onDismissRequest = onResetUpdateState,
                title = { Text(text = stringResource(id = R.string.update_dialog_title)) },
                text = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.update_dialog_version, state.release.version, state.release.releaseType),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.update_dialog_date, state.release.date),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.update_dialog_notes),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = state.release.notes,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { onDownloadAndInstallUpdate(state.asset) }) {
                        Text(text = stringResource(id = R.string.update_dialog_btn_download))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onResetUpdateState) {
                        Text(text = stringResource(id = R.string.update_dialog_btn_cancel))
                    }
                }
            )
        }
        is UpdateState.Downloading -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                title = { Text(text = stringResource(id = R.string.update_downloading, state.progress)) },
                text = {
                    Column {
                        LinearProgressIndicator(
                            progress = { state.progress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.progress}%",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            )
        }
        is UpdateState.ReadyToInstall -> {
            AlertDialog(
                onDismissRequest = onResetUpdateState,
                title = { Text(text = "Update Ready") },
                text = { Text(text = "The update has been downloaded. Press install to update the app.") },
                confirmButton = {
                    Button(onClick = { onTriggerInstall(state.apkFile) }) {
                        Text(text = "Install")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onResetUpdateState) {
                        Text(text = stringResource(id = R.string.update_dialog_btn_cancel))
                    }
                }
            )
        }
        is UpdateState.UpToDate -> {
            AlertDialog(
                onDismissRequest = onResetUpdateState,
                title = { Text(text = "Up to Date") },
                text = { Text(text = stringResource(id = R.string.update_no_updates)) },
                confirmButton = {
                    Button(onClick = onResetUpdateState) {
                        Text(text = "OK")
                    }
                }
            )
        }
        is UpdateState.Error -> {
            AlertDialog(
                onDismissRequest = onResetUpdateState,
                title = { Text(text = "Error") },
                text = { Text(text = state.message) },
                confirmButton = {
                    Button(onClick = onResetUpdateState) {
                        Text(text = "OK")
                    }
                }
            )
        }
        else -> {}
    }
}

