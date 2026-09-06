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
)
