package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomBoxShadowPreview(
    offsetX: Float,
    offsetY: Float,
    blurVal: Float,
    spreadVal: Float,
    colorHex: String,
    opacityVal: Float,
    inset: Boolean,
    radiusVal: Float
) {
    val shadowColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color(0xFF000000)
    }

    val resolvedShadowColor = shadowColor.copy(alpha = opacityVal / 100f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Base dimensions of our element
        val boxWidth = 150.dp
        val boxHeight = 100.dp

        if (!inset) {
            // Drop Shadow Layer (rendered behind)
            // Spread increases or decreases shadow size
            val shadowWidth = boxWidth + (spreadVal * 2).dp
            val shadowHeight = boxHeight + (spreadVal * 2).dp

            Box(
                modifier = Modifier
                    .size(width = shadowWidth.coerceAtLeast(0.dp), height = shadowHeight.coerceAtLeast(0.dp))
                    .offset(x = offsetX.dp, y = offsetY.dp)
                    .blur(if (blurVal > 0f) (blurVal / 2f).dp else 0.dp)
                    .background(
                        color = resolvedShadowColor,
                        shape = RoundedCornerShape(radiusVal.dp)
                    )
            )
        }

        // Main Element Box
        Box(
            modifier = Modifier
                .size(width = boxWidth, height = boxHeight)
                .background(
                    color = Color(0xFF161616),
                    shape = RoundedCornerShape(radiusVal.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF2E2E2E),
                    shape = RoundedCornerShape(radiusVal.dp)
                )
                .then(
                    if (inset) {
                        // Inset Shadow simulation: render an internal glow/blur border
                        Modifier.border(
                            width = (blurVal / 5f).coerceAtLeast(1f).dp,
                            color = resolvedShadowColor,
                            shape = RoundedCornerShape(radiusVal.dp)
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ELEMENT",
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
