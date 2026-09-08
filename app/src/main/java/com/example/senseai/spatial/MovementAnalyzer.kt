package com.example.senseai.spatial

import com.example.senseai.ai.TrackedHistory
import com.example.senseai.data.model.MovementResult
import com.example.senseai.data.model.MovementState
import kotlin.math.abs
import kotlin.math.max

/**
 * Analyzes the movement of a tracked object.
 *
 * Movement is estimated from changes in the object's
 * bounding box across multiple frames.
 *
 * This does not provide true physical velocity because
 * a normal phone camera does not directly provide reliable
 * depth information.
 */
class MovementAnalyzer {

    companion object {
        private const val MIN_HISTORY_SIZE = 3

        private const val APPROACHING_THRESHOLD = 0.15f
        private const val MOVING_AWAY_THRESHOLD = -0.15f

        private const val LATERAL_MOVEMENT_THRESHOLD = 0.20f
    }

    fun analyzeMovement(
        trackHistory: TrackedHistory?,
        currentDistance: Float?
    ): MovementResult {

        val trackingId = trackHistory?.trackingId ?: -1

        val historyCount =
            trackHistory?.historyBoxes?.size ?: 0

        /*
         * We need at least three observations before
         * attempting to classify movement.
         */
        if (
            trackHistory == null ||
            historyCount < MIN_HISTORY_SIZE
        ) {
            return MovementResult(
                trackingId = trackingId,
                movementState = MovementState.UNKNOWN,
                deltaDistance = 0f,
                velocityMetersPerSec = 0f,
                historyCount = historyCount
            )
        }

        val boxes = trackHistory.historyBoxes

        val firstEntry = boxes.first()
        val lastEntry = boxes.last()

        val firstBox = firstEntry.second
        val lastBox = lastEntry.second

        val firstHeight = firstBox.height()
        val lastHeight = lastBox.height()

        /*
         * Invalid bounding boxes cannot be used.
         */
        if (
            firstHeight <= 0f ||
            lastHeight <= 0f
        ) {
            return MovementResult(
                trackingId = trackingId,
                movementState = MovementState.UNKNOWN,
                deltaDistance = 0f,
                velocityMetersPerSec = 0f,
                historyCount = historyCount
            )
        }

        /*
         * Compare the object's visual height.
         *
         * Example:
         *
         * 100 px -> 130 px
         *
         * Change = 0.30
         *
         * This means the object appears approximately
         * 30% larger in the camera view.
         */
        val heightChange =
            (lastHeight - firstHeight) / firstHeight

        /*
         * Compare horizontal movement.
         *
         * This helps identify an object moving across
         * the camera view rather than directly approaching.
         */
        val horizontalMovement =
            abs(lastBox.centerX() - firstBox.centerX())

        val referenceWidth =
            max(
                (firstBox.width() + lastBox.width()) / 2f,
                1f
            )

        val normalizedHorizontalMovement =
            horizontalMovement / referenceWidth

        /*
         * Determine movement state.
         *
         * Size increasing:
         *     likely approaching
         *
         * Size decreasing:
         *     likely moving away
         *
         * Size relatively stable but strong horizontal
         * movement:
         *     moving laterally
         *
         * Otherwise:
         *     stationary
         */
        val movementState = when {

            heightChange >= APPROACHING_THRESHOLD -> {
                MovementState.APPROACHING
            }

            heightChange <= MOVING_AWAY_THRESHOLD -> {
                MovementState.MOVING_AWAY
            }

            normalizedHorizontalMovement >= LATERAL_MOVEMENT_THRESHOLD -> {
                MovementState.MOVING_LATERALLY
            }

            else -> {
                MovementState.STATIONARY
            }
        }

        /*
         * We currently cannot calculate a trustworthy
         * meters-per-second velocity.
         *
         * currentDistance is only the current estimated
         * distance. We don't have the previous metric
         * distance stored in TrackedHistory yet.
         *
         * Therefore returning 0 is safer than inventing
         * a velocity.
         */
        val velocityMetersPerSecond = 0f

        /*
         * Keep the parameter available for compatibility
         * with SpatialAnalyzer.
         */
        @Suppress("UNUSED_VARIABLE")
        val distance = currentDistance

        return MovementResult(
            trackingId = trackingId,
            movementState = movementState,
            deltaDistance = heightChange,
            velocityMetersPerSec = velocityMetersPerSecond,
            historyCount = historyCount
        )
    }
}