package com.example.senseai.utils

object Constants {
    const val APP_NAME = "SenseAI"
    const val TAGLINE = "See Less. Know More."

    // Camera & Analysis Settings
    const val INFERENCE_INTERVAL_MS = 250L // ~4 FPS inference cap to save battery & CPU
    const val DEFAULT_MODEL_CONFIDENCE = 0.50f
    const val MAX_TRACKING_HISTORY = 10

    // High Risk Object Classes
    val CRITICAL_OBJECT_CLASSES = setOf(
        "person",
        "car",
        "vehicle",
        "bus",
        "truck",
        "motorcycle",
        "bicycle",
        "stair",
        "stairs",
        "step",
        "door",
        "hole",
        "pole"
    )

    // Approaching Threshold (Distance Decrease Ratio)
    const val APPROACHING_DELTA_THRESHOLD_METERS = -0.3f // Distance reduced by > 0.3m
}
