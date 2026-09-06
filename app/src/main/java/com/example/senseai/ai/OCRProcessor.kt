package com.example.senseai.ai

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.senseai.utils.Logger
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRProcessor {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

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

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        textRecognizer.process(inputImage)
            .addOnSuccessListener { text ->
                val detectedText = text.text.trim()
                imageProxy.close()
                if (detectedText.isEmpty()) {
                    onSuccess("No readable text found")
                } else {
                    onSuccess(detectedText)
                }
            }
            .addOnFailureListener { e ->
                Logger.e("OCR processing failed", e)
                imageProxy.close()
                onError(e)
            }
    }

    fun close() {
        try {
            textRecognizer.close()
        } catch (e: Exception) {
            Logger.e("Error closing OCRProcessor", e)
        }
    }
}
