package com.twinpath.cambg_record.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.twinpath.cambg_record.util.ThumbnailLoader

@Composable
fun VideoThumbnail(
    filePath: String?,
    fallbackColors: List<Long>,
    modifier: Modifier = Modifier
) {
    var thumbnail by remember(filePath) { mutableStateOf<Bitmap?>(null) }
    var hasLoaded by remember(filePath) { mutableStateOf(false) }

    LaunchedEffect(filePath) {
        if (!filePath.isNullOrEmpty()) {
            thumbnail = ThumbnailLoader.getThumbnail(filePath)
        }
        hasLoaded = true
    }

    Crossfade(
        targetState = thumbnail,
        label = "thumbnail_crossfade",
        modifier = modifier
    ) { bitmap ->
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Video thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Gradient fallback if thumbnail is not available or loading
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = fallbackColors.map { Color(it) }
                        )
                    )
            )
        }
    }
}
