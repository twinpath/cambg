package com.twinpath.cambg.feature.settings.component

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.*
import com.twinpath.cambg.feature.settings.model.AppSettings
import com.twinpath.cambg.feature.settings.model.StorageLocation
import com.twinpath.cambg.core.component.SettingsSectionHeader

@Composable
fun SettingsStorageCard(
    settings: AppSettings,
    onUpdateStorageLocation: (StorageLocation) -> Unit,
    onUpdateCustomStoragePath: (String) -> Unit,
    onPickCustomFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val availableLocations = remember {
        StorageLocation.entries.toList()
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Storage", icon = Icons.Default.SdCard)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Save Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown Selector Trigger
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = settings.storageLocation.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = settings.storageLocation.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select storage location",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        availableLocations.forEach { location ->
                            DropdownMenuItem(
                                text = {
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(
                                            text = location.displayName,
                                            fontWeight = if (settings.storageLocation == location) FontWeight.Bold else FontWeight.Normal,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = location.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onUpdateStorageLocation(location)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Custom Folder Picker (only visible when Custom Location is selected)
                AnimatedVisibility(
                    visible = settings.storageLocation == StorageLocation.CUSTOM,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        // Show current selected folder path
                        if (settings.customStoragePath.isNotBlank()) {
                            val displayPath = getDisplayPathFromUri(settings.customStoragePath)
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = displayPath,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Button to open folder picker
                        OutlinedButton(
                            onClick = onPickCustomFolder,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (settings.customStoragePath.isBlank()) "Choose Folder" else "Change Folder"
                            )
                        }

                        if (settings.customStoragePath.isBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No folder selected. Tap to choose a folder from your device.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic storage usage
                val statFs = remember {
                    try {
                        android.os.StatFs(android.os.Environment.getExternalStorageDirectory().absolutePath)
                    } catch (_: Exception) { null }
                }
                val totalGb = statFs?.let { it.totalBytes / (1024.0 * 1024.0 * 1024.0) } ?: 0.0
                val availGb = statFs?.let { it.availableBytes / (1024.0 * 1024.0 * 1024.0) } ?: 0.0
                val usedGb = totalGb - availGb
                val usedFraction = if (totalGb > 0) (usedGb / totalGb).toFloat().coerceIn(0f, 1f) else 0f

                Text(
                    text = "Storage Used: ${String.format(java.util.Locale.US, "%.1f", usedGb)} GB / ${String.format(java.util.Locale.US, "%.0f", totalGb)} GB",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { usedFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Converts a SAF URI string to a human-readable display path.
 */
private fun getDisplayPathFromUri(uriString: String): String {
    return try {
        val uri = Uri.parse(uriString)
        val path = uri.path ?: uriString
        // SAF URIs typically have paths like /tree/primary:DCIM/MyFolder
        // Extract the meaningful part after the colon
        val treePath = path.substringAfter("/tree/", "")
        if (treePath.isNotEmpty()) {
            treePath.replace(":", "/").replace("%2F", "/")
        } else {
            val docPath = path.substringAfter("/document/", "")
            if (docPath.isNotEmpty()) {
                docPath.replace(":", "/").replace("%2F", "/")
            } else {
                uriString
            }
        }
    } catch (_: Exception) {
        uriString
    }
}
