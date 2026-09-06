package com.example.senseai.ai

import android.graphics.RectF
import androidx.camera.core.ExperimentalGetImage
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

    private val detectorOptions = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .build()

    private val mlKitDetector = ObjectDetection.getClient(detectorOptions)

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

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        mlKitDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                val results = detectedObjects.map { obj ->
                    val label = obj.labels.firstOrNull()
                    val className = label?.text ?: mapCategoryToName(obj.labels.firstOrNull()?.index ?: -1)
                    val confidence = label?.confidence ?: 0.65f
                    val bounds = RectF(obj.boundingBox)

                    DetectionResult(
                        className = className,
                        confidence = confidence,
                        boundingBox = bounds,
                        timestamp = System.currentTimeMillis(),
                        trackingId = obj.trackingId
                    )
                }
                imageProxy.close()
                onSuccess(results)
            }
            .addOnFailureListener { e ->
                Logger.e("MLKit Object Detection failed", e)
                imageProxy.close()
                onError(e)
            }
    }

    private fun mapCategoryToName(index: Int): String {
        return when (index) {
            0 -> "object"
            1 -> "person"
            2 -> "food"
            3 -> "plant"
            4 -> "place"
            5 -> "vehicle"
            else -> "obstacle"
        }
    }

    override fun close() {
        try {
            mlKitDetector.close()
        } catch (e: Exception) {
            Logger.e("Error closing ObjectDetector", e)
        }
    }
}
