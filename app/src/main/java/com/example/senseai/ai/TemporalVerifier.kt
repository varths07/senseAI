package com.example.senseai.ai

import com.example.senseai.data.model.DetectionResult
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.data.model.TrustLevel
import com.example.senseai.data.model.TrustResult

class TemporalVerifier {

    fun verifyDetection(
        detection: DetectionResult,
        trackedHistory: TrackedHistory?
    ): TrustResult {

        val confidence = detection.confidence.coerceIn(0f, 1f)

        val historyCount =
            trackedHistory?.historyBoxes?.size ?: 0

        val detectionCount =
            trackedHistory?.detectionCount ?: 1

        val consistencyScore = when {
            historyCount >= 5 -> 1.0f
            historyCount >= 3 -> 0.85f
            historyCount >= 2 -> 0.70f
            else -> 0.45f
        }

        val trustScore = (
                confidence * 0.60f +
                        consistencyScore * 0.40f
                ).coerceIn(0f, 1f)

        val trustLevel = when {
            trustScore >= 0.75f -> TrustLevel.HIGH
            trustScore >= 0.50f -> TrustLevel.MEDIUM
            else -> TrustLevel.LOW
        }

        val riskLevel = when {
            confidence < 0.35f -> RiskLevel.HIGH
            confidence < 0.55f -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        return TrustResult(
            trustScore = trustScore,
            trustLevel = trustLevel,
            riskLevel = riskLevel,
            verificationCount = detectionCount,
            isVerified = trustLevel != TrustLevel.LOW
        )
    }
}