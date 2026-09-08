package com.example.senseai.ai

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.senseai.utils.Logger

class OCRProcessor {

    private val recognizer =
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    fun processImage(
        inputImage: InputImage,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {

        recognizer
            .process(inputImage)
            .addOnSuccessListener { result ->

                try {
                    onSuccess(
                        result.text.trim()
                    )
                } catch (e: Exception) {

                    Logger.e(
                        "Failed to process OCR result",
                        e
                    )

                    onError(e)
                }
            }
            .addOnFailureListener { exception ->

                Logger.e(
                    "OCR processing failed",
                    exception
                )

                onError(exception)
            }
    }

    fun close() {
        try {
            recognizer.close()
        } catch (e: Exception) {

            Logger.e(
                "Failed to close OCR recognizer",
                e
            )
        }
    }
}