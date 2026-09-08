package com.example.senseai.trust

import com.example.senseai.data.model.DetectionResult
import com.example.senseai.data.model.MovementState
import com.example.senseai.data.model.SpatialResult
import com.example.senseai.data.model.TrustLevel
import com.example.senseai.data.model.TrustResult

class TrustEngine {

    private val riskAnalyzer = RiskAnalyzer()

    fun evaluate(
        detection: DetectionResult,
        spatial: SpatialResult,
        trackHistory: com.example.senseai.ai.TrackedHistory?
    ): TrustResult {

        val confidence =
            detection.confidence.coerceIn(0f, 1f)

        val historyCount =
            trackHistory?.historyBoxes?.size ?: 0

        /*
         * Trust is based on:
         * 1. Detection confidence
         * 2. Number of consistent observations
         */

        val consistencyScore = when {
            historyCount >= 5 -> 1.0f
            historyCount >= 3 -> 0.85f
            historyCount >= 2 -> 0.70f
            else -> 0.45f
        }

        val trustScore =
            ((confidence * 0.6f) +
                    (consistencyScore * 0.4f))
                .coerceIn(0f, 1f)

        val trustLevel = when {
            trustScore >= 0.75f -> TrustLevel.HIGH
            trustScore >= 0.50f -> TrustLevel.MEDIUM
            else -> TrustLevel.LOW
        }

        val riskLevel =
            riskAnalyzer.calculateRisk(
                detection = detection,
                spatial = spatial,
                trustLevel = trustLevel
            )

        return TrustResult(
            trustScore = trustScore,
            trustLevel = trustLevel,
            riskLevel = riskLevel,
            verificationCount = trackHistory?.detectionCount ?: 1,
            isVerified = trustLevel != TrustLevel.LOW
        )
    }

    fun buildAnnouncementText(
        detection: DetectionResult,
        spatial: SpatialResult,
        trust: TrustResult
    ): String {

        val name =
            detection.className
                .trim()
                .lowercase()
                .replaceFirstChar {
                    it.uppercase()
                }

        val direction =
            spatial.direction.label

        val distance =
            spatial.distanceFormatted

        val approaching =
            spatial.movementState ==
                    MovementState.APPROACHING

        return when (trust.trustLevel) {

            TrustLevel.HIGH -> {
                when {
                    approaching ->
                        "$name $direction, moving closer."

                    spatial.estimatedDistanceMeters != null ->
                        "$name $direction, $distance."

                    else ->
                        "$name $direction."
                }
            }

            TrustLevel.MEDIUM -> {
                when {
                    approaching ->
                        "Possible $name $direction, approaching."

                    else ->
                        "Possible $name $direction."
                }
            }

            TrustLevel.LOW ->
                "Uncertain $name $direction. Please verify."
        }
    }
}