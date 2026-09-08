package com.example.senseai.data.model

data class TrackedObject(
    val detection: DetectionResult,
    val spatial: SpatialResult,
    val trust: TrustResult
) {

    val objectName: String
        get() = detection.className

    val confidence: Float
        get() = detection.confidence

    val direction: SpatialDirection
        get() = spatial.direction

    val distanceMeters: Float?
        get() = spatial.estimatedDistanceMeters

    val movementState: MovementState
        get() = spatial.movementState

    val riskLevel: RiskLevel
        get() = trust.riskLevel

    val trustLevel: TrustLevel
        get() = trust.trustLevel

    val isApproaching: Boolean
        get() = spatial.isApproaching

    val isAhead: Boolean
        get() = spatial.isAhead

    val isHighRisk: Boolean
        get() = riskLevel == RiskLevel.HIGH

    val isReliable: Boolean
        get() = trust.isVerified
}

data class SceneResult(
    val primaryObject: TrackedObject?,
    val secondaryObjects: List<TrackedObject>,
    val primaryAlertText: String?,
    val fullSceneDescription: String,
    val timestamp: Long = System.currentTimeMillis()
) {

    val hasObjects: Boolean
        get() = primaryObject != null

    val objectCount: Int
        get() = (if (primaryObject != null) 1 else 0) + secondaryObjects.size

    val hasHighRiskObject: Boolean
        get() = primaryObject?.isHighRisk == true ||
                secondaryObjects.any { it.isHighRisk }

    val approachingObjects: List<TrackedObject>
        get() = buildList {
            primaryObject?.let {
                if (it.isApproaching) add(it)
            }

            addAll(
                secondaryObjects.filter { it.isApproaching }
            )
        }
}