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
import com.twinpath.cambg.feature.settings.model.UpdateArchitecturePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.first
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
    data class RequirePermission(val apkFile: File) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

class UpdateManager(private val context: Context) {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    // Build custom OkHttpClient to fully disable HTTP caching for update queries
    private val networkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build()
            chain.proceed(request)
        }
        .build()

    private val moshi = Moshi.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twinpath.github.io/")
        .client(networkHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val updateApi = retrofit.create(UpdateApi::class.java)

    suspend fun checkForUpdates(selectedChannel: UpdateChannel, isManual: Boolean = false) {
        if (isManual) {
            _updateState.value = UpdateState.Checking
        }
        try {
            // Cache busting URL query parameter
            val cacheBusterUrl = "${UpdateConstants.UPDATE_API_URL}?t=${System.currentTimeMillis()}"
            val releases = withContext(Dispatchers.IO) {
                updateApi.getReleases(cacheBusterUrl)
            }
            
            val latestRelease = findLatestUpdate(releases, selectedChannel)
            if (latestRelease != null) {
                // Fetch current user preference from repository dynamically
                val settingsRepository = com.twinpath.cambg.core.data.SettingsRepository(context)
                val appSettings = settingsRepository.settingsFlow.first()
                
                val asset = selectAssetForDevice(latestRelease.assets, appSettings.updateArchPreference)
                if (asset != null) {
                    _updateState.value = UpdateState.UpdateAvailable(latestRelease, asset)
                } else {
                    if (isManual) {
                        _updateState.value = UpdateState.Error("No compatible architecture asset found")
                    } else {
                        _updateState.value = UpdateState.Idle
                    }
                }
            } else {
                if (isManual) {
                    _updateState.value = UpdateState.UpToDate
                } else {
                    _updateState.value = UpdateState.Idle
                }
            }
        } catch (e: Exception) {
            if (isManual) {
                _updateState.value = UpdateState.Error(e.localizedMessage ?: "Unknown error occurred")
            } else {
                _updateState.value = UpdateState.Idle
            }
        }
    }

    private fun findLatestUpdate(releases: List<UpdateRelease>, channel: UpdateChannel): UpdateRelease? {
        val currentVersionCode = BuildConfig.VERSION_CODE
        val currentVersionName = BuildConfig.VERSION_NAME

        return releases
            .filter { release ->
                if (channel == UpdateChannel.ANY) {
                    true
                } else {
                    val allowedTypes = when (channel) {
                        UpdateChannel.STABLE -> listOf("stable")
                        UpdateChannel.BETA -> listOf("stable", "beta")
                        UpdateChannel.ALPHA -> listOf("stable", "beta", "alpha")
                        UpdateChannel.TEST -> listOf("stable", "beta", "alpha", "test")
                        UpdateChannel.ANY -> emptyList() // Handled above
                    }
                    release.releaseType in allowedTypes
                }
            }
            .filter { release ->
                if (release.versionCode != null) {
                    release.versionCode > currentVersionCode
                } else {
                    VersionComparator.compare(release.version, currentVersionName) > 0
                }
            }
            .maxWithOrNull { r1, r2 ->
                if (r1.versionCode != null && r2.versionCode != null) {
                    r1.versionCode.compareTo(r2.versionCode)
                } else {
                    VersionComparator.compare(r1.version, r2.version)
                }
            }
    }

    private fun selectAssetForDevice(assets: List<UpdateAsset>, pref: UpdateArchitecturePreference): UpdateAsset? {
        // If UNIVERSAL preference is selected, try to get universal asset first
        if (pref == UpdateArchitecturePreference.UNIVERSAL) {
            val universalAsset = assets.find {
                it.architecture.lowercase() == "universal" ||
                it.name.contains("universal", ignoreCase = true)
            }
            if (universalAsset != null) return universalAsset
        }

        // Try getting ABI-specific asset
        val supportedAbis = Build.SUPPORTED_ABIS.map { it.lowercase() }
        for (abi in supportedAbis) {
            val matchingAsset = assets.find { it.architecture.lowercase() == abi }
            if (matchingAsset != null) return matchingAsset
        }

        // Fallback to universal asset if not already checked
        if (pref != UpdateArchitecturePreference.UNIVERSAL) {
            return assets.find {
                it.architecture.lowercase() == "universal" ||
                it.name.contains("universal", ignoreCase = true)
            }
        }
        return null
    }

    fun downloadAndInstallApk(asset: UpdateAsset): Flow<Int> = flow {
        _updateState.value = UpdateState.Downloading(0)
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(asset.downloadUrl).build()

            val response = client.newCall(request).execute()
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

            _updateState.value = UpdateState.ReadyToInstall(destinationFile)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            _updateState.value = UpdateState.Error(e.localizedMessage ?: "Unknown download error occurred")
        }
    }.flowOn(Dispatchers.IO)

    fun triggerInstall(file: File) {
        // Request unknown app sources permission if needed on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                _updateState.value = UpdateState.RequirePermission(file)
                return
            }
        }

        val authority = "${context.packageName}.fileprovider"
        val apkUri = FileProvider.getUriForFile(context, authority, file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun setReadyToInstall(file: File) {
        _updateState.value = UpdateState.ReadyToInstall(file)
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }
}
