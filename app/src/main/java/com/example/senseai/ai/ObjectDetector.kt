package com.example.senseai.ai

import android.graphics.RectF
import androidx.camera.core.ImageProxy
import com.example.senseai.data.model.DetectionResult
import com.example.senseai.utils.Logger
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

interface ObjectDetectorInterface {

    fun detectObjects(
        imageProxy: ImageProxy,
        onSuccess: (List<DetectionResult>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun close()
}

class ObjectDetector : ObjectDetectorInterface {

    private val detectorOptions =
        ObjectDetectorOptions.Builder()
            .setDetectorMode(
                ObjectDetectorOptions.STREAM_MODE
            )
            .enableMultipleObjects()
            .enableClassification()
            .build()

    private val mlKitDetector =
        ObjectDetection.getClient(detectorOptions)

    override fun detectObjects(
        imageProxy: ImageProxy,
        onSuccess: (List<DetectionResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            onSuccess(emptyList())
            return
        }

        try {

            val inputImage =
                InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

            mlKitDetector
                .process(inputImage)
                .addOnSuccessListener { detectedObjects ->

                    try {

                        val results =
                            detectedObjects.mapNotNull { obj ->

                                val bounds =
                                    RectF(obj.boundingBox)

                                if (
                                    bounds.width() <= 0f ||
                                    bounds.height() <= 0f
                                ) {
                                    return@mapNotNull null
                                }

                                val label =
                                    obj.labels
                                        .maxByOrNull {
                                            it.confidence
                                        }

                                val className =
                                    label?.text
                                        ?.trim()
                                        ?.lowercase()
                                        ?.takeIf {
                                            it.isNotEmpty()
                                        }
                                        ?: "object"

                                val confidence =
                                    (
                                            label?.confidence
                                                ?: 0.65f
                                            ).coerceIn(
                                            0.0f,
                                            1.0f
                                        )

                                DetectionResult(
                                    className = className,
                                    confidence = confidence,
                                    boundingBox = bounds,
                                    timestamp =
                                        System.currentTimeMillis(),
                                    trackingId =
                                        obj.trackingId
                                )
                            }

                        onSuccess(results)

                    } catch (e: Exception) {

                        Logger.e(
                            "Error converting ML Kit detections",
                            e
                        )

                        onError(e)
                    } finally {
                        imageProxy.close()
                    }
                }
                .addOnFailureListener { e ->

                    Logger.e(
                        "ML Kit Object Detection failed",
                        e
                    )

                    imageProxy.close()
                    onError(e)
                }

        } catch (e: Exception) {

            Logger.e(
                "Failed to create ML Kit input image",
                e
            )

            imageProxy.close()
            onError(e)
        }
    }

    override fun close() {
        try {
            mlKitDetector.close()
        } catch (e: Exception) {
            Logger.e(
                "Error closing ObjectDetector",
                e
            )
        }
    }
}