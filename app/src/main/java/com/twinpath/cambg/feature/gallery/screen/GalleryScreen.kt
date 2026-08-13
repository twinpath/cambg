package com.twinpath.cambg.feature.gallery.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twinpath.cambg.feature.gallery.model.VideoItem
import com.twinpath.cambg.feature.gallery.component.GalleryTopBar
import com.twinpath.cambg.core.component.VideoDeleteConfirmationDialog
import com.twinpath.cambg.feature.gallery.component.VideoGridItem
import com.twinpath.cambg.feature.gallery.component.VideoListItem
import com.twinpath.cambg.core.component.VideoPreviewDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    videos: List<VideoItem>,
    searchQuery: String,
    isSearchExpanded: Boolean,
    selectedFilter: String,
    isGridView: Boolean,
    selectedVideoForPreview: VideoItem?,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onSelectFilter: (String) -> Unit,
    onToggleViewMode: () -> Unit,
    onSelectVideo: (VideoItem?) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterOptions = listOf("All", "Today", "This Week", "Front Camera", "Back Camera")
    var videoToDelete by remember { mutableStateOf<VideoItem?>(null) }

    Scaffold(
        topBar = {
            GalleryTopBar(
                searchQuery = searchQuery,
                isSearchExpanded = isSearchExpanded,
                isGridView = isGridView,
                onSearchQueryChange = onSearchQueryChange,
                onToggleSearch = onToggleSearch,
                onToggleViewMode = onToggleViewMode,
                onBack = onBack
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Filter Chips Row ---
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectFilter(filter) },
                        label = { Text(filter) },
                        modifier = Modifier.testTag("filter_chip_$filter")
                    )
                }
            }

            // --- Video Content Grid / List / Empty State ---
            if (videos.isEmpty()) {
                // Empty State View
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.VideoLibrary,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Video Recordings Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedFilter != "All")
                                "Try clearing your search query or filter chip."
                            else "Recordings made on the Camera screen will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else if (isGridView) {
                // 3-Column Grid View
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos, key = { it.id }) { video ->
                        VideoGridItem(
                            video = video,
                            onClick = { onSelectVideo(video) }
                        )
                    }
                }
            } else {
                // List View
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos, key = { it.id }) { video ->
                        VideoListItem(
                            video = video,
                            onClick = { onSelectVideo(video) },
                            onDelete = { videoToDelete = video }
                        )
                    }
                }
            }
        }

        // --- Video Preview Detail Dialog ---
        if (selectedVideoForPreview != null) {
            VideoPreviewDialog(
                video = selectedVideoForPreview,
                onDismiss = { onSelectVideo(null) },
                onDeleteTriggered = { videoToDelete = selectedVideoForPreview }
            )
        }

        // --- Delete Confirmation Dialog ---
        if (videoToDelete != null) {
            VideoDeleteConfirmationDialog(
                videoTitle = videoToDelete!!.title,
                onDismiss = { videoToDelete = null },
                onConfirm = {
                    videoToDelete?.let {
                        onDeleteVideo(it.id)
                        onSelectVideo(null)
                    }
                    videoToDelete = null
                }
            )
        }
    }
}
