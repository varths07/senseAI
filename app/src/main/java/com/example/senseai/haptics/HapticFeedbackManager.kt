package com.example.senseai.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.utils.Logger

class HapticFeedbackManager(
    context: Context
) {

    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager =
                context.applicationContext
                    .getSystemService(VibratorManager::class.java)

            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.applicationContext
                .getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    /**
     * Basic vibration.
     */
    fun triggerHaptic(
        duration: Long = 100L,
        intensity: Float = 1.0f
    ) {
        val safeIntensity =
            intensity.coerceIn(0.2f, 1.0f)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val amplitude =
                    (255f * safeIntensity)
                        .toInt()
                        .coerceIn(1, 255)

                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        amplitude
                    )
                )

            } else {

                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }

        } catch (e: Exception) {

            Logger.e(
                "Failed to trigger haptic feedback",
                e
            )
        }
    }

    /**
     * Risk-based vibration.
     */
    fun triggerHapticForRisk(
        riskLevel: RiskLevel,
        isEnabled: Boolean = true,
        intensity: Float = 1.0f
    ) {
        if (!isEnabled) return

        when (riskLevel) {

            RiskLevel.HIGH -> {
                triggerHaptic(
                    duration = 220L,
                    intensity = intensity
                )
            }

            RiskLevel.MEDIUM -> {
                triggerHaptic(
                    duration = 140L,
                    intensity = intensity
                )
            }

            RiskLevel.LOW -> {
                triggerHaptic(
                    duration = 80L,
                    intensity = intensity
                )
            }

            RiskLevel.SAFE -> {
                // No vibration.
            }
        }
    }

    /**
     * Stop all vibration.
     */
    fun stop() {
        try {
            vibrator?.cancel()
        } catch (e: Exception) {

            Logger.e(
                "Failed to stop haptic feedback",
                e
            )
        }
    }
}