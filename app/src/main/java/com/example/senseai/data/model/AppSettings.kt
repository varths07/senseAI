package com.example.senseai.data.model

data class AppSettings(
    val assistanceEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showDetectionBoxes: Boolean = true,
    val confidenceThreshold: Float = 0.50f,
    val speechRate: Float = 1.0f,
    val repeatAlertDelayMs: Long = 4000L
) {

    val validConfidenceThreshold: Float
        get() = confidenceThreshold.coerceIn(0.0f, 1.0f)

    val validSpeechRate: Float
        get() = speechRate.coerceIn(0.5f, 2.0f)

    val validRepeatAlertDelayMs: Long
        get() = repeatAlertDelayMs.coerceIn(1000L, 15000L)
}