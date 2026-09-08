package com.example.senseai.trust


import com.example.senseai.data.model.DetectionResult
import kotlin.math.abs

/**
 * Manages detection confidence before detections enter
 * the tracking and safety pipeline.
 *
 * Responsibilities:
 * - Remove very weak detections.
 * - Apply class-specific confidence thresholds.
 * - Avoid invalid confidence values.
 * - Provide confidence normalization utilities.
 */
class ConfidenceManager {

    companion object {
        private const val DEFAULT_MIN_CONFIDENCE = 0.45f

        /**
         * Some objects are more safety-critical and should
         * generally require stronger evidence before being
         * treated as reliable detections.
         */
        private val CLASS_THRESHOLDS = mapOf(
            "person" to 0.40f,
            "car" to 0.45f,
            "vehicle" to 0.45f,
            "bus" to 0.45f,
            "truck" to 0.45f,
            "bicycle" to 0.45f,
            "motorcycle" to 0.45f,
            "chair" to 0.50f,
            "table" to 0.50f,
            "bottle" to 0.50f,
            "cup" to 0.50f,
            "door" to 0.45f,
            "stairs" to 0.45f,
            "obstacle" to 0.50f
        )
    }

    /**
     * Filters detections using a class-aware confidence threshold.
     */
    fun filterDetections(
        detections: List<DetectionResult>,
        minConfidence: Float = DEFAULT_MIN_CONFIDENCE
    ): List<DetectionResult> {

        val safeMinimum = minConfidence.coerceIn(0.0f, 1.0f)

        return detections.filter { detection ->

            val confidence = normalizeConfidence(detection.confidence)

            val classThreshold = CLASS_THRESHOLDS[
                detection.className.trim().lowercase()
            ] ?: safeMinimum

            confidence >= classThreshold
        }
    }

    /**
     * Returns a single detection only when its confidence
     * satisfies the required threshold.
     */
    fun isConfidentEnough(
        detection: DetectionResult,
        minConfidence: Float = DEFAULT_MIN_CONFIDENCE
    ): Boolean {

        val confidence = normalizeConfidence(detection.confidence)

        val classThreshold = CLASS_THRESHOLDS[
            detection.className.trim().lowercase()
        ] ?: minConfidence.coerceIn(0.0f, 1.0f)

        return confidence >= classThreshold
    }

    /**
     * Converts invalid confidence values into a safe range.
     */
    fun normalizeConfidence(confidence: Float): Float {
        if (confidence.isNaN() || confidence.isInfinite()) {
            return 0.0f
        }

        return confidence.coerceIn(0.0f, 1.0f)
    }

    /**
     * Returns a confidence category useful for UI,
     * logging and future announcement decisions.
     */
    fun getConfidenceCategory(confidence: Float): ConfidenceCategory {
        val normalized = normalizeConfidence(confidence)

        return when {
            normalized >= 0.85f -> ConfidenceCategory.VERY_HIGH
            normalized >= 0.70f -> ConfidenceCategory.HIGH
            normalized >= 0.50f -> ConfidenceCategory.MEDIUM
            normalized >= 0.30f -> ConfidenceCategory.LOW
            else -> ConfidenceCategory.VERY_LOW
        }
    }

    /**
     * Measures how much two confidence values differ.
     */
    fun confidenceChangedSignificantly(
        previous: Float,
        current: Float,
        threshold: Float = 0.15f
    ): Boolean {

        val previousNormalized = normalizeConfidence(previous)
        val currentNormalized = normalizeConfidence(current)

        return abs(currentNormalized - previousNormalized) >= threshold
    }
}

/**
 * Human-readable confidence categories.
 */
enum class ConfidenceCategory {
    VERY_HIGH,
    HIGH,
    MEDIUM,
    LOW,
    VERY_LOW
}

