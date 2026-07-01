package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun LoaderPreview(
    style: String,
    colorHex: String,
    size: Float,
    speed: Float, // speed representation (3 to 30) -> (0.3s to 3.0s)
    strokeWidth: Float
) {
    val color = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color(0xFF3B82F6)
    }

    // Convert speed integer (e.g. 10) to milliseconds (e.g. 1000ms)
    val durationMs = (speed * 100).toInt()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (style) {
            "spinner" -> {
                val infiniteTransition = rememberInfiniteTransition(label = "Spinner")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = durationMs, easing = LinearEasing)
                    ),
                    label = "Rotation"
                )
                
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size.dp)
                        .rotate(rotation),
                    color = color,
                    strokeWidth = strokeWidth.dp,
                    trackColor = color.copy(alpha = 0.2f)
                )
            }
            "dots" -> {
                val dotSize = (size / 3f).dp
                val gap = (size / 4f).dp
                val liftDistance = (size / 2.5f).dp

                val infiniteTransition = rememberInfiniteTransition(label = "Dots")
                
                @Composable
                fun animateBounce(delayMs: Int): Float {
                    val bounce by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = durationMs
                                0.0f at 0 + delayMs using LinearEasing
                                -liftDistance.value at (durationMs / 2) + delayMs using LinearEasing
                                0.0f at durationMs + delayMs using LinearEasing
                            }
                        ),
                        label = "Bounce"
                    )
                    return bounce
                }

                val bounce1 = animateBounce(0)
                val bounce2 = animateBounce((durationMs * 0.15f).toInt())
                val bounce3 = animateBounce((durationMs * 0.3f).toInt())

                Row(
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .graphicsLayer { translationY = bounce1.dp.toPx() }
                            .clip(CircleShape)
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .graphicsLayer { translationY = bounce2.dp.toPx() }
                            .clip(CircleShape)
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .graphicsLayer { translationY = bounce3.dp.toPx() }
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
            "pulse" -> {
                val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = durationMs
                            0.8f at 0
                            1.2f at (durationMs / 2)
                            0.8f at durationMs
                        }
                    ),
                    label = "PulseScale"
                )
                val opacity by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = durationMs
                            0.4f at 0
                            1.0f at (durationMs / 2)
                            0.4f at durationMs
                        }
                    ),
                    label = "PulseOpacity"
                )

                Box(
                    modifier = Modifier
                        .size(size.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .alpha(opacity)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            "bars" -> {
                val barWidth = (size / 5f).dp
                val infiniteTransition = rememberInfiniteTransition(label = "Bars")

                @Composable
                fun animateBar(delayMs: Int): Float {
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = durationMs
                                0.3f at 0 + delayMs
                                1.0f at (durationMs / 2) + delayMs
                                0.3f at durationMs + delayMs
                            }
                        ),
                        label = "BarScale"
                    )
                    return scale
                }

                val bar1 = animateBar(0)
                val bar2 = animateBar((durationMs * 0.1f).toInt())
                val bar3 = animateBar((durationMs * 0.2f).toInt())
                val bar4 = animateBar((durationMs * 0.3f).toInt())

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .height(size.dp)
                            .graphicsLayer { scaleY = bar1 }
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .height(size.dp)
                            .graphicsLayer { scaleY = bar2 }
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .height(size.dp)
                            .graphicsLayer { scaleY = bar3 }
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .height(size.dp)
                            .graphicsLayer { scaleY = bar4 }
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}
