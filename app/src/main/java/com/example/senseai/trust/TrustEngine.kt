package com.example.senseai.trust

import com.example.senseai.ai.TemporalVerifier
import com.example.senseai.ai.TrackedHistory
import com.example.senseai.data.model.*

class TrustEngine {
    private val temporalVerifier = TemporalVerifier()
    private val confidenceManager = ConfidenceManager()
    private val riskAnalyzer = RiskAnalyzer()

    fun evaluate(
        detection: DetectionResult,
        spatial: SpatialResult,
        trackHistory: TrackedHistory?
    ): TrustResult {
        val baseTrust = temporalVerifier.verifyDetection(detection, trackHistory)
        val riskLevel = riskAnalyzer.calculateRisk(detection, spatial, baseTrust.trustLevel)

        return baseTrust.copy(riskLevel = riskLevel)
    }

    /**
     * Formats speech output with cautious wording when trust score is low or medium.
     */
    fun buildAnnouncementText(
        detection: DetectionResult,
        spatial: SpatialResult,
        trust: TrustResult
    ): String {
        val name = detection.className.lowercase()
        val directionText = spatial.direction.label
        val distanceText = spatial.distanceFormatted
        val isApproaching = spatial.movementState == MovementState.APPROACHING

        return when {
            // High trust announcement
            trust.trustLevel == TrustLevel.HIGH -> {
                if (isApproaching) {
                    "${name.replaceFirstChar { it.uppercase() }} $directionText, moving closer."
                } else {
                    "${name.replaceFirstChar { it.uppercase() }} $directionText, $distanceText."
                }
            }

            // Medium trust announcement
            trust.trustLevel == TrustLevel.MEDIUM -> {
                if (isApproaching) {
                    "${name.replaceFirstChar { it.uppercase() }} $directionText, approaching."
                } else {
                    "${name.replaceFirstChar { it.uppercase() }} $directionText."
                }
            }

            // Low trust cautious announcement
            else -> {
                "Possible $name $directionText. Please verify."
            }
        }
    }
}
