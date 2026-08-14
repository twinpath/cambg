package com.twinpath.cambg.feature.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twinpath.cambg.R
import com.twinpath.cambg.core.component.DropdownSettingItem
import com.twinpath.cambg.core.component.SettingsSectionHeader
import com.twinpath.cambg.core.component.SwitchSettingItem
import com.twinpath.cambg.core.constant.AudioConstants
import com.twinpath.cambg.feature.settings.model.AppSettings

@Composable
fun SettingsAudioSection(
    settings: AppSettings,
    onToggleAudio: () -> Unit,
    onUpdateAudioSource: (String) -> Unit,
    onUpdateAudioChannels: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
}
