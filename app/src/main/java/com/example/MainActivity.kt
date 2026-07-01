package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CssCodeGenerator
import com.example.ui.MainViewModel
import com.example.ui.components.CustomBoxShadowPreview
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.InteractiveImageSlider
import com.example.ui.components.LoaderPreview
import com.example.ui.components.MockStickyHeaderViewport

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    CssToolkitApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Color.White,
            secondary = Color(0xFF888888),
            background = Color(0xFF0A0A0A),
            surface = Color(0xFF121212)
        ),
        content = content
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CssToolkitApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val settings by viewModel.currentSettings.collectAsState()
    val activeTool by viewModel.activeTool.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val context = LocalContext.current

    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Resolve compiled CSS & HTML
    val codes = remember(activeTool, settings) {
        when (activeTool) {
            "glass" -> {
                CssCodeGenerator.generateGlass(
                    blur = viewModel.getFloat("glass_blur", settings),
                    opacity = viewModel.getFloat("glass_opacity", settings),
                    saturation = viewModel.getFloat("glass_saturation", settings),
                    radius = viewModel.getFloat("glass_radius", settings),
                    colorHex = viewModel.getStr("glass_color", settings),
                    darkText = viewModel.getBool("glass_dark_text", settings),
                    bgUrl = viewModel.getStr("glass_bg_url", settings)
                )
            }
            "slider" -> {
                CssCodeGenerator.generateSlider(
                    urlsString = viewModel.getStr("slider_urls", settings),
                    speed = viewModel.getFloat("slider_speed", settings),
                    radius = viewModel.getFloat("slider_radius", settings),
                    showArrows = viewModel.getBool("slider_show_arrows", settings)
                )
            }
            "header" -> {
                CssCodeGenerator.generateHeader(
                    bgColor = viewModel.getStr("header_bg_color", settings),
                    blur = viewModel.getFloat("header_blur", settings),
                    height = viewModel.getFloat("header_height", settings),
                    shadow = viewModel.getFloat("header_shadow", settings),
                    borderBottom = viewModel.getBool("header_border_bottom", settings),
                    brandText = viewModel.getStr("header_brand_text", settings),
                    navLinks = viewModel.getStr("header_nav_links", settings)
                )
            }
            "shadow" -> {
                CssCodeGenerator.generateShadow(
                    offsetX = viewModel.getFloat("shadow_offset_x", settings),
                    offsetY = viewModel.getFloat("shadow_offset_y", settings),
                    blur = viewModel.getFloat("shadow_blur", settings),
                    spread = viewModel.getFloat("shadow_spread", settings),
                    colorHex = viewModel.getStr("shadow_color", settings),
                    opacity = viewModel.getFloat("shadow_opacity", settings),
                    inset = viewModel.getBool("shadow_inset", settings),
                    radius = viewModel.getFloat("shadow_radius", settings)
                )
            }
            "gradient" -> {
                CssCodeGenerator.generateGradient(
                    type = viewModel.getStr("gradient_type", settings),
                    angle = viewModel.getFloat("gradient_angle", settings),
                    color1 = viewModel.getStr("gradient_color1", settings),
                    stop1 = viewModel.getFloat("gradient_stop1", settings),
                    color2 = viewModel.getStr("gradient_color2", settings),
                    stop2 = viewModel.getFloat("gradient_stop2", settings),
                    useColor3 = viewModel.getBool("gradient_use_color3", settings),
                    color3 = viewModel.getStr("gradient_color3", settings),
                    stop3 = viewModel.getFloat("gradient_stop3", settings)
                )
            }
            "button" -> {
                CssCodeGenerator.generateButton(
                    label = viewModel.getStr("button_label", settings),
                    style = viewModel.getStr("button_style", settings),
                    bgColor = viewModel.getStr("button_bg_color", settings),
                    textColor = viewModel.getStr("button_text_color", settings),
                    radius = viewModel.getFloat("button_radius", settings),
                    fontSize = viewModel.getFloat("button_font_size", settings),
                    paddingH = viewModel.getFloat("button_padding_h", settings),
                    paddingV = viewModel.getFloat("button_padding_v", settings),
                    hoverFx = viewModel.getStr("button_hover_fx", settings)
                )
            }
            "ticker" -> {
                CssCodeGenerator.generateTicker(
                    itemsText = viewModel.getStr("ticker_items", settings),
                    speed = viewModel.getFloat("ticker_speed", settings),
                    direction = viewModel.getStr("ticker_direction", settings),
                    bgColor = viewModel.getStr("ticker_bg_color", settings),
                    textColor = viewModel.getStr("ticker_text_color", settings),
                    fontSize = viewModel.getFloat("ticker_font_size", settings),
                    gap = viewModel.getFloat("ticker_gap", settings)
                )
            }
            else -> {
                CssCodeGenerator.generateLoader(
                    style = viewModel.getStr("loader_style", settings),
                    color = viewModel.getStr("loader_color", settings),
                    size = viewModel.getFloat("loader_size", settings),
                    speed = viewModel.getFloat("loader_speed", settings),
                    stroke = viewModel.getFloat("loader_stroke", settings)
                )
            }
        }
    }

    val activeOutputText = if (activeTab == "css") codes.first else codes.second

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("CSS Toolkit Output", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label Copied to Clipboard!", Toast.LENGTH_SHORT).show()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            // Wide Screen Layout (Sidebar + Two columns)
            Row(modifier = Modifier.fillMaxSize()) {
                // Sidebar
                SidebarSection(
                    activeTool = activeTool,
                    onToolSelected = { viewModel.selectTool(it) },
                    onShowPrivacy = { showPrivacyDialog = true },
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight()
                )

                VerticalDivider(color = Color(0xFF222222), thickness = 1.dp)

                // Main Workspace: Left is Controls, Right is Preview & Output
                Row(modifier = Modifier.weight(1f)) {
                    // Left: Controls Column
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .background(Color(0xFF121212))
                            .padding(16.dp)
                    ) {
                        Column {
                            WorkspaceHeader(activeTool = activeTool)
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF222222))
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                ControlsPanel(
                                    activeTool = activeTool,
                                    settings = settings,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }

                    VerticalDivider(color = Color(0xFF222222), thickness = 1.dp)

                    // Right: Live Preview (Top) & Output Area (Bottom)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // Top: Live Preview
                        Box(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxWidth()
                        ) {
                            LivePreviewBox(
                                activeTool = activeTool,
                                settings = settings,
                                viewModel = viewModel
                            )
                        }

                        HorizontalDivider(color = Color(0xFF222222), thickness = 1.dp)

                        // Bottom: Code Output
                        if (activeTool != "guide") {
                            CodeOutputSection(
                                activeTab = activeTab,
                                activeOutputText = activeOutputText,
                                onTabSelect = { viewModel.selectTab(it) },
                                onCopyClick = { copyToClipboard(activeOutputText, activeTab.uppercase()) },
                                modifier = Modifier
                                    .height(240.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Screen Layout (Top Horizontal Bar + Sticky Live Preview + Scrollable Controls + Fixed Output)
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header and Categories Row
                MobileHeaderSection(
                    activeTool = activeTool,
                    onToolSelected = { viewModel.selectTool(it) },
                    onShowPrivacy = { showPrivacyDialog = true }
                )

                HorizontalDivider(color = Color(0xFF222222))

                // Sticky Live Preview
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    LivePreviewBox(
                        activeTool = activeTool,
                        settings = settings,
                        viewModel = viewModel
                    )
                }

                HorizontalDivider(color = Color(0xFF222222))

                // Scrollable Controls
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF121212))
                        .padding(16.dp)
                ) {
                    ControlsPanel(
                        activeTool = activeTool,
                        settings = settings,
                        viewModel = viewModel
                    )
                }

                // Bottom Output
                if (activeTool != "guide") {
                    HorizontalDivider(color = Color(0xFF222222))
                    CodeOutputSection(
                        activeTab = activeTab,
                        activeOutputText = activeOutputText,
                        onTabSelect = { viewModel.selectTab(it) },
                        onCopyClick = { copyToClipboard(activeOutputText, activeTab.uppercase()) },
                        modifier = Modifier
                            .height(210.dp)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }
}

@Composable
fun MobileHeaderSection(
    activeTool: String,
    onToolSelected: (String) -> Unit,
    onShowPrivacy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212))
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "CSS TOOLKIT",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                // Offline badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4ADE80))
                    )
                    Text(
                        text = "SQLite Active / Offline Ready",
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            IconButton(onClick = onShowPrivacy) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = "Privacy Policy",
                    tint = Color(0xFF888888)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal scrolling category bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tools = listOf(
                Pair("glass", "Glass"),
                Pair("slider", "Slider"),
                Pair("header", "Header"),
                Pair("shadow", "Shadow"),
                Pair("gradient", "Gradient"),
                Pair("button", "Button"),
                Pair("ticker", "Ticker"),
                Pair("loader", "Loader"),
                Pair("guide", "Guidelines")
            )

            tools.forEach { (id, name) ->
                val isActive = activeTool == id
                val bracketedText = if (isActive) "[ $name ]" else "▪ $name"
                Text(
                    text = bracketedText,
                    color = if (isActive) Color.White else Color(0xFF888888),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onToolSelected(id) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("mobile_category_$id")
                )
            }
        }
    }
}

@Composable
fun SidebarSection(
    activeTool: String,
    onToolSelected: (String) -> Unit,
    onShowPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF121212))
            .padding(20.dp)
    ) {
        // Logo
        Text(
            text = "CSS TOOLKIT",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        
        // Status Badge
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80))
            )
            Text(
                text = "Offline Ready",
                color = Color(0xFF888888),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "// generators",
            color = Color(0xFF666666),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val generators = listOf(
            Pair("glass", "Glass"),
            Pair("slider", "Slider"),
            Pair("header", "Header"),
            Pair("shadow", "Shadow"),
            Pair("gradient", "Gradient"),
            Pair("button", "Button"),
            Pair("ticker", "Ticker"),
            Pair("loader", "Loader")
        )

        generators.forEach { (id, label) ->
            val isActive = activeTool == id
            SidebarButton(
                label = label,
                isActive = isActive,
                onClick = { onToolSelected(id) },
                modifier = Modifier.testTag("sidebar_gen_$id")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "// docs",
            color = Color(0xFF666666),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        SidebarButton(
            label = "Guidelines",
            isActive = activeTool == "guide",
            onClick = { onToolSelected("guide") },
            modifier = Modifier.testTag("sidebar_guide")
        )

        Spacer(modifier = Modifier.weight(1f))

        // Privacy Policy Link
        Text(
            text = "PRIVACY POLICY",
            color = Color(0xFF888888),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onShowPrivacy() }
                .padding(vertical = 12.dp)
                .testTag("privacy_policy_button")
        )
    }
}

@Composable
fun SidebarButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val text = if (isActive) "[ $label ]" else "▪ $label"
    Text(
        text = text,
        color = if (isActive) Color.White else Color(0xFF888888),
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
    )
}

@Composable
fun WorkspaceHeader(activeTool: String) {
    val (title, desc) = when (activeTool) {
        "glass" -> Pair("Glassmorphism", "Frosted glass card backdrop effects")
        "slider" -> Pair("Image Slider", "Responsive and modular image galleries")
        "header" -> Pair("Sticky Header", "Beautiful transparent navigation cards")
        "shadow" -> Pair("Box Shadow", "Modern high-precision elements shadow")
        "gradient" -> Pair("Gradient BG", "Smooth conic and linear transitions")
        "button" -> Pair("Button Styler", "Interactive and beautiful buttons styling")
        "ticker" -> Pair("Scroll Ticker", "Infinite scrolling announcements banners")
        "loader" -> Pair("CSS Loader", "Animated pure-CSS loading graphics")
        else -> Pair("Guidelines", "Complete developer offline manuals")
    }

    Column {
        Text(
            text = title.uppercase(),
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black
        )
        Text(
            text = desc,
            color = Color(0xFF888888),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun LivePreviewBox(
    activeTool: String,
    settings: Map<String, String>,
    viewModel: MainViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Preview Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Monitor,
                contentDescription = "Preview",
                tint = Color(0xFF888888),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE PREVIEW",
                color = Color(0xFF888888),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Preview Element Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .drawBehind {
                    // Draw designer grid/checkers background pattern
                    val sizePx = 16.dp.toPx()
                    for (x in 0 until (size.width / sizePx).toInt() + 1) {
                        for (y in 0 until (size.height / sizePx).toInt() + 1) {
                            if ((x + y) % 2 == 0) {
                                drawRect(
                                    color = Color(0xFF141414),
                                    topLeft = Offset(x * sizePx, y * sizePx),
                                    size = androidx.compose.ui.geometry.Size(sizePx, sizePx)
                                )
                            }
                        }
                    }
                }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (activeTool) {
                "glass" -> {
                    GlassmorphismCard(
                        blurVal = viewModel.getFloat("glass_blur", settings),
                        opacityVal = viewModel.getFloat("glass_opacity", settings),
                        saturationVal = viewModel.getFloat("glass_saturation", settings),
                        radiusVal = viewModel.getFloat("glass_radius", settings),
                        colorHex = viewModel.getStr("glass_color", settings),
                        darkText = viewModel.getBool("glass_dark_text", settings),
                        bgUrl = viewModel.getStr("glass_bg_url", settings)
                    )
                }
                "slider" -> {
                    InteractiveImageSlider(
                        urlsString = viewModel.getStr("slider_urls", settings),
                        speed = viewModel.getFloat("slider_speed", settings),
                        radius = viewModel.getFloat("slider_radius", settings),
                        showArrows = viewModel.getBool("slider_show_arrows", settings)
                    )
                }
                "header" -> {
                    MockStickyHeaderViewport(
                        bgColorHex = viewModel.getStr("header_bg_color", settings),
                        blurVal = viewModel.getFloat("header_blur", settings),
                        heightVal = viewModel.getFloat("header_height", settings),
                        shadowVal = viewModel.getFloat("header_shadow", settings),
                        borderBottom = viewModel.getBool("header_border_bottom", settings),
                        brandText = viewModel.getStr("header_brand_text", settings),
                        navLinks = viewModel.getStr("header_nav_links", settings)
                    )
                }
                "shadow" -> {
                    CustomBoxShadowPreview(
                        offsetX = viewModel.getFloat("shadow_offset_x", settings),
                        offsetY = viewModel.getFloat("shadow_offset_y", settings),
                        blurVal = viewModel.getFloat("shadow_blur", settings),
                        spreadVal = viewModel.getFloat("shadow_spread", settings),
                        colorHex = viewModel.getStr("shadow_color", settings),
                        opacityVal = viewModel.getFloat("shadow_opacity", settings),
                        inset = viewModel.getBool("shadow_inset", settings),
                        radiusVal = viewModel.getFloat("shadow_radius", settings)
                    )
                }
                "gradient" -> {
                    val gType = viewModel.getStr("gradient_type", settings)
                    val c1 = viewModel.getStr("gradient_color1", settings)
                    val c2 = viewModel.getStr("gradient_color2", settings)
                    val use3 = viewModel.getBool("gradient_use_color3", settings)
                    val c3 = viewModel.getStr("gradient_color3", settings)
                    val angle = viewModel.getFloat("gradient_angle", settings)

                    val color1 = try { Color(android.graphics.Color.parseColor(c1)) } catch(e: Exception) { Color(0xFF4FACFE) }
                    val color2 = try { Color(android.graphics.Color.parseColor(c2)) } catch(e: Exception) { Color(0xFF00F2FE) }
                    val color3 = try { Color(android.graphics.Color.parseColor(c3)) } catch(e: Exception) { Color.Black }

                    val colorsList = if (use3) listOf(color1, color2, color3) else listOf(color1, color2)

                    val brush = when (gType) {
                        "linear-gradient" -> {
                            // Calculate simple angles
                            val rad = Math.toRadians(angle.toDouble())
                            val endX = Math.cos(rad).toFloat() * 1000f + 500f
                            val endY = Math.sin(rad).toFloat() * 1000f + 500f
                            Brush.linearGradient(
                                colors = colorsList,
                                start = Offset(500f - Math.cos(rad).toFloat() * 500f, 500f - Math.sin(rad).toFloat() * 500f),
                                end = Offset(endX, endY)
                            )
                        }
                        "radial-gradient" -> {
                            Brush.radialGradient(colors = colorsList)
                        }
                        else -> {
                            Brush.sweepGradient(colors = colorsList)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(240.dp, 140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GRADIENT",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                "button" -> {
                    val label = viewModel.getStr("button_label", settings)
                    val style = viewModel.getStr("button_style", settings)
                    val bgColHex = viewModel.getStr("button_bg_color", settings)
                    val textColHex = viewModel.getStr("button_text_color", settings)
                    val radius = viewModel.getFloat("button_radius", settings)
                    val fSize = viewModel.getFloat("button_font_size", settings)
                    val padH = viewModel.getFloat("button_padding_h", settings)
                    val padV = viewModel.getFloat("button_padding_v", settings)

                    val bgColor = try { Color(android.graphics.Color.parseColor(bgColHex)) } catch(e: Exception) { Color(0xFF3B82F6) }
                    val textColor = try { Color(android.graphics.Color.parseColor(textColHex)) } catch(e: Exception) { Color.White }

                    val isOutline = style == "outline"
                    val isGhost = style == "ghost"
                    val isGrad = style == "gradient"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(radius.dp))
                            .then(
                                when {
                                    isOutline -> Modifier.border(2.dp, bgColor, RoundedCornerShape(radius.dp))
                                    isGrad -> Modifier.background(
                                        Brush.linearGradient(colors = listOf(bgColor, Color(0xFF555555)))
                                    )
                                    isGhost -> Modifier
                                    else -> Modifier.background(bgColor)
                                }
                            )
                            .clickable { /* Simulate hover/click ripple */ }
                            .padding(horizontal = padH.dp, vertical = padV.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isOutline || isGhost) bgColor else textColor,
                            fontSize = fSize.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "ticker" -> {
                    val itemsString = viewModel.getStr("ticker_items", settings)
                    val tickerBg = viewModel.getStr("ticker_bg_color", settings)
                    val tickerText = viewModel.getStr("ticker_text_color", settings)
                    val fSize = viewModel.getFloat("ticker_font_size", settings)
                    val gap = viewModel.getFloat("ticker_gap", settings)

                    val bgCol = try { Color(android.graphics.Color.parseColor(tickerBg)) } catch(e: Exception) { Color(0xFF3B82F6) }
                    val textCol = try { Color(android.graphics.Color.parseColor(tickerText)) } catch(e: Exception) { Color.White }

                    val itemsList = itemsString.split("\n").filter { it.trim().isNotEmpty() }
                    val marqueeString = if (itemsList.isNotEmpty()) {
                        (itemsList + itemsList).joinToString(" ".repeat((gap / 6f).toInt().coerceAtLeast(3)))
                    } else {
                        "No Ticker Items"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgCol)
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = marqueeString,
                            color = textCol,
                            fontSize = fSize.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.horizontalScroll(rememberScrollState()) // Simulates running ticker smoothly on click-drag or auto
                        )
                    }
                }
                "loader" -> {
                    LoaderPreview(
                        style = viewModel.getStr("loader_style", settings),
                        colorHex = viewModel.getStr("loader_color", settings),
                        size = viewModel.getFloat("loader_size", settings),
                        speed = viewModel.getFloat("loader_speed", settings),
                        strokeWidth = viewModel.getFloat("loader_stroke", settings)
                    )
                }
                "guide" -> {
                    GuidelinesPreviewSection()
                }
            }
        }
    }
}

@Composable
fun GuidelinesPreviewSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. ALL YOU NEED TO KNOW",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Welcome to CSS Toolkit! This app serves as your offline companion for generating modern production-grade CSS styles instantly. No coding skills required.",
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. ADJUSTABLE GENERATORS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pick components from the lists like Glassmorphism, Slider, or Sticky Header. Drag sliders, type hex colors, or upload visual assets. Check updates live instantly in the preview panel above.",
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3. PERSISTENT OFFLINE STORAGE",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Any tweak or asset configuration you set is securely cached directly into a high-performance local SQLite database via Room. Close the app and resume work later anytime offline!",
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun CodeOutputSection(
    activeTab: String,
    activeOutputText: String,
    onTabSelect: (String) -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(Color(0xFF121212))
    ) {
        // Output Tab row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                listOf(
                    Pair("css", "CSS OUTPUT"),
                    Pair("html", "HTML")
                ).forEach { (id, label) ->
                    val isSel = activeTab == id
                    Box(
                        modifier = Modifier
                            .clickable { onTabSelect(id) }
                            .background(if (isSel) Color(0xFF121212) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (isSel) "[ $label ]" else label,
                            color = if (isSel) Color.White else Color(0xFF888888),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = onCopyClick) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Output",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Monospaced Code Text block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF070707))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = activeOutputText,
                color = Color(0xFF4ADE80),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ControlsPanel(
    activeTool: String,
    settings: Map<String, String>,
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (activeTool) {
            "glass" -> {
                // Blur
                SliderControl(
                    label = "Blur",
                    value = viewModel.getFloat("glass_blur", settings),
                    min = 0f,
                    max = 40f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("glass_blur", it.toInt().toString()) }
                )

                // Opacity
                SliderControl(
                    label = "Opacity",
                    value = viewModel.getFloat("glass_opacity", settings),
                    min = 0f,
                    max = 80f,
                    suffix = "%",
                    onValueChange = { viewModel.updateSetting("glass_opacity", it.toInt().toString()) }
                )

                // Saturation
                SliderControl(
                    label = "Saturation",
                    value = viewModel.getFloat("glass_saturation", settings),
                    min = 100f,
                    max = 300f,
                    suffix = "%",
                    onValueChange = { viewModel.updateSetting("glass_saturation", it.toInt().toString()) }
                )

                // Radius
                SliderControl(
                    label = "Border Radius",
                    value = viewModel.getFloat("glass_radius", settings),
                    min = 0f,
                    max = 48f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("glass_radius", it.toInt().toString()) }
                )

                // Color Hex
                ColorPickerControl(
                    label = "Card Background Color",
                    currentColor = viewModel.getStr("glass_color", settings),
                    onColorSelect = { viewModel.updateSetting("glass_color", it) }
                )

                // Dark text theme toggle
                ToggleControl(
                    label = "Use Dark Text Color",
                    checked = viewModel.getBool("glass_dark_text", settings),
                    onCheckedChange = { viewModel.updateSetting("glass_dark_text", it.toString()) }
                )

                // Custom Background Image URL
                TextInputControl(
                    label = "Custom Background Image URL",
                    value = viewModel.getStr("glass_bg_url", settings),
                    placeholder = "https://images.unsplash.com/...",
                    onValueChange = { viewModel.updateSetting("glass_bg_url", it) }
                )
            }

            "slider" -> {
                // Speed
                SliderControl(
                    label = "Transition Looping Duration",
                    value = viewModel.getFloat("slider_speed", settings),
                    min = 1f,
                    max = 10f,
                    suffix = "s",
                    onValueChange = { viewModel.updateSetting("slider_speed", it.toInt().toString()) }
                )

                // Radius
                SliderControl(
                    label = "Corners Rounded",
                    value = viewModel.getFloat("slider_radius", settings),
                    min = 0f,
                    max = 50f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("slider_radius", it.toInt().toString()) }
                )

                // Arrows
                ToggleControl(
                    label = "Show Navigation Toggles",
                    checked = viewModel.getBool("slider_show_arrows", settings),
                    onCheckedChange = { viewModel.updateSetting("slider_show_arrows", it.toString()) }
                )

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = Color(0xFF222222))
                Spacer(modifier = Modifier.height(4.dp))

                // Image URLs slot editor
                Text(
                    text = "SLIDER IMAGES",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                val sliderUrlsString = viewModel.getStr("slider_urls", settings)
                val urls = sliderUrlsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                urls.forEachIndexed { index, url ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = url,
                            onValueChange = { newVal ->
                                val list = urls.toMutableList()
                                list[index] = newVal
                                viewModel.updateSetting("slider_urls", list.joinToString(","))
                            },
                            placeholder = { Text("Image URL", color = Color.Gray, fontFamily = FontFamily.Monospace) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0xFF222222),
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        IconButton(
                            onClick = {
                                val list = urls.toMutableList()
                                list.removeAt(index)
                                viewModel.updateSetting("slider_urls", list.joinToString(","))
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Slot", tint = Color(0xFFEF4444))
                        }
                    }
                }

                // Add Slot
                Button(
                    onClick = {
                        val updated = if (sliderUrlsString.isEmpty()) {
                            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800"
                        } else {
                            "$sliderUrlsString,https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800"
                        }
                        viewModel.updateSetting("slider_urls", updated)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add URL", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ADD IMAGE SLOT", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                // Native Android Photo Picker integration
                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        if (uri != null) {
                            val updated = if (sliderUrlsString.isEmpty()) {
                                uri.toString()
                            } else {
                                "$sliderUrlsString,${uri.toString()}"
                            }
                            viewModel.updateSetting("slider_urls", updated)
                        }
                    }
                )

                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Select Photo", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "CHOOSE LOCAL IMAGE FILE", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }

            "header" -> {
                // BG Color
                ColorPickerControl(
                    label = "Background Color",
                    currentColor = viewModel.getStr("header_bg_color", settings),
                    onColorSelect = { viewModel.updateSetting("header_bg_color", it) }
                )

                // Blur
                SliderControl(
                    label = "Backdrop Blur Layer",
                    value = viewModel.getFloat("header_blur", settings),
                    min = 0f,
                    max = 30f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("header_blur", it.toInt().toString()) }
                )

                // Height
                SliderControl(
                    label = "Header Height Block",
                    value = viewModel.getFloat("header_height", settings),
                    min = 40f,
                    max = 120f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("header_height", it.toInt().toString()) }
                )

                // Shadow
                SliderControl(
                    label = "Bottom Drop Shadow",
                    value = viewModel.getFloat("header_shadow", settings),
                    min = 0f,
                    max = 40f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("header_shadow", it.toInt().toString()) }
                )

                // Border Bottom
                ToggleControl(
                    label = "Draw Bottom Border Row",
                    checked = viewModel.getBool("header_border_bottom", settings),
                    onCheckedChange = { viewModel.updateSetting("header_border_bottom", it.toString()) }
                )

                // Brand Text
                TextInputControl(
                    label = "Brand Title text",
                    value = viewModel.getStr("header_brand_text", settings),
                    placeholder = "BRAND",
                    onValueChange = { viewModel.updateSetting("header_brand_text", it) }
                )

                // Nav Links
                TextInputControl(
                    label = "Navigation Links (comma separated)",
                    value = viewModel.getStr("header_nav_links", settings),
                    placeholder = "Home, About, Contact",
                    onValueChange = { viewModel.updateSetting("header_nav_links", it) }
                )
            }

            "shadow" -> {
                // X Offset
                SliderControl(
                    label = "Shadow Horizontal Offset (X)",
                    value = viewModel.getFloat("shadow_offset_x", settings),
                    min = -50f,
                    max = 50f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("shadow_offset_x", it.toInt().toString()) }
                )

                // Y Offset
                SliderControl(
                    label = "Shadow Vertical Offset (Y)",
                    value = viewModel.getFloat("shadow_offset_y", settings),
                    min = -50f,
                    max = 50f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("shadow_offset_y", it.toInt().toString()) }
                )

                // Blur
                SliderControl(
                    label = "Blur Spread Radius",
                    value = viewModel.getFloat("shadow_blur", settings),
                    min = 0f,
                    max = 100f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("shadow_blur", it.toInt().toString()) }
                )

                // Spread
                SliderControl(
                    label = "Shadow Spread Range",
                    value = viewModel.getFloat("shadow_spread", settings),
                    min = -30f,
                    max = 30f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("shadow_spread", it.toInt().toString()) }
                )

                // Color
                ColorPickerControl(
                    label = "Shadow Color",
                    currentColor = viewModel.getStr("shadow_color", settings),
                    onColorSelect = { viewModel.updateSetting("shadow_color", it) }
                )

                // Opacity
                SliderControl(
                    label = "Shadow Intensity / Opacity",
                    value = viewModel.getFloat("shadow_opacity", settings),
                    min = 0f,
                    max = 100f,
                    suffix = "%",
                    onValueChange = { viewModel.updateSetting("shadow_opacity", it.toInt().toString()) }
                )

                // Inset
                ToggleControl(
                    label = "Render as Inset Shadow",
                    checked = viewModel.getBool("shadow_inset", settings),
                    onCheckedChange = { viewModel.updateSetting("shadow_inset", it.toString()) }
                )

                // Box Corner Radius
                SliderControl(
                    label = "Element Corner Radius",
                    value = viewModel.getFloat("shadow_radius", settings),
                    min = 0f,
                    max = 50f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("shadow_radius", it.toInt().toString()) }
                )
            }

            "gradient" -> {
                // Type dropdown
                val currentType = viewModel.getStr("gradient_type", settings)
                Text(
                    text = "GRADIENT TYPE",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (currentType) {
                            "linear-gradient" -> "Linear"
                            "radial-gradient" -> "Radial"
                            else -> "Conic"
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0xFF222222)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Linear", color = Color.White, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                viewModel.updateSetting("gradient_type", "linear-gradient")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Radial", color = Color.White, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                viewModel.updateSetting("gradient_type", "radial-gradient")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Conic", color = Color.White, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                viewModel.updateSetting("gradient_type", "conic-gradient")
                                expanded = false
                            }
                        )
                    }
                }

                // Angle
                SliderControl(
                    label = "Transition Angle",
                    value = viewModel.getFloat("gradient_angle", settings),
                    min = 0f,
                    max = 360f,
                    suffix = "°",
                    onValueChange = { viewModel.updateSetting("gradient_angle", it.toInt().toString()) }
                )

                // Color 1
                ColorPickerControl(
                    label = "Color Stop 1",
                    currentColor = viewModel.getStr("gradient_color1", settings),
                    onColorSelect = { viewModel.updateSetting("gradient_color1", it) }
                )

                // Stop 1
                SliderControl(
                    label = "Color Stop 1 Range",
                    value = viewModel.getFloat("gradient_stop1", settings),
                    min = 0f,
                    max = 100f,
                    suffix = "%",
                    onValueChange = { viewModel.updateSetting("gradient_stop1", it.toInt().toString()) }
                )

                // Color 2
                ColorPickerControl(
                    label = "Color Stop 2",
                    currentColor = viewModel.getStr("gradient_color2", settings),
                    onColorSelect = { viewModel.updateSetting("gradient_color2", it) }
                )

                // Stop 2
                SliderControl(
                    label = "Color Stop 2 Range",
                    value = viewModel.getFloat("gradient_stop2", settings),
                    min = 0f,
                    max = 100f,
                    suffix = "%",
                    onValueChange = { viewModel.updateSetting("gradient_stop2", it.toInt().toString()) }
                )

                // Toggle 3
                ToggleControl(
                    label = "Use Color Stop 3",
                    checked = viewModel.getBool("gradient_use_color3", settings),
                    onCheckedChange = { viewModel.updateSetting("gradient_use_color3", it.toString()) }
                )

                if (viewModel.getBool("gradient_use_color3", settings)) {
                    // Color 3
                    ColorPickerControl(
                        label = "Color Stop 3",
                        currentColor = viewModel.getStr("gradient_color3", settings),
                        onColorSelect = { viewModel.updateSetting("gradient_color3", it) }
                    )

                    // Stop 3
                    SliderControl(
                        label = "Color Stop 3 Range",
                        value = viewModel.getFloat("gradient_stop3", settings),
                        min = 0f,
                        max = 100f,
                        suffix = "%",
                        onValueChange = { viewModel.updateSetting("gradient_stop3", it.toInt().toString()) }
                    )
                }
            }

            "button" -> {
                // Button Label
                TextInputControl(
                    label = "Button Label Text",
                    value = viewModel.getStr("button_label", settings),
                    placeholder = "Get Started",
                    onValueChange = { viewModel.updateSetting("button_label", it) }
                )

                // Style select
                val buttonStyle = viewModel.getStr("button_style", settings)
                Text(
                    text = "BUTTON PRESENTATION STYLE",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                var styleExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = styleExpanded,
                    onExpandedChange = { styleExpanded = !styleExpanded }
                ) {
                    OutlinedTextField(
                        value = buttonStyle.uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = styleExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0xFF222222)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = styleExpanded,
                        onDismissRequest = { styleExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        listOf("solid", "outline", "ghost", "gradient").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.uppercase(), color = Color.White, fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    viewModel.updateSetting("button_style", st)
                                    styleExpanded = false
                                }
                            )
                        }
                    }
                }

                // BG Color
                ColorPickerControl(
                    label = "Button Accent Color",
                    currentColor = viewModel.getStr("button_bg_color", settings),
                    onColorSelect = { viewModel.updateSetting("button_bg_color", it) }
                )

                // Text Color
                ColorPickerControl(
                    label = "Button Text Color",
                    currentColor = viewModel.getStr("button_text_color", settings),
                    onColorSelect = { viewModel.updateSetting("button_text_color", it) }
                )

                // Radius
                SliderControl(
                    label = "Corner Border Radius",
                    value = viewModel.getFloat("button_radius", settings),
                    min = 0f,
                    max = 50f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("button_radius", it.toInt().toString()) }
                )

                // Font Size
                SliderControl(
                    label = "Label Font Size",
                    value = viewModel.getFloat("button_font_size", settings),
                    min = 12f,
                    max = 24f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("button_font_size", it.toInt().toString()) }
                )

                // Padding H
                SliderControl(
                    label = "Horizontal Padding",
                    value = viewModel.getFloat("button_padding_h", settings),
                    min = 8f,
                    max = 60f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("button_padding_h", it.toInt().toString()) }
                )

                // Padding V
                SliderControl(
                    label = "Vertical Padding",
                    value = viewModel.getFloat("button_padding_v", settings),
                    min = 4f,
                    max = 30f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("button_padding_v", it.toInt().toString()) }
                )

                // Hover fx
                val hoverFx = viewModel.getStr("button_hover_fx", settings)
                Text(
                    text = "HOVER ACTION EFFECT",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                var hoverExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = hoverExpanded,
                    onExpandedChange = { hoverExpanded = !hoverExpanded }
                ) {
                    OutlinedTextField(
                        value = when (hoverFx) {
                            "scale" -> "SCALE UP"
                            "lighten" -> "LIGHTEN ALPHA"
                            "shadow" -> "GLOW SHADOW"
                            else -> "NONE"
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hoverExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0xFF222222)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = hoverExpanded,
                        onDismissRequest = { hoverExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        listOf(
                            Pair("scale", "SCALE UP"),
                            Pair("lighten", "LIGHTEN ALPHA"),
                            Pair("shadow", "GLOW SHADOW"),
                            Pair("none", "NONE")
                        ).forEach { (fxId, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = Color.White, fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    viewModel.updateSetting("button_hover_fx", fxId)
                                    hoverExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            "ticker" -> {
                // Ticker items
                Text(
                    text = "BANNER ITEMS (ONE PER LINE)",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = viewModel.getStr("ticker_items", settings),
                    onValueChange = { viewModel.updateSetting("ticker_items", it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF222222),
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Speed
                SliderControl(
                    label = "Animation Duration Speed",
                    value = viewModel.getFloat("ticker_speed", settings),
                    min = 3f,
                    max = 30f,
                    suffix = "s",
                    onValueChange = { viewModel.updateSetting("ticker_speed", it.toInt().toString()) }
                )

                // Direction dropdown
                val dir = viewModel.getStr("ticker_direction", settings)
                Text(
                    text = "SCROLL DIRECTION",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                var dirExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = dirExpanded,
                    onExpandedChange = { dirExpanded = !dirExpanded }
                ) {
                    OutlinedTextField(
                        value = if (dir == "left") "LEFT" else "RIGHT",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dirExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0xFF222222)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = dirExpanded,
                        onDismissRequest = { dirExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        DropdownMenuItem(
                            text = { Text("LEFT", color = Color.White, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                viewModel.updateSetting("ticker_direction", "left")
                                dirExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("RIGHT", color = Color.White, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                viewModel.updateSetting("ticker_direction", "right")
                                dirExpanded = false
                            }
                        )
                    }
                }

                // BG Color
                ColorPickerControl(
                    label = "Banner Accent Color",
                    currentColor = viewModel.getStr("ticker_bg_color", settings),
                    onColorSelect = { viewModel.updateSetting("ticker_bg_color", it) }
                )

                // Text Color
                ColorPickerControl(
                    label = "Banner Text Color",
                    currentColor = viewModel.getStr("ticker_text_color", settings),
                    onColorSelect = { viewModel.updateSetting("ticker_text_color", it) }
                )

                // Font Size
                SliderControl(
                    label = "Banner Text Size",
                    value = viewModel.getFloat("ticker_font_size", settings),
                    min = 10f,
                    max = 32f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("ticker_font_size", it.toInt().toString()) }
                )

                // Gap
                SliderControl(
                    label = "Spans Gap Margin",
                    value = viewModel.getFloat("ticker_gap", settings),
                    min = 10f,
                    max = 120f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("ticker_gap", it.toInt().toString()) }
                )
            }

            "loader" -> {
                // Style select
                val lStyle = viewModel.getStr("loader_style", settings)
                Text(
                    text = "SPINNER ANIMATION STYLE",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                var lExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = lExpanded,
                    onExpandedChange = { lExpanded = !lExpanded }
                ) {
                    OutlinedTextField(
                        value = when (lStyle) {
                            "spinner" -> "RING"
                            "dots" -> "BOUNCING DOTS"
                            "pulse" -> "PULSE"
                            else -> "VERTICAL BARS"
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color(0xFF222222)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = lExpanded,
                        onDismissRequest = { lExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        listOf(
                            Pair("spinner", "RING"),
                            Pair("dots", "BOUNCING DOTS"),
                            Pair("pulse", "PULSE"),
                            Pair("bars", "VERTICAL BARS")
                        ).forEach { (id, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = Color.White, fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    viewModel.updateSetting("loader_style", id)
                                    lExpanded = false
                                }
                            )
                        }
                    }
                }

                // Color
                ColorPickerControl(
                    label = "Loader Ink Color",
                    currentColor = viewModel.getStr("loader_color", settings),
                    onColorSelect = { viewModel.updateSetting("loader_color", it) }
                )

                // Size
                SliderControl(
                    label = "Loader Dimensions Block",
                    value = viewModel.getFloat("loader_size", settings),
                    min = 20f,
                    max = 100f,
                    suffix = "px",
                    onValueChange = { viewModel.updateSetting("loader_size", it.toInt().toString()) }
                )

                // Speed
                SliderControl(
                    label = "Animation Duration Clock",
                    value = viewModel.getFloat("loader_speed", settings),
                    min = 3f,
                    max = 30f,
                    suffix = "s",
                    valueMultiplier = 0.1f, // represented as tenths of a second
                    onValueChange = { viewModel.updateSetting("loader_speed", it.toInt().toString()) }
                )

                // Stroke width
                if (lStyle == "spinner") {
                    SliderControl(
                        label = "Ring Path Thickness",
                        value = viewModel.getFloat("loader_stroke", settings),
                        min = 2f,
                        max = 16f,
                        suffix = "px",
                        onValueChange = { viewModel.updateSetting("loader_stroke", it.toInt().toString()) }
                    )
                }
            }

            else -> {
                // Guidelines Panel TOC
                Text(
                    text = "MANUAL CONTENTS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                listOf(
                    "1. Introduction",
                    "2. Using Generators",
                    "3. Persistent SQLite"
                ).forEach { heading ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Scroll is handled by viewport! */ },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Book, contentDescription = "Topic", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = heading.uppercase(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SliderControl(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    suffix: String,
    valueMultiplier: Float = 1.0f,
    onValueChange: (Float) -> Unit
) {
    val displayValue = if (valueMultiplier != 1.0f) {
        String.format("%.1f", value * valueMultiplier)
    } else {
        value.toInt().toString()
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.uppercase(),
                color = Color(0xFF888888),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$displayValue$suffix",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color(0xFF222222)
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun ToggleControl(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            color = Color(0xFF888888),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color.White,
                uncheckedThumbColor = Color(0xFF888888),
                uncheckedTrackColor = Color(0xFF222222)
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerControl(
    label: String,
    currentColor: String,
    onColorSelect: (String) -> Unit
) {
    Column {
        Text(
            text = label.uppercase(),
            color = Color(0xFF888888),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Custom Hex input
        OutlinedTextField(
            value = currentColor,
            onValueChange = { newVal ->
                if (newVal.startsWith("#") && newVal.length <= 7) {
                    onColorSelect(newVal)
                } else if (!newVal.startsWith("#") && newVal.length <= 6) {
                    onColorSelect("#$newVal")
                }
            },
            placeholder = { Text("#3B82F6", color = Color.Gray, fontFamily = FontFamily.Monospace) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color(0xFF222222),
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            leadingIcon = { Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = Color(0xFF888888), modifier = Modifier.size(16.dp)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Presets grid list
        val presets = listOf("#ffffff", "#000000", "#3b82f6", "#4facfe", "#00f2fe", "#4ade80", "#ef4444", "#facc15", "#764ba2", "#121212")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            presets.forEach { colorStr ->
                val col = try { Color(android.graphics.Color.parseColor(colorStr)) } catch(e: Exception) { Color.White }
                val isSelected = currentColor.lowercase() == colorStr.lowercase()

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(col)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color.White else Color(0xFF444444),
                            shape = CircleShape
                        )
                        .clickable { onColorSelect(colorStr) }
                )
            }
        }
    }
}

@Composable
fun TextInputControl(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label.uppercase(),
            color = Color(0xFF888888),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontFamily = FontFamily.Monospace) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color(0xFF222222),
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "PRIVACY POLICY",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last Updated: May 13, 2026",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                HorizontalDivider(color = Color(0xFF222222), modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "CSS Toolkit is built with a strict user privacy policy. All generation data, slider parameters, and text changes are stored locally on your device inside an offline SQLite database via Android's Room persistence engine. No user analytical metrics, data logs, or tracking keys are ever transmitted over external networks. The Internet permission is solely declared to download the user-configured Unsplash images for image slider previews.",
                    color = Color(0xFFCCCCCC),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(text = "CLOSE", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
