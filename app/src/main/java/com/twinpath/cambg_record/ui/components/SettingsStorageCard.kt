package com.twinpath.cambg_record.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.twinpath.cambg_record.model.AppSettings
import com.twinpath.cambg_record.model.StorageLocation

@Composable
fun SettingsStorageCard(
    settings: AppSettings,
    onUpdateStorageLocation: (StorageLocation) -> Unit,
    onUpdateCustomStoragePath: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val hasSdCard = remember {
        val dirs = ContextCompat.getExternalFilesDirs(context, null)
        dirs.size > 1 && dirs[1] != null
    }

    val availableLocations = remember(hasSdCard) {
        StorageLocation.entries.filter {
            it != StorageLocation.SD_CARD || hasSdCard
        }
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

                // Custom Storage Path Text Field (only visible when Custom Location is selected)
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
                        OutlinedTextField(
                            value = settings.customStoragePath,
                            onValueChange = onUpdateCustomStoragePath,
                            label = { Text("Custom Folder Name") },
                            placeholder = { Text("e.g. MySecretRecordings") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Save files under DCIM/[Custom Folder Name]",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
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
