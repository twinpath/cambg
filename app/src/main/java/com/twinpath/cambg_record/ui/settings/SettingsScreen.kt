package com.twinpath.cambg_record.ui.settings

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
import com.twinpath.cambg_record.BuildConfig
import com.twinpath.cambg_record.model.AppSettings
import com.twinpath.cambg_record.model.AppThemeMode
import com.twinpath.cambg_record.model.StorageLocation
import com.twinpath.cambg_record.ui.components.SettingsSectionHeader
import com.twinpath.cambg_record.ui.components.SwitchSettingItem
import com.twinpath.cambg_record.ui.components.DropdownSettingItem
import com.twinpath.cambg_record.ui.components.SettingsBatteryCard
import com.twinpath.cambg_record.ui.components.SettingsStorageCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateResolution: (String) -> Unit,
    onUpdateFrameRate: (String) -> Unit,
    onUpdateBitrate: (String) -> Unit,
    onToggleAudio: () -> Unit,
    onUpdateAudioSource: (String) -> Unit,
    onUpdateAudioChannels: (String) -> Unit,
    onUpdateStorageLocation: (StorageLocation) -> Unit,
    onUpdateCustomStoragePath: (String) -> Unit,
    onUpdateThemeMode: (AppThemeMode) -> Unit,
    onToggleDynamicColor: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
                SettingsSectionHeader(title = "Video Settings", icon = Icons.Default.Videocam)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Resolution Dropdown Item
                        DropdownSettingItem(
                            title = "Video Resolution",
                            subtitle = settings.resolution,
                            options = listOf("480p", "720p", "1080p", "4K"),
                            selected = settings.resolution,
                            onSelect = onUpdateResolution,
                            testTagPrefix = "res"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Frame Rate Dropdown Item
                        DropdownSettingItem(
                            title = "Frame Rate",
                            subtitle = settings.frameRate,
                            options = listOf("24 fps", "30 fps", "60 fps"),
                            selected = settings.frameRate,
                            onSelect = onUpdateFrameRate,
                            testTagPrefix = "fps"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Bitrate Dropdown Item
                        DropdownSettingItem(
                            title = "Bitrate Quality",
                            subtitle = settings.bitrate,
                            options = listOf("Auto", "Medium", "High"),
                            selected = settings.bitrate,
                            onSelect = onUpdateBitrate,
                            testTagPrefix = "bitrate"
                        )
                    }
                }
            }

            // --- AUDIO SECTION ---
            item {
                SettingsSectionHeader(title = "Audio Settings", icon = Icons.Default.Mic)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Toggle Audio Switch
                        SwitchSettingItem(
                            title = "Record Audio",
                            subtitle = if (settings.audioEnabled) "Capture microphone/camcorder sound" else "Muted video recording",
                            checked = settings.audioEnabled,
                            onCheckedChange = { onToggleAudio() },
                            testTag = "record_audio_switch"
                        )

                        if (settings.audioEnabled) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownSettingItem(
                                title = "Audio Source",
                                subtitle = settings.audioSource,
                                options = listOf("Camcorder", "Microphone"),
                                selected = settings.audioSource,
                                onSelect = onUpdateAudioSource,
                                testTagPrefix = "audio_source"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownSettingItem(
                                title = "Channels",
                                subtitle = settings.audioChannels,
                                options = listOf("Stereo", "Mono"),
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
                SettingsSectionHeader(title = "Appearance", icon = Icons.Default.Palette)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Theme Preference",
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
                                        AppThemeMode.LIGHT -> "Light Theme"
                                        AppThemeMode.DARK -> "Dark Theme"
                                        AppThemeMode.SYSTEM -> "System Default"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SwitchSettingItem(
                            title = "Dynamic Color (Material You)",
                            subtitle = "Use colors extracted from device wallpaper (Android 12+)",
                            checked = settings.dynamicColor,
                            onCheckedChange = { onToggleDynamicColor() },
                            testTag = "dynamic_color_switch"
                        )
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
                                    text = "Package: com.twinpath.cambg_record",
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
}

