package com.example.senseai.spatial

import com.example.senseai.ai.TrackedHistory
import com.example.senseai.data.model.DetectionResult
import com.example.senseai.data.model.SpatialResult

class SpatialAnalyzer {

    private val directionAnalyzer = DirectionAnalyzer()
    private val distanceEstimator = DistanceEstimator()
    private val movementAnalyzer = MovementAnalyzer()

    fun analyzeSpatial(
        detection: DetectionResult,
        imageWidth: Float,
        imageHeight: Float,
        trackHistory: TrackedHistory?
    ): SpatialResult {

        /*
         * Determine where the object is located
         * horizontally in the camera frame.
         */
        val direction = directionAnalyzer.calculateDirection(
            boundingBox = detection.boundingBox,
            imageWidth = imageWidth
        )

        /*
         * Estimate approximate distance.
         */
        val distanceResult =
            distanceEstimator.estimateDistance(
                className = detection.className,
                boundingBox = detection.boundingBox,
                imageHeight = imageHeight
            )

        val distanceMeters = distanceResult.first
        val distanceFormatted = distanceResult.second

        /*
         * Analyze movement using the object's
         * previous tracking history.
         */
        val movementResult =
            movementAnalyzer.analyzeMovement(
                trackHistory = trackHistory,
                currentDistance = distanceMeters
            )

        /*
         * Combine all spatial information into one result.
         */
        return SpatialResult(
            direction = direction,
            estimatedDistanceMeters = distanceMeters,
            distanceFormatted = distanceFormatted,
            movementState = movementResult.movementState
        )
    }
}