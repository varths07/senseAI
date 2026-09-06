package com.example.senseai.trust

import com.example.senseai.data.model.DetectionResult

class ConfidenceManager {

    fun filterDetections(
        detections: List<DetectionResult>,
        minConfidence: Float = 0.45f
    ): List<DetectionResult> {
        return detections.filter { it.confidence >= minConfidence }
    }
}
