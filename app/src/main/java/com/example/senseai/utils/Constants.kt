package com.example.senseai.utils

object Constants {

    const val APP_NAME = "SenseAI"
    const val TAGLINE = "See Less. Know More."

    /*
     * AI processing
     */

    const val INFERENCE_INTERVAL_MS = 250L

    const val DEFAULT_MODEL_CONFIDENCE = 0.50f

    const val MAX_TRACKING_HISTORY = 10


    /*
     * Distance estimation
     */

    const val MIN_DISTANCE_METERS = 0.3f
    const val MAX_DISTANCE_METERS = 20.0f


    /*
     * Movement detection
     */

    const val APPROACHING_DELTA_THRESHOLD_METERS = -0.3f

    const val APPROACHING_DISTANCE_METERS = 2.5f


    /*
     * Safety
     */

    const val HIGH_RISK_DISTANCE_METERS = 1.5f

    const val MEDIUM_RISK_DISTANCE_METERS = 3.5f


    /*
     * Objects that can become important
     * for navigation and safety.
     */

    val CRITICAL_OBJECT_CLASSES = setOf(
        "person",
        "car",
        "vehicle",
        "bus",
        "truck",
        "motorcycle",
        "motorbike",
        "bicycle",
        "stair",
        "stairs",
        "step",
        "door",
        "hole",
        "pole",
        "obstacle"
    )


    /*
     * Objects that should receive
     * stronger voice announcements.
     */

    val HIGH_PRIORITY_OBJECT_CLASSES = setOf(
        "person",
        "car",
        "vehicle",
        "bus",
        "truck",
        "motorcycle",
        "motorbike",
        "bicycle",
        "stairs",
        "stair",
        "step",
        "hole"
    )


    /*
     * Scene announcement timing.
     *
     * Prevents SenseAI from continuously
     * repeating the same message.
     */

    const val ANNOUNCEMENT_COOLDOWN_MS = 3500L

    const val URGENT_ANNOUNCEMENT_COOLDOWN_MS = 1200L


    /*
     * Camera
     */

    const val DEFAULT_CAMERA_WIDTH = 640

    const val DEFAULT_CAMERA_HEIGHT = 480


    /*
     * OCR
     */

    const val OCR_MIN_TEXT_LENGTH = 2


    /*
     * Voice
     */

    const val DEFAULT_SPEECH_RATE = 1.0f

    const val MIN_SPEECH_RATE = 0.5f

    const val MAX_SPEECH_RATE = 2.0f


    /*
     * Tracking
     */

    const val TRACK_TIMEOUT_MS = 2500L

    const val MAX_TRACKING_DISTANCE_RATIO = 1.5f


    /*
     * Application limits
     */

    const val MAX_OBJECTS_TO_ANNOUNCE = 3
}