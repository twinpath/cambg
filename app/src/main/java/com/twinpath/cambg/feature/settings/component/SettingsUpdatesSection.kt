package com.twinpath.cambg.feature.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twinpath.cambg.R
import com.twinpath.cambg.core.component.DropdownSettingItem
import com.twinpath.cambg.core.component.SettingsSectionHeader
import com.twinpath.cambg.core.component.SwitchSettingItem
import com.twinpath.cambg.feature.settings.model.AppSettings
import com.twinpath.cambg.feature.settings.model.UpdateChannel
import com.twinpath.cambg.feature.settings.model.UpdateArchitecturePreference

@Composable
fun SettingsUpdatesSection(
    settings: AppSettings,
    onUpdateUpdateChannel: (UpdateChannel) -> Unit,
    onUpdateArchPreference: (UpdateArchitecturePreference) -> Unit,
    onToggleAutoCheck: () -> Unit,
    onCheckForUpdates: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceArchLabel = stringResource(id = R.string.update_arch_device)
    val universalArchLabel = stringResource(id = R.string.update_arch_universal)
    Column(modifier = modifier) {
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

                DropdownSettingItem(
                    title = stringResource(id = R.string.settings_update_arch_pref),
                    subtitle = when (settings.updateArchPreference) {
                        UpdateArchitecturePreference.DEVICE_ARCH -> deviceArchLabel
                        UpdateArchitecturePreference.UNIVERSAL -> universalArchLabel
                    },
                    options = listOf(deviceArchLabel, universalArchLabel),
                    selected = when (settings.updateArchPreference) {
                        UpdateArchitecturePreference.DEVICE_ARCH -> deviceArchLabel
                        UpdateArchitecturePreference.UNIVERSAL -> universalArchLabel
                    },
                    onSelect = { selectedLabel ->
                        val pref = if (selectedLabel == deviceArchLabel) {
                            UpdateArchitecturePreference.DEVICE_ARCH
                        } else {
                            UpdateArchitecturePreference.UNIVERSAL
                        }
                        onUpdateArchPreference(pref)
                    },
                    testTagPrefix = "update_arch_pref"
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
}
