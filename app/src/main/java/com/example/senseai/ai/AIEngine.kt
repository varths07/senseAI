package com.example.senseai.ai

import android.content.Context
import androidx.camera.core.ImageProxy
import com.example.senseai.data.model.SceneResult
import com.example.senseai.data.model.TrackedObject
import com.example.senseai.spatial.SpatialAnalyzer
import com.example.senseai.trust.ConfidenceManager
import com.example.senseai.trust.TrustEngine
import com.example.senseai.utils.Logger

class AIEngine(context: Context) {

    val modelManager = ModelManager(context)

    private val objectDetector = ObjectDetector()
    private val objectTracker = ObjectTracker()
    private val spatialAnalyzer = SpatialAnalyzer()
    private val trustEngine = TrustEngine()
    private val sceneAnalyzer = SceneAnalyzer()
    private val confidenceManager = ConfidenceManager()

    val ocrProcessor = OCRProcessor()

    fun processFrame(
        imageProxy: ImageProxy,
        onSceneResult: (SceneResult) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val imageWidth = imageProxy.width.toFloat()
        val imageHeight = imageProxy.height.toFloat()

        objectDetector.detectObjects(
            imageProxy = imageProxy,

            onSuccess = { detections ->

                try {

                    // Remove very low-confidence detections.
                    val filteredDetections =
                        confidenceManager.filterDetections(detections)

                    // Track the remaining detections.
                    val tracks =
                        objectTracker.processDetections(
                            filteredDetections
                        )

                    // Analyse every detected object.
                    val trackedObjects =
                        filteredDetections.map { detection ->

                            val history =
                                detection.trackingId?.let {
                                    tracks[it]
                                }

                            val spatial =
                                spatialAnalyzer.analyzeSpatial(
                                    detection = detection,
                                    imageWidth = imageWidth,
                                    imageHeight = imageHeight,
                                    trackHistory = history
                                )

                            val trust =
                                trustEngine.evaluate(
                                    detection = detection,
                                    spatial = spatial,
                                    trackHistory = history
                                )

                            TrackedObject(
                                detection = detection,
                                spatial = spatial,
                                trust = trust
                            )
                        }

                    // Analyse the complete scene.
                    val sceneResult =
                        sceneAnalyzer.analyzeScene(
                            trackedObjects
                        )

                    onSceneResult(sceneResult)

                } catch (e: Exception) {

                    Logger.e(
                        "Error processing frame in AIEngine",
                        e
                    )

                    onError(e)
                }
            },

            onError = onError
        )
    }

    fun close() {
        objectDetector.close()
        ocrProcessor.close()
        objectTracker.clear()
    }
}