package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun InteractiveImageSlider(
    urlsString: String,
    speed: Float, // 1 to 10 seconds
    radius: Float, // 0 to 50 rounded corners
    showArrows: Boolean
) {
    val urls = urlsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val allUrls = if (urls.isNotEmpty()) urls else listOf(
        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800"
    )

    var activeIndex by remember(allUrls) { mutableStateOf(0) }

    // Automated loop based on speed setting
    LaunchedEffect(allUrls, speed) {
        while (true) {
            delay((speed * 1000).toLong())
            if (allUrls.isNotEmpty()) {
                activeIndex = (activeIndex + 1) % allUrls.size
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(radius.dp))
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        // Slide image container with crossfade
        if (allUrls.isNotEmpty() && activeIndex in allUrls.indices) {
            Crossfade(
                targetState = allUrls[activeIndex],
                animationSpec = tween(600),
                label = "SlideFade"
            ) { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Slider Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Navigation Arrows
        if (showArrows && allUrls.size > 1) {
            // Prev Arrow
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        activeIndex = (activeIndex - 1 + allUrls.size) % allUrls.size
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Slide",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Next Arrow
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        activeIndex = (activeIndex + 1) % allUrls.size
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "Next Slide",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Dot indicators
        if (allUrls.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allUrls.forEachIndexed { index, _ ->
                    val isActive = index == activeIndex
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                            .clickable { activeIndex = index }
                    )
                }
            }
        }
    }
}
