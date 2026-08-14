package com.twinpath.cambg.feature.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
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
import com.twinpath.cambg.core.constant.VideoConstants
import com.twinpath.cambg.feature.settings.model.AppSettings

@Composable
fun SettingsVideoSection(
    settings: AppSettings,
    onUpdateResolution: (String) -> Unit,
    onUpdateFrameRate: (String) -> Unit,
    onUpdateBitrate: (String) -> Unit,
    onUpdateAspectRatio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
}
