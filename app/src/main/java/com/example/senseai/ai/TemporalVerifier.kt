package com.example.senseai.ai

import com.example.senseai.data.model.DetectionResult
import com.example.senseai.data.model.TrustLevel
import com.example.senseai.data.model.TrustResult

class TemporalVerifier {

    /**
     * Evaluates verification metrics over tracked history.
     */
    fun verifyDetection(
        detection: DetectionResult,
        trackedHistory: TrackedHistory?
    ): TrustResult {
        val count = trackedHistory?.detectionCount ?: 1
        val confidence = detection.confidence

        // Base trust calculations
        val persistenceScore = (count / 4.0f).coerceAtMost(1.0f)
        val stabilityScore = confidence.coerceIn(0.0f, 1.0f)
        val trustScore = (persistenceScore * 0.5f) + (stabilityScore * 0.5f)

        val trustLevel = when {
            trustScore >= 0.75f && count >= 3 -> TrustLevel.HIGH
            trustScore >= 0.50f || count >= 2 -> TrustLevel.MEDIUM
            else -> TrustLevel.LOW
        }

        val isVerified = trustLevel != TrustLevel.LOW

        return TrustResult(
            trustScore = trustScore,
            trustLevel = trustLevel,
            riskLevel = com.example.senseai.data.model.RiskLevel.LOW, // Calculated later in RiskEngine
            verificationCount = count,
            isVerified = isVerified
        )
    }
}
