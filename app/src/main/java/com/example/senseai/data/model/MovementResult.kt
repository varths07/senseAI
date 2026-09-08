package com.example.senseai.data.model

/**
 * Describes the observed movement of a tracked object.
 */
data class MovementResult(
    /**
     * ID assigned to the tracked object.
     * -1 means that no valid tracking ID is available.
     */
    val trackingId: Int,

    /**
     * Estimated movement state.
     */
    val movementState: MovementState,

    /**
     * Relative change in the object's visual size.
     */
    val deltaDistance: Float,

    /**
     * Estimated velocity in meters per second.
     * Zero means metric velocity is unavailable.
     */
    val velocityMetersPerSec: Float,

    /**
     * Number of historical observations used
     * for movement analysis.
     */
    val historyCount: Int,

    /**
     * Confidence of the movement classification.
     * Range: 0.0 to 1.0.
     */
    val movementConfidence: Float = 0.0f
)