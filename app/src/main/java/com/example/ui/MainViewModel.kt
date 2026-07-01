package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(
        AppDatabase.getDatabase(application).settingDao()
    )

    val dbSettings: StateFlow<Map<String, String>> = repository.allSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _activeTool = MutableStateFlow("glass")
    val activeTool: StateFlow<String> = _activeTool

    private val _activeTab = MutableStateFlow("css")
    val activeTab: StateFlow<String> = _activeTab

    // Preset / Default configurations map
    val defaults = mapOf(
        "glass_blur" to "16",
        "glass_opacity" to "15",
        "glass_saturation" to "180",
        "glass_radius" to "16",
        "glass_color" to "#ffffff",
        "glass_dark_text" to "false",
        "glass_bg_url" to "",
        
        "slider_urls" to "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800,https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800",
        "slider_speed" to "3",
        "slider_radius" to "8",
        "slider_show_arrows" to "true",
        
        "header_bg_color" to "#000000",
        "header_blur" to "12",
        "header_height" to "64",
        "header_shadow" to "10",
        "header_border_bottom" to "true",
        "header_brand_text" to "BRAND",
        "header_nav_links" to "Home, About, Contact",
        
        "shadow_offset_x" to "0",
        "shadow_offset_y" to "10",
        "shadow_blur" to "30",
        "shadow_spread" to "-5",
        "shadow_color" to "#000000",
        "shadow_opacity" to "15",
        "shadow_inset" to "false",
        "shadow_radius" to "8",
        
        "gradient_type" to "linear-gradient",
        "gradient_angle" to "135",
        "gradient_color1" to "#4facfe",
        "gradient_stop1" to "0",
        "gradient_color2" to "#00f2fe",
        "gradient_stop2" to "100",
        "gradient_use_color3" to "false",
        "gradient_color3" to "#000000",
        "gradient_stop3" to "100",
        
        "button_label" to "Get Started",
        "button_style" to "solid",
        "button_bg_color" to "#3b82f6",
        "button_text_color" to "#ffffff",
        "button_radius" to "6",
        "button_font_size" to "14",
        "button_padding_h" to "24",
        "button_padding_v" to "12",
        "button_hover_fx" to "scale",
        
        "ticker_items" to "— New Feature Launched\n— Fast & Responsive\n— Cross Platform UI\n— Instant CSS Export",
        "ticker_speed" to "10",
        "ticker_direction" to "left",
        "ticker_bg_color" to "#3b82f6",
        "ticker_text_color" to "#ffffff",
        "ticker_font_size" to "14",
        "ticker_gap" to "40",
        
        "loader_style" to "spinner",
        "loader_color" to "#3b82f6",
        "loader_size" to "40",
        "loader_speed" to "10",
        "loader_stroke" to "4"
    )

    // Combined settings exposing active values
    val currentSettings: StateFlow<Map<String, String>> = combine(dbSettings, _activeTool) { db, _ ->
        defaults + db
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = defaults
    )

    private var initialized = false

    init {
        // Load active tool and tab from DB if saved
        viewModelScope.launch {
            dbSettings.collect { db ->
                if (!initialized && db.isNotEmpty()) {
                    db["active_tool"]?.let { _activeTool.value = it }
                    db["active_tab"]?.let { _activeTab.value = it }
                    initialized = true
                }
            }
        }
    }

    fun selectTool(tool: String) {
        _activeTool.value = tool
        viewModelScope.launch {
            repository.saveSetting("active_tool", tool)
        }
    }

    fun selectTab(tab: String) {
        _activeTab.value = tab
        viewModelScope.launch {
            repository.saveSetting("active_tab", tab)
        }
    }

    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.saveSetting(key, value)
        }
    }

    // Helper functions to get typed settings from the current map
    fun getStr(key: String, settings: Map<String, String>): String = settings[key] ?: defaults[key] ?: ""
    fun getFloat(key: String, settings: Map<String, String>): Float = (settings[key] ?: defaults[key] ?: "0").toFloatOrNull() ?: 0f
    fun getBool(key: String, settings: Map<String, String>): Boolean = (settings[key] ?: defaults[key] ?: "false").toBoolean()
}
