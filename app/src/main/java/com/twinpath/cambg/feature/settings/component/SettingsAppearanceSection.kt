package com.twinpath.cambg.feature.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsSuggest
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
import com.twinpath.cambg.feature.settings.model.AppLanguage
import com.twinpath.cambg.feature.settings.model.AppSettings
import com.twinpath.cambg.feature.settings.model.AppThemeMode

@Composable
fun SettingsAppearanceSection(
    settings: AppSettings,
    onUpdateThemeMode: (AppThemeMode) -> Unit,
    onToggleDynamicColor: () -> Unit,
    onUpdateLanguage: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val lightLabel = stringResource(id = R.string.theme_light)
    val darkLabel = stringResource(id = R.string.theme_dark)
    val systemLabel = stringResource(id = R.string.theme_system)

    Column(modifier = modifier) {
        SettingsSectionHeader(title = stringResource(id = R.string.section_appearance), icon = Icons.Default.Palette)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DropdownSettingItem(
                    title = stringResource(id = R.string.theme_preference),
                    subtitle = when (settings.themeMode) {
                        AppThemeMode.LIGHT -> lightLabel
                        AppThemeMode.DARK -> darkLabel
                        AppThemeMode.SYSTEM -> systemLabel
                    },
                    options = AppThemeMode.entries,
                    selected = settings.themeMode,
                    onSelect = onUpdateThemeMode,
                    labelProvider = { mode ->
                        when (mode) {
                            AppThemeMode.LIGHT -> lightLabel
                            AppThemeMode.DARK -> darkLabel
                            AppThemeMode.SYSTEM -> systemLabel
                        }
                    },
                    iconProvider = { mode ->
                        when (mode) {
                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.SYSTEM -> Icons.Default.SettingsSuggest
                        }
                    },
                    testTagPrefix = "theme"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SwitchSettingItem(
                    title = stringResource(id = R.string.dynamic_color_title),
                    subtitle = stringResource(id = R.string.dynamic_color_desc),
                    checked = settings.dynamicColor,
                    onCheckedChange = { onToggleDynamicColor() },
                    testTag = "dynamic_color_switch"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DropdownSettingItem(
                    title = stringResource(id = R.string.language_preference),
                    subtitle = settings.language.displayName,
                    options = AppLanguage.entries,
                    selected = settings.language,
                    onSelect = onUpdateLanguage,
                    labelProvider = { it.displayName },
                    iconProvider = null,
                    testTagPrefix = "language"
                )
            }
        }
    }
}

