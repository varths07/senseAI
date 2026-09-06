package com.example.senseai.spatial

import android.graphics.RectF
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
        val direction = directionAnalyzer.calculateDirection(
            boundingBox = detection.boundingBox,
            imageWidth = imageWidth
        )

        val (distanceMeters, distanceFormatted) = distanceEstimator.estimateDistance(
            className = detection.className,
            boundingBox = detection.boundingBox,
            imageHeight = imageHeight
        )

        val movementResult = movementAnalyzer.analyzeMovement(
            trackHistory = trackHistory,
            currentDistance = distanceMeters
        )

        return SpatialResult(
            direction = direction,
            estimatedDistanceMeters = distanceMeters,
            distanceFormatted = distanceFormatted,
            movementState = movementResult.movementState
        )
    }
}
