package com.btech_dev.quotebro.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val fontSize: Float = 1.0f // 1.0f = normal, 1.2f = medium, 1.4f = large
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // Default to false or maybe system default?
        // For now false (Light) as per current hardcoded implementation.
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val fontSize = prefs.getFloat("font_size", 1.0f)
        _uiState.update { it.copy(isDarkMode = isDarkMode, fontSize = fontSize) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            prefs.edit().putBoolean("dark_mode", enabled).apply()
            _uiState.update { it.copy(isDarkMode = enabled) }
        }
    }

    fun updateFontSize(fontSize: Float) {
        viewModelScope.launch {
            prefs.edit().putFloat("font_size", fontSize).apply()
            _uiState.update { it.copy(fontSize = fontSize) }
        }
    }
}
