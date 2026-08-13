package com.twinpath.cambg.core.component

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.twinpath.cambg.feature.gallery.helper.ThumbnailLoader

@Composable
fun shimmerBrush(targetValue: Float = 1000f): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

@Composable
fun VideoThumbnail(
    filePath: String?,
    fallbackColors: List<Long>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var thumbnail by remember(filePath) { mutableStateOf<Bitmap?>(null) }
    var hasLoaded by remember(filePath) { mutableStateOf(false) }

    LaunchedEffect(filePath) {
        if (!filePath.isNullOrEmpty()) {
            thumbnail = ThumbnailLoader.getThumbnail(context, filePath)
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
        } else if (!hasLoaded) {
            // Show shimmering skeleton during load
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(shimmerBrush())
            )
        } else {
            // Gradient fallback if thumbnail is not available after loading
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
