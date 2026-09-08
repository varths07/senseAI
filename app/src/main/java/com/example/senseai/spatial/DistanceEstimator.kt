package com.example.senseai.spatial

import android.graphics.RectF
import kotlin.math.roundToInt

class DistanceEstimator {

    private val averageHeightsMeters = mapOf(
        "person" to 1.70f,
        "car" to 1.50f,
        "vehicle" to 1.50f,
        "bus" to 3.00f,
        "truck" to 2.80f,
        "bicycle" to 1.00f,
        "motorcycle" to 1.20f,
        "motorbike" to 1.20f,
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

        if (
            imageHeight <= 0f ||
            boundingBox.height() <= 0f
        ) {
            return Pair(null, "distance unknown")
        }

        val normalizedClassName =
            className.trim().lowercase()

        /*
         * Use the known approximate height.
         * For unknown objects, use a conservative default
         * so the existing SpatialAnalyzer API continues
         * to work without changes.
         */
        val realHeight =
            averageHeightsMeters[normalizedClassName] ?: 1.0f

        /*
         * Approximate focal length for a phone camera.
         */
        val focalLengthPx =
            imageHeight * 1.15f

        val pixelHeight =
            boundingBox.height()

        /*
         * Basic monocular distance estimation:
         *
         * distance =
         * real object height * focal length
         * ----------------------------------
         * object height in pixels
         */
        val estimatedDistance =
            (realHeight * focalLengthPx) / pixelHeight

        if (
            estimatedDistance.isNaN() ||
            estimatedDistance.isInfinite() ||
            estimatedDistance <= 0f
        ) {
            return Pair(null, "distance unknown")
        }

        /*
         * Limit extreme estimates.
         */
        val safeDistance =
            estimatedDistance.coerceIn(0.3f, 20.0f)

        val roundedDistance =
            (safeDistance * 10f)
                .roundToInt() / 10.0f

        val formattedDistance = when {
            roundedDistance < 1.0f ->
                "less than a meter away"

            roundedDistance < 2.0f ->
                "about one meter away"

            else ->
                "about ${roundedDistance.roundToInt()} meters away"
        }

        return Pair(
            roundedDistance,
            formattedDistance
        )
    }
}