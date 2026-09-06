package com.example.senseai.data.model

data class AppSettings(
    val speechRate: Float = 1.0f,
    val isHapticsEnabled: Boolean = true,
    val hapticIntensity: Float = 1.0f,
    val alertSensitivity: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val detectionThreshold: Float = 0.50f,
    val showDebugOverlay: Boolean = true,
    val voiceCommandsEnabled: Boolean = true
)
