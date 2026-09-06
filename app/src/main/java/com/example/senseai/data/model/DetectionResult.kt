package com.example.senseai.data.model

import android.graphics.RectF

/**
 * Represents a single detected object in a frame.
 */
data class DetectionResult(
    val className: String,
    val confidence: Float,
    val boundingBox: RectF,
    val timestamp: Long = System.currentTimeMillis(),
    val trackingId: Int? = null
)
