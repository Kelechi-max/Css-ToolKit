package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MockStickyHeaderViewport(
    bgColorHex: String,
    blurVal: Float,
    heightVal: Float,
    shadowVal: Float,
    borderBottom: Boolean,
    brandText: String,
    navLinks: String
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(bgColorHex))
    } catch (e: Exception) {
        Color(0xFF000000)
    }

    val parsedLinks = navLinks.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val isLightBg = (bgColor.red + bgColor.green + bgColor.blue) > 1.5f
    val textColor = if (isLightBg) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val mutedColor = textColor.copy(alpha = 0.6f)

    // Render a mock web browser frame
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Browser Top Bar/Address input mimic
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF222222))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Browser buttons
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFEF4444)))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFFACC15)))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF4ADE80)))
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Address input box
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF111111))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "csstoolkit.dev/sticky-preview",
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            // Webpage Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0E0E0E))
            ) {
                val scrollState = rememberScrollState()

                // Content scrollable underneath
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = (heightVal + 16f).dp, start = 16.dp, end = 16.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "CSS STICKY NAVIGATION EFFECT",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "This sticky header uses modern CSS properties 'position: sticky' to snap to the top of the viewpoint as the user scrolls. Scroll this frame to see content glide under the semi-transparent glass navigation.",
                        color = Color(0xFF888888),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )

                    // Dummy blocks
                    repeat(6) { index ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "SECTION #${index + 1}",
                                    color = Color(0xFFFACC15),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam id finibus elit, sit amet luctus arcu. Suspendisse pulvinar erat vitae ante finibus facilisis.",
                                    color = Color(0xFFCCCCCC),
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Sticky Header on top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightVal.dp)
                        .align(Alignment.TopCenter)
                        .shadow(elevation = (shadowVal / 4f).dp)
                        .blur(if (blurVal > 0f) (blurVal / 2f).dp else 0.dp) // Divide blur to fit preview well
                        .background(bgColor.copy(alpha = 0.85f))
                        .then(
                            if (borderBottom) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = if (isLightBg) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(0.dp)
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = brandText,
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            parsedLinks.forEach { link ->
                                Text(
                                    text = link,
                                    color = mutedColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
