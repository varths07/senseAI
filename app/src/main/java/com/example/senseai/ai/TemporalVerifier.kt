package com.example.senseai.data.model

/**
 * Describes the observed movement of a tracked object.
 *
 * IMPORTANT:
 *
 * deltaDistance is a relative change in the object's
 * visual size in the camera frame. It is NOT guaranteed
 * to be a measurement in meters.
 *
 * velocityMetersPerSec is only populated when a reliable
 * metric distance calculation is available. Otherwise it
 * remains 0.0.
 */
data class MovementResult(

    /**
     * ID assigned to the tracked object.
     *
     * -1 means that no valid tracking ID is available.
     */
    val trackingId: Int,

    /**
     * Estimated movement direction.
     */
    val movementState: MovementState,

    /**
     * Relative change in visual object size.
     *
     * Positive value:
     *     object appears larger / may be approaching
     *
     * Negative value:
     *     object appears smaller / may be moving away
     *
     * Example:
     *     0.20 = approximately 20% increase in visual height
     *
     * This is NOT meters.
     */
    val deltaDistance: Float,

    /**
     * Estimated velocity in meters per second.
     *
     * This value is only meaningful when metric distance
     * information is available.
     *
     * 0.0 means velocity could not be reliably calculated.
     */
    val velocityMetersPerSec: Float,

    /**
     * Number of historical observations available for
     * this tracked object.
     */
    val historyCount: Int,

    /**
     * Confidence that the movement classification is reliable.
     *
     * Range:
     *     0.0 -> unreliable
     *     1.0 -> highly reliable
     *
     * Default is 0.0 because the existing MovementAnalyzer
     * does not calculate movement confidence yet.
     */
    val movementConfidence: Float = 0.0f
) {

    /**
     * Returns true when enough temporal information exists
     * to make a movement decision.
     */
    val hasSufficientHistory: Boolean
        get() = historyCount >= 3

    /**
     * Returns true when the movement result has meaningful
     * confidence.
     */
    val isReliable: Boolean
        get() = movementConfidence >= 0.60f &&
                movementState != MovementState.UNKNOWN
}