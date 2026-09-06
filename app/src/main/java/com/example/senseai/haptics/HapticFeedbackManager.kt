package com.example.senseai.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.utils.Logger

class HapticFeedbackManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun triggerHapticForRisk(
        riskLevel: RiskLevel,
        isEnabled: Boolean = true
    ) {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (riskLevel) {
                    RiskLevel.HIGH -> {
                        // Urgent double heavy pulse pattern: [0ms delay, 150ms vibration, 100ms pause, 200ms vibration]
                        val timings = longArrayOf(0, 150, 100, 200)
                        val amplitudes = intArrayOf(0, 255, 0, 255)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    RiskLevel.MEDIUM -> {
                        // Single medium pulse: 120ms at 180 intensity
                        vibrator.vibrate(VibrationEffect.createOneShot(120, 180))
                    }
                    RiskLevel.LOW -> {
                        // Subtle light pulse: 50ms at 100 intensity
                        vibrator.vibrate(VibrationEffect.createOneShot(50, 100))
                    }
                    RiskLevel.SAFE -> {
                        // No vibration
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (riskLevel) {
                    RiskLevel.HIGH -> vibrator.vibrate(longArrayOf(0, 150, 100, 200), -1)
                    RiskLevel.MEDIUM -> vibrator.vibrate(120)
                    RiskLevel.LOW -> vibrator.vibrate(50)
                    RiskLevel.SAFE -> {}
                }
            }
        } catch (e: Exception) {
            Logger.e("Error triggering haptic vibration", e)
        }
    }
}
