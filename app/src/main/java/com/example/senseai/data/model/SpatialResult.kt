package com.example.senseai.data.model

enum class SpatialDirection(val label: String) {
    FAR_LEFT("far left"),
    LEFT("left"),
    AHEAD_LEFT("ahead-left"),
    SLIGHTLY_LEFT("slightly left"),
    CENTER("ahead"),
    SLIGHTLY_RIGHT("slightly right"),
    AHEAD_RIGHT("ahead-right"),
    RIGHT("right"),
    FAR_RIGHT("far right")
}

enum class MovementState(val label: String) {
    STATIONARY("stationary"),
    APPROACHING("moving closer"),
    MOVING_AWAY("moving away"),
    MOVING_LATERALLY("moving across"),
    UNKNOWN("unknown")
}

data class SpatialResult(
    val direction: SpatialDirection,
    val estimatedDistanceMeters: Float?,
    val distanceFormatted: String,
    val movementState: MovementState = MovementState.UNKNOWN
) {

    /**
     * Returns true when the object is somewhere
     * in the user's forward walking path.
     */
    val isAhead: Boolean
        get() = when (direction) {
            SpatialDirection.CENTER,
            SpatialDirection.SLIGHTLY_LEFT,
            SpatialDirection.SLIGHTLY_RIGHT,
            SpatialDirection.AHEAD_LEFT,
            SpatialDirection.AHEAD_RIGHT -> true

            else -> false
        }

    /**
     * Returns true when the object is moving toward
     * the camera.
     */
    val isApproaching: Boolean
        get() = movementState == MovementState.APPROACHING

    /**
     * Returns true when a usable numeric distance
     * estimate is available.
     */
    val hasDistance: Boolean
        get() = estimatedDistanceMeters != null &&
                estimatedDistanceMeters > 0f
}