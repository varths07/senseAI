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

        /*
         * Temporal verification:
         *
         * More observations of the same object
         * + higher confidence
         * = higher trust.
         */

        val consistencyScore = when {
            historyCount >= 5 -> 1.0f
            historyCount >= 3 -> 0.85f
            historyCount >= 2 -> 0.70f
            else -> 0.45f
        }

        val trustScore =
            (confidence * 0.6f) +
                    (consistencyScore * 0.4f)

        val finalScore =
            trustScore.coerceIn(0f, 1f)

        val trustLevel = when {
            finalScore >= 0.75f -> TrustLevel.HIGH
            finalScore >= 0.50f -> TrustLevel.MEDIUM
            else -> TrustLevel.LOW
        }

        return TrustResult(
            trustScore = finalScore,
            trustLevel = trustLevel,
            riskLevel = RiskLevel.LOW,
            verificationCount = detectionCount,
            isVerified = trustLevel != TrustLevel.LOW
        )
    }
}