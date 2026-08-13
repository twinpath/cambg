package com.twinpath.cambg.feature.gallery.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.twinpath.cambg.feature.gallery.helper.RecordedFilesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

data class GalleryUiState(
    val videos: List<VideoItem> = mockVideoItems,
    val searchQuery: String = "",
    val isSearchExpanded: Boolean = false,
    val selectedFilter: String = "All", // "All", "Today", "This Week", "Front Camera", "Back Camera"
    val isGridView: Boolean = true,
    val selectedVideoForPreview: VideoItem? = null
)

class GalleryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    fun loadRecordedVideos(context: Context) {
        val recordedFiles = RecordedFilesHelper.getRecordedVideos(context)
        _uiState.update { currentState ->
            currentState.copy(videos = recordedFiles)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearchExpanded() {
        _uiState.update {
            val nextState = !it.isSearchExpanded
            it.copy(
                isSearchExpanded = nextState,
                searchQuery = if (!nextState) "" else it.searchQuery
            )
        }
    }

    fun selectFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun selectVideoForPreview(video: VideoItem?) {
        _uiState.update { it.copy(selectedVideoForPreview = video) }
    }

    fun deleteVideo(context: Context, videoId: String) {
        _uiState.update { currentState ->
            val videoToDelete = currentState.videos.find { it.id == videoId }
            videoToDelete?.filePath?.let { path ->
                RecordedFilesHelper.deleteRecordedFile(context, path)
            }
            val updated = currentState.videos.filterNot { it.id == videoId }
            currentState.copy(
                videos = updated,
                selectedVideoForPreview = if (currentState.selectedVideoForPreview?.id == videoId) null else currentState.selectedVideoForPreview
            )
        }
    }

    fun addRecordedVideo(
        durationSeconds: Long,
        quality: String,
        isFrontCamera: Boolean,
        filePath: String? = null,
        fileSizeBytes: Long? = null
    ) {
        val newVideo = if (filePath != null && File(filePath).exists()) {
            RecordedFilesHelper.extractVideoItem(File(filePath))
        } else {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            val durationStr = String.format("%02d:%02d", minutes, seconds)
            val currentTime = System.currentTimeMillis()
            val calculatedMb = if (fileSizeBytes != null && fileSizeBytes > 0) {
                fileSizeBytes / (1024.0 * 1024.0)
            } else {
                (durationSeconds * 1.5).coerceAtLeast(1.0)
            }
            val fileName = if (filePath != null) {
                File(filePath).name
            } else {
                "VID_${currentTime}.mp4"
            }
            VideoItem(
                id = filePath ?: currentTime.toString(),
                title = fileName,
                duration = durationStr,
                resolution = quality,
                dateText = "Just now",
                timestamp = currentTime,
                cameraType = if (isFrontCamera) "Front Camera" else "Back Camera",
                sizeMb = String.format(java.util.Locale.US, "%.1f", calculatedMb).toDoubleOrNull() ?: calculatedMb,
                gradientColors = listOf(0xFF1A73E8, 0xFFEA4335),
                filePath = filePath
            )
        }

        _uiState.update { currentState ->
            // Replace if already exists or prepend
            val filtered = currentState.videos.filterNot { it.filePath == filePath || it.id == newVideo.id }
            currentState.copy(videos = listOf(newVideo) + filtered)
        }
    }

    fun getFilteredVideos(): List<VideoItem> {
        val state = _uiState.value
        val now = System.currentTimeMillis()
        val oneDayMs = 86400000L
        val oneWeekMs = 7 * oneDayMs

        return state.videos.filter { video ->
            // Search Query filter
            val matchesSearch = state.searchQuery.isEmpty() ||
                    video.title.contains(state.searchQuery, ignoreCase = true) ||
                    video.dateText.contains(state.searchQuery, ignoreCase = true)

            // Category filter
            val matchesFilter = when (state.selectedFilter) {
                "Today" -> (now - video.timestamp) <= oneDayMs
                "This Week" -> (now - video.timestamp) <= oneWeekMs
                "Front Camera" -> video.cameraType == "Front Camera"
                "Back Camera" -> video.cameraType == "Back Camera"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }
}

