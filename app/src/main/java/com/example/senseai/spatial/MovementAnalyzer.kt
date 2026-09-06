package com.example.senseai.spatial

import com.example.senseai.ai.TrackedHistory
import com.example.senseai.data.model.MovementResult
import com.example.senseai.data.model.MovementState
import com.example.senseai.utils.Constants

class MovementAnalyzer {

    fun analyzeMovement(
        trackHistory: TrackedHistory?,
        currentDistance: Float?
    ): MovementResult {
        val trackingId = trackHistory?.trackingId ?: -1
        if (trackHistory == null || currentDistance == null || trackHistory.historyBoxes.size < 3) {
            return MovementResult(
                trackingId = trackingId,
                movementState = MovementState.UNKNOWN,
                deltaDistance = 0f,
                velocityMetersPerSec = 0f,
                historyCount = trackHistory?.historyBoxes?.size ?: 0
            )
        }

        // Compare historical bounding box sizes & heights over time
        val firstBox = trackHistory.historyBoxes.first().second
        val lastBox = trackHistory.historyBoxes.last().second

        val initialHeight = firstBox.height()
        val currentHeight = lastBox.height()

        val heightRatioChange = (currentHeight - initialHeight) / initialHeight

        val movementState = when {
            heightRatioChange > 0.15f -> MovementState.APPROACHING
            heightRatioChange < -0.15f -> MovementState.MOVING_AWAY
            else -> MovementState.STATIONARY
        }

        return MovementResult(
            trackingId = trackingId,
            movementState = movementState,
            deltaDistance = heightRatioChange,
            velocityMetersPerSec = 0f,
            historyCount = trackHistory.historyBoxes.size
        )
    }
}
