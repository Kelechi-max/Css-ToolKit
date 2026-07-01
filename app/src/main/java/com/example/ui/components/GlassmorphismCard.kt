package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun GlassmorphismCard(
    blurVal: Float,
    opacityVal: Float,
    saturationVal: Float, // for future expansions or simple scaling
    radiusVal: Float,
    colorHex: String,
    darkText: Boolean,
    bgUrl: String
) {
    val cardColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color(0xFFFFFFFF)
    }

    val alpha = opacityVal / 100f
    val resolvedCardColor = cardColor.copy(alpha = alpha)
    val textColor = if (darkText) Color(0xFF1E293B) else Color.White
    val textMuted = textColor.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Background representation (Ambient shapes or background Image)
        if (bgUrl.isNotEmpty()) {
            AsyncImage(
                model = bgUrl,
                contentDescription = "Custom Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Flowing gradient background mimicking Unsplash photo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    )
            ) {
                // Large floating warm sun
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopStart)
                        .padding(top = 16.dp, start = 16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8A65).copy(alpha = 0.8f))
                )
                // Bottom cool highlight glow
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF4DD0E1).copy(alpha = 0.8f))
                )
            }
        }

        // The Glassmorphic Card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .blur(if (blurVal > 0f) (blurVal / 2f).dp else 0.dp) // Divide slightly to fit display density
                .background(
                    color = resolvedCardColor,
                    shape = RoundedCornerShape(radiusVal.dp)
                )
                .border(
                    width = 1.dp,
                    color = cardColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(radiusVal.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GLASS CARD",
                    color = textColor,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Frosted glass component.",
                    color = textMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
