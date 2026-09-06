package com.example.senseai.data.model

data class TrackedObject(
    val detection: DetectionResult,
    val spatial: SpatialResult,
    val trust: TrustResult
)

data class SceneResult(
    val primaryObject: TrackedObject?,
    val secondaryObjects: List<TrackedObject>,
    val primaryAlertText: String?,
    val fullSceneDescription: String,
    val timestamp: Long = System.currentTimeMillis()
)
