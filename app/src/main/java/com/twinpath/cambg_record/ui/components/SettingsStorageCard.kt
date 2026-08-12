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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twinpath.cambg_record.model.AppSettings
import com.twinpath.cambg_record.model.StorageLocation

@Composable
fun SettingsStorageCard(
    settings: AppSettings,
    onUpdateStorageLocation: (StorageLocation) -> Unit,
    modifier: Modifier = Modifier
) {
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
                Spacer(modifier = Modifier.height(8.dp))

                StorageLocation.entries.forEach { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onUpdateStorageLocation(location) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.storageLocation == location,
                            onClick = { onUpdateStorageLocation(location) }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = location.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (settings.storageLocation == location) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = location.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
