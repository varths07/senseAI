package com.example.senseai.data.repository

import android.content.Context
import com.example.senseai.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "senseai_settings"

        private const val KEY_ASSISTANCE_ENABLED = "assistance_enabled"
        private const val KEY_VOICE_ENABLED = "voice_enabled"
        private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        private const val KEY_SHOW_DETECTION_BOXES = "show_detection_boxes"
        private const val KEY_CONFIDENCE_THRESHOLD = "confidence_threshold"
        private const val KEY_SPEECH_RATE = "speech_rate"
        private const val KEY_REPEAT_ALERT_DELAY = "repeat_alert_delay"
        private const val KEY_VOICE_COMMANDS_ENABLED = "voice_commands_enabled"
        private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
        private const val KEY_ALERT_SENSITIVITY = "alert_sensitivity"
    }

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val _settings = MutableStateFlow(loadSettings())

    val settings: StateFlow<AppSettings> =
        _settings.asStateFlow()

    fun updateSettings(newSettings: AppSettings) {

        val normalizedSettings = newSettings.copy(
            confidenceThreshold =
                newSettings.validConfidenceThreshold,

            speechRate =
                newSettings.validSpeechRate,

            repeatAlertDelayMs =
                newSettings.validRepeatAlertDelayMs,

            hapticIntensity =
                newSettings.validHapticIntensity
        )

        preferences.edit()
            .putBoolean(
                KEY_ASSISTANCE_ENABLED,
                normalizedSettings.assistanceEnabled
            )
            .putBoolean(
                KEY_VOICE_ENABLED,
                normalizedSettings.voiceEnabled
            )
            .putBoolean(
                KEY_HAPTIC_ENABLED,
                normalizedSettings.hapticEnabled
            )
            .putBoolean(
                KEY_SHOW_DETECTION_BOXES,
                normalizedSettings.showDetectionBoxes
            )
            .putFloat(
                KEY_CONFIDENCE_THRESHOLD,
                normalizedSettings.confidenceThreshold
            )
            .putFloat(
                KEY_SPEECH_RATE,
                normalizedSettings.speechRate
            )
            .putLong(
                KEY_REPEAT_ALERT_DELAY,
                normalizedSettings.repeatAlertDelayMs
            )
            .putBoolean(
                KEY_VOICE_COMMANDS_ENABLED,
                normalizedSettings.voiceCommandsEnabled
            )
            .putFloat(
                KEY_HAPTIC_INTENSITY,
                normalizedSettings.hapticIntensity
            )
            .putString(
                KEY_ALERT_SENSITIVITY,
                normalizedSettings.alertSensitivity
            )
            .apply()

        _settings.value = normalizedSettings
    }

    private fun loadSettings(): AppSettings {

        return AppSettings(
            assistanceEnabled =
                preferences.getBoolean(
                    KEY_ASSISTANCE_ENABLED,
                    true
                ),

            voiceEnabled =
                preferences.getBoolean(
                    KEY_VOICE_ENABLED,
                    true
                ),

            hapticEnabled =
                preferences.getBoolean(
                    KEY_HAPTIC_ENABLED,
                    true
                ),

            showDetectionBoxes =
                preferences.getBoolean(
                    KEY_SHOW_DETECTION_BOXES,
                    true
                ),

            confidenceThreshold =
                preferences.getFloat(
                    KEY_CONFIDENCE_THRESHOLD,
                    0.50f
                ),

            speechRate =
                preferences.getFloat(
                    KEY_SPEECH_RATE,
                    1.0f
                ),

            repeatAlertDelayMs =
                preferences.getLong(
                    KEY_REPEAT_ALERT_DELAY,
                    3000L
                ),

            voiceCommandsEnabled =
                preferences.getBoolean(
                    KEY_VOICE_COMMANDS_ENABLED,
                    true
                ),

            hapticIntensity =
                preferences.getFloat(
                    KEY_HAPTIC_INTENSITY,
                    1.0f
                ),

            alertSensitivity =
                preferences.getString(
                    KEY_ALERT_SENSITIVITY,
                    "MEDIUM"
                ) ?: "MEDIUM"
        )
    }
}