package com.example.senseai.trust

import com.example.senseai.data.model.*
import com.example.senseai.utils.Constants

class RiskAnalyzer {

    fun calculateRisk(
        detection: DetectionResult,
        spatial: SpatialResult,
        trustLevel: TrustLevel
    ): RiskLevel {
        val className = detection.className.lowercase()
        val isCriticalClass = Constants.CRITICAL_OBJECT_CLASSES.contains(className)
        val distance = spatial.estimatedDistanceMeters ?: 5.0f
        val isClose = distance < 3.5f
        val isVeryClose = distance < 1.5f
        val isApproaching = spatial.movementState == MovementState.APPROACHING
        val isAhead = spatial.direction == SpatialDirection.CENTER ||
                spatial.direction == SpatialDirection.SLIGHTLY_LEFT ||
                spatial.direction == SpatialDirection.SLIGHTLY_RIGHT ||
                spatial.direction == SpatialDirection.AHEAD_LEFT ||
                spatial.direction == SpatialDirection.AHEAD_RIGHT

        return when {
            // High Risk Condition
            isCriticalClass && isApproaching && (isClose || isAhead) -> RiskLevel.HIGH
            isCriticalClass && isVeryClose && isAhead -> RiskLevel.HIGH
            isVeryClose && isAhead -> RiskLevel.HIGH

            // Medium Risk Condition
            isCriticalClass && isClose -> RiskLevel.MEDIUM
            isApproaching -> RiskLevel.MEDIUM
            isAhead && isClose -> RiskLevel.MEDIUM

            // Low Risk / Safe
            else -> RiskLevel.LOW
        }
    }
}
