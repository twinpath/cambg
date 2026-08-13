package com.twinpath.cambg.core.helper

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import com.squareup.moshi.Moshi
import com.twinpath.cambg.BuildConfig
import com.twinpath.cambg.core.constant.UpdateConstants
import com.twinpath.cambg.core.data.api.UpdateApi
import com.twinpath.cambg.core.data.model.UpdateAsset
import com.twinpath.cambg.core.data.model.UpdateRelease
import com.twinpath.cambg.core.util.VersionComparator
import com.twinpath.cambg.feature.settings.model.UpdateChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileOutputStream

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class UpdateAvailable(val release: UpdateRelease, val asset: UpdateAsset) : UpdateState()
    object UpToDate : UpdateState()
    data class Downloading(val progress: Int) : UpdateState()
    data class ReadyToInstall(val apkFile: File) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

class UpdateManager(private val context: Context) {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    private val moshi = Moshi.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twinpath.github.io/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val updateApi = retrofit.create(UpdateApi::class.java)

    suspend fun checkForUpdates(selectedChannel: UpdateChannel) {
        _updateState.value = UpdateState.Checking
        try {
            val releases = withContext(Dispatchers.IO) {
                updateApi.getReleases()
            }
            val latestRelease = findLatestUpdate(releases, selectedChannel)
            if (latestRelease != null) {
                val asset = selectAssetForDevice(latestRelease.assets)
                if (asset != null) {
                    _updateState.value = UpdateState.UpdateAvailable(latestRelease, asset)
                } else {
                    _updateState.value = UpdateState.Error("No compatible architecture asset found")
                }
            } else {
                _updateState.value = UpdateState.UpToDate
            }
        } catch (e: Exception) {
            _updateState.value = UpdateState.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    private fun findLatestUpdate(releases: List<UpdateRelease>, channel: UpdateChannel): UpdateRelease? {
        val currentVersion = BuildConfig.VERSION_NAME

        val allowedTypes = when (channel) {
            UpdateChannel.STABLE -> listOf("stable")
            UpdateChannel.BETA -> listOf("stable", "beta")
            UpdateChannel.ALPHA -> listOf("stable", "beta", "alpha")
            UpdateChannel.TEST -> listOf("stable", "beta", "alpha", "test")
        }

        return releases
            .filter { it.releaseType in allowedTypes }
            .filter { VersionComparator.compare(it.version, currentVersion) > 0 }
            .maxWithOrNull { r1, r2 -> VersionComparator.compare(r1.version, r2.version) }
    }

    private fun selectAssetForDevice(assets: List<UpdateAsset>): UpdateAsset? {
        val supportedAbis = Build.SUPPORTED_ABIS.map { it.lowercase() }

        for (abi in supportedAbis) {
            val matchingAsset = assets.find { it.architecture.lowercase() == abi }
            if (matchingAsset != null) return matchingAsset
        }

        return assets.find {
            it.architecture.lowercase() == "universal" ||
            it.name.contains("universal", ignoreCase = true)
        }
    }

    fun downloadAndInstallApk(asset: UpdateAsset): Flow<Int> = flow {
        _updateState.value = UpdateState.Downloading(0)
        val client = OkHttpClient()
        val request = Request.Builder().url(asset.downloadUrl).build()

        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
        if (!response.isSuccessful) {
            _updateState.value = UpdateState.Error("Failed to download file from server")
            return@flow
        }

        val body = response.body
        if (body == null) {
            _updateState.value = UpdateState.Error("Empty response body from server")
            return@flow
        }

        val destinationFile = File(context.cacheDir, "update.apk")
        if (destinationFile.exists()) destinationFile.delete()

        val totalBytes = body.contentLength()
        var bytesCopied = 0L

        withContext(Dispatchers.IO) {
            body.byteStream().use { input ->
                FileOutputStream(destinationFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        val progress = if (totalBytes > 0) ((bytesCopied * 100) / totalBytes).toInt() else 0
                        emit(progress)
                        _updateState.value = UpdateState.Downloading(progress)
                        bytes = input.read(buffer)
                    }
                }
            }
        }

        _updateState.value = UpdateState.ReadyToInstall(destinationFile)
    }.flowOn(Dispatchers.IO)

    fun triggerInstall(file: File) {
        val authority = "${context.packageName}.fileprovider"
        val apkUri = FileProvider.getUriForFile(context, authority, file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }
}
