package com.example.senseai.data.model

data class MovementResult(
    val trackingId: Int,
    val movementState: MovementState,
    val deltaDistance: Float,
    val velocityMetersPerSec: Float,
    val historyCount: Int
)
