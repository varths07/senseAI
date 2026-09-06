package com.example.senseai.ai

import com.example.senseai.data.model.*
import com.example.senseai.trust.TrustEngine

class SceneAnalyzer {
    private val trustEngine = TrustEngine()

    fun analyzeScene(
        trackedObjects: List<TrackedObject>
    ): SceneResult {
        if (trackedObjects.isEmpty()) {
            return SceneResult(
                primaryObject = null,
                secondaryObjects = emptyList(),
                primaryAlertText = null,
                fullSceneDescription = "Clear path ahead."
            )
        }

        // Sort by priority: Risk Level > Approaching State > Distance
        val sortedDetections = trackedObjects.sortedWith(
            compareByDescending<TrackedObject> { it.trust.riskLevel.ordinal }
                .thenByDescending { if (it.spatial.movementState == MovementState.APPROACHING) 1 else 0 }
                .thenBy { it.spatial.estimatedDistanceMeters ?: 99f }
        )

        val primary = sortedDetections.first()
        val secondary = sortedDetections.drop(1)

        val primaryAlertText = trustEngine.buildAnnouncementText(
            detection = primary.detection,
            spatial = primary.spatial,
            trust = primary.trust
        )

        val sceneDescription = buildString {
            append(primaryAlertText)
            if (secondary.isNotEmpty()) {
                val nextTwo = secondary.take(2)
                append(" Also ")
                append(nextTwo.joinToString(". ") { obj ->
                    "${obj.detection.className} ${obj.spatial.direction.label}"
                })
                append(".")
            }
        }

        return SceneResult(
            primaryObject = primary,
            secondaryObjects = secondary,
            primaryAlertText = primaryAlertText,
            fullSceneDescription = sceneDescription
        )
    }
}
