package com.twinpath.cambg.feature.settings.component

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twinpath.cambg.R
import com.twinpath.cambg.core.helper.UpdateState

@Composable
fun SettingsUpdateDialogs(
    updateState: UpdateState,
    onDownloadAndInstallUpdate: (com.twinpath.cambg.core.data.model.UpdateAsset) -> Unit,
    onTriggerInstall: (java.io.File) -> Unit,
    onSetReadyToInstall: (java.io.File) -> Unit,
    onResetUpdateState: () -> Unit
) {
    val context = LocalContext.current

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
        is UpdateState.RequirePermission -> {
            AlertDialog(
                onDismissRequest = { onSetReadyToInstall(state.apkFile) },
                title = { Text(text = stringResource(id = R.string.update_permission_title)) },
                text = { Text(text = stringResource(id = R.string.update_permission_desc)) },
                confirmButton = {
                    Button(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                Uri.parse("package:${context.packageName}")
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                        // Return to ReadyToInstall so user can try again after granting
                        onSetReadyToInstall(state.apkFile)
                    }) {
                        Text(text = stringResource(id = R.string.update_permission_btn_settings))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onSetReadyToInstall(state.apkFile) }) {
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
