package com.example.senseai.data.model

data class AppSettings(
    val assistanceEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showDetectionBoxes: Boolean = true,
    val confidenceThreshold: Float = 0.30f,
    val speechRate: Float = 1.0f,
    val repeatAlertDelayMs: Long = 3000L,

    // Settings used by the Settings screen
    val voiceCommandsEnabled: Boolean = true,
    val hapticIntensity: Float = 1.0f,
    val alertSensitivity: String = "MEDIUM"
) {

    val validConfidenceThreshold: Float
        get() = confidenceThreshold.coerceIn(0.0f, 1.0f)

    val validSpeechRate: Float
        get() = speechRate.coerceIn(0.5f, 2.0f)

    val validRepeatAlertDelayMs: Long
        get() = repeatAlertDelayMs.coerceIn(1000L, 15000L)

    val validHapticIntensity: Float
        get() = hapticIntensity.coerceIn(0.2f, 1.0f)

    // Compatibility properties used by existing screens
    val isHapticsEnabled: Boolean
        get() = hapticEnabled

    val showDebugOverlay: Boolean
        get() = showDetectionBoxes

    val detectionThreshold: Float
        get() = confidenceThreshold
}