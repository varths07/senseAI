package com.example.senseai.ai

import com.example.senseai.data.model.MovementState
import com.example.senseai.data.model.SceneResult
import com.example.senseai.data.model.TrackedObject
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

        val sortedObjects =
            trackedObjects.sortedWith(
                compareByDescending<TrackedObject> {
                    riskPriority(it)
                }
                    .thenByDescending {
                        if (
                            it.spatial.movementState ==
                            MovementState.APPROACHING
                        ) {
                            1
                        } else {
                            0
                        }
                    }
                    .thenBy {
                        it.spatial.estimatedDistanceMeters
                            ?: 99f
                    }
                    .thenByDescending {
                        it.detection.confidence
                    }
            )

        val primary =
            sortedObjects.first()

        val secondary =
            sortedObjects.drop(1)

        val primaryAlertText =
            trustEngine.buildAnnouncementText(
                detection = primary.detection,
                spatial = primary.spatial,
                trust = primary.trust
            )

        val sceneDescription =
            buildSceneDescription(
                primary = primary,
                secondary = secondary
            )

        return SceneResult(
            primaryObject = primary,
            secondaryObjects = secondary,
            primaryAlertText = primaryAlertText,
            fullSceneDescription = sceneDescription
        )
    }

    private fun riskPriority(
        objectData: TrackedObject
    ): Int {

        return when (objectData.trust.riskLevel) {
            com.example.senseai.data.model.RiskLevel.HIGH -> 4
            com.example.senseai.data.model.RiskLevel.MEDIUM -> 3
            com.example.senseai.data.model.RiskLevel.LOW -> 2
            com.example.senseai.data.model.RiskLevel.SAFE -> 1
        }
    }

    private fun buildSceneDescription(
        primary: TrackedObject,
        secondary: List<TrackedObject>
    ): String {

        return buildString {

            append(
                trustEngine.buildAnnouncementText(
                    detection = primary.detection,
                    spatial = primary.spatial,
                    trust = primary.trust
                )
            )

            val additionalObjects =
                secondary.take(2)

            if (additionalObjects.isNotEmpty()) {

                append(". Also ")

                append(
                    additionalObjects.joinToString(
                        separator = ". "
                    ) { objectData ->

                        val name =
                            objectData.detection.className
                                .trim()
                                .lowercase()

                        val direction =
                            objectData.spatial.direction.label

                        "$name $direction"
                    }
                )

                append(".")
            }
        }
    }
}