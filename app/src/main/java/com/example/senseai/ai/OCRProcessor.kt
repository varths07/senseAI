package com.example.senseai.ai

import androidx.camera.core.ImageProxy
import com.example.senseai.utils.Logger
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRProcessor {

    private val textRecognizer =
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    fun processImage(
        imageProxy: ImageProxy,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            onSuccess("No text visible")
            return
        }

        try {

            val inputImage =
                InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

            textRecognizer
                .process(inputImage)
                .addOnSuccessListener { text ->

                    try {
                        val detectedText =
                            text.text
                                .trim()
                                .replace(
                                    Regex("\\s+"),
                                    " "
                                )

                        if (detectedText.isEmpty()) {
                            onSuccess("No readable text found")
                        } else {
                            onSuccess(detectedText)
                        }

                    } catch (e: Exception) {
                        Logger.e(
                            "Error reading OCR result",
                            e
                        )
                        onError(e)
                    } finally {
                        imageProxy.close()
                    }
                }
                .addOnFailureListener { e ->

                    Logger.e(
                        "OCR processing failed",
                        e
                    )

                    imageProxy.close()
                    onError(e)
                }

        } catch (e: Exception) {

            Logger.e(
                "Failed to process OCR image",
                e
            )

            imageProxy.close()
            onError(e)
        }
    }

    fun close() {
        try {
            textRecognizer.close()
        } catch (e: Exception) {
            Logger.e(
                "Error closing OCRProcessor",
                e
            )
        }
    }
}