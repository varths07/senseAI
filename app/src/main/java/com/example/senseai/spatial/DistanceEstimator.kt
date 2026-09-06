package com.example.senseai.spatial

import android.graphics.RectF
import kotlin.math.roundToInt

class DistanceEstimator {

    /**
     * Estimated average physical heights of typical objects in meters.
     */
    private val averageHeightsMeters = mapOf(
        "person" to 1.70f,
        "car" to 1.50f,
        "vehicle" to 1.50f,
        "bus" to 3.00f,
        "truck" to 2.80f,
        "bicycle" to 1.00f,
        "chair" to 0.90f,
        "table" to 0.75f,
        "door" to 2.00f,
        "bottle" to 0.25f,
        "cup" to 0.15f
    )

    fun estimateDistance(
        className: String,
        boundingBox: RectF,
        imageHeight: Float
    ): Pair<Float?, String> {
        if (imageHeight <= 0f || boundingBox.height() <= 0f) {
            return Pair(null, "distance unknown")
        }

        val knownRealHeight = averageHeightsMeters[className.lowercase()] ?: 1.0f
        // Assumed focal length factor for typical phone cameras (approx 600px for 480p/720p height)
        val focalLengthPx = imageHeight * 1.15f

        val pixelHeight = boundingBox.height()
        val estimatedDistanceMeters = (knownRealHeight * focalLengthPx) / pixelHeight

        val roundedDistance = (estimatedDistanceMeters * 10).roundToInt() / 10.0f

        val formatted = when {
            roundedDistance < 1.0f -> "less than a meter away"
            roundedDistance.roundToInt() == 1 -> "about one meter away"
            else -> "about ${roundedDistance.roundToInt()} meters away"
        }

        return Pair(roundedDistance, formatted)
    }
}
