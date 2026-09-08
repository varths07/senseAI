package com.example.senseai.trust

import com.example.senseai.data.model.DetectionResult
import com.example.senseai.data.model.MovementState
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.data.model.SpatialDirection
import com.example.senseai.data.model.SpatialResult
import com.example.senseai.data.model.TrustLevel
import com.example.senseai.utils.Constants

/**
 * Evaluates the physical safety risk of a detected object.
 *
 * IMPORTANT:
 * Trust and risk are different concepts.
 *
 * Trust:
 *     "How confident are we that this detection is correct?"
 *
 * Risk:
 *     "If this object is real, how dangerous is its position
 *      and movement for the user?"
 *
 * A low-trust detection can still produce a high-risk warning.
 * This is intentional because safety systems should not silently
 * ignore a potentially dangerous object just because the detector
 * is uncertain.
 */
class RiskAnalyzer {

    /**
     * Calculates the risk level of a detected object.
     *
     * The calculation considers:
     * - Object class
     * - Direction
     * - Approximate distance when available
     * - Movement
     * - Detector trust
     *
     * No fake distance is assumed when the distance estimator
     * cannot provide a value.
     */
    fun calculateRisk(
        detection: DetectionResult,
        spatial: SpatialResult,
        trustLevel: TrustLevel
    ): RiskLevel {

        val className = detection.className
            .trim()
            .lowercase()

        val isCriticalObject =
            Constants.CRITICAL_OBJECT_CLASSES.contains(className)

        val isAhead = isInForwardPath(spatial.direction)

        val isApproaching =
            spatial.movementState == MovementState.APPROACHING

        val isMovingLaterally =
            spatial.movementState == MovementState.MOVING_LATERALLY

        val distance = spatial.estimatedDistanceMeters

        /*
         * We only classify distance when the estimator actually
         * produced a value.
         *
         * We DO NOT replace null with an arbitrary value such as
         * 5 meters because that could hide a real danger.
         */
        val isVeryClose = distance != null && distance <= 1.5f

        val isClose = distance != null && distance <= 3.5f

        /*
         * HIGH RISK
         *
         * These are situations where the object could require
         * immediate attention from the user.
         */

        // Critical object approaching in front of the user.
        if (isCriticalObject && isApproaching && isAhead) {
            return RiskLevel.HIGH
        }

        // Any object extremely close and directly in the path.
        if (isVeryClose && isAhead) {
            return RiskLevel.HIGH
        }

        /*
         * If distance is unavailable, an approaching critical
         * object is still dangerous.
         */
        if (isCriticalObject && isApproaching) {
            return RiskLevel.HIGH
        }

        /*
         * MEDIUM RISK
         */

        // Critical object close to the user.
        if (isCriticalObject && isClose) {
            return RiskLevel.MEDIUM
        }

        // Critical object in the forward path.
        if (isCriticalObject && isAhead) {
            return RiskLevel.MEDIUM
        }

        // Any object approaching from the side may become relevant.
        if (isApproaching) {
            return RiskLevel.MEDIUM
        }

        // Object moving laterally across the user's path.
        if (isMovingLaterally && isAhead) {
            return RiskLevel.MEDIUM
        }

        // A close object in front of the user.
        if (isClose && isAhead) {
            return RiskLevel.MEDIUM
        }

        /*
         * LOW RISK
         *
         * The object is detected, but there is currently
         * no strong indication of immediate danger.
         */
        return RiskLevel.LOW
    }

    /**
     * Determines whether an object is inside the user's
     * approximate forward field.
     */
    private fun isInForwardPath(
        direction: SpatialDirection
    ): Boolean {

        return when (direction) {
            SpatialDirection.CENTER,
            SpatialDirection.SLIGHTLY_LEFT,
            SpatialDirection.SLIGHTLY_RIGHT,
            SpatialDirection.AHEAD_LEFT,
            SpatialDirection.AHEAD_RIGHT -> true

            else -> false
        }
    }
}