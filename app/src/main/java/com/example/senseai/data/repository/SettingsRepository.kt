package com.example.senseai.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.senseai.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            speechRate = prefs.getFloat(KEY_SPEECH_RATE, 1.0f),
            isHapticsEnabled = prefs.getBoolean(KEY_HAPTICS_ENABLED, true),
            hapticIntensity = prefs.getFloat(KEY_HAPTIC_INTENSITY, 1.0f),
            alertSensitivity = prefs.getString(KEY_ALERT_SENSITIVITY, "MEDIUM") ?: "MEDIUM",
            detectionThreshold = prefs.getFloat(KEY_DETECTION_THRESHOLD, 0.50f),
            showDebugOverlay = prefs.getBoolean(KEY_SHOW_DEBUG_OVERLAY, true),
            voiceCommandsEnabled = prefs.getBoolean(KEY_VOICE_COMMANDS, true)
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit().apply {
            putFloat(KEY_SPEECH_RATE, newSettings.speechRate)
            putBoolean(KEY_HAPTICS_ENABLED, newSettings.isHapticsEnabled)
            putFloat(KEY_HAPTIC_INTENSITY, newSettings.hapticIntensity)
            putString(KEY_ALERT_SENSITIVITY, newSettings.alertSensitivity)
            putFloat(KEY_DETECTION_THRESHOLD, newSettings.detectionThreshold)
            putBoolean(KEY_SHOW_DEBUG_OVERLAY, newSettings.showDebugOverlay)
            putBoolean(KEY_VOICE_COMMANDS, newSettings.voiceCommandsEnabled)
            apply()
        }
        _settings.value = newSettings
    }

    companion object {
        private const val PREFS_NAME = "senseai_settings"
        private const val KEY_SPEECH_RATE = "speech_rate"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
        private const val KEY_ALERT_SENSITIVITY = "alert_sensitivity"
        private const val KEY_DETECTION_THRESHOLD = "detection_threshold"
        private const val KEY_SHOW_DEBUG_OVERLAY = "show_debug_overlay"
        private const val KEY_VOICE_COMMANDS = "voice_commands"
    }
}
