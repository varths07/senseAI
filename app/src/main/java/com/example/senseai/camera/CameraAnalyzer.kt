package com.example.senseai.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.senseai.utils.Constants
import com.example.senseai.utils.Logger

class CameraAnalyzer(
    private val onFrameAvailable: (ImageProxy) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastInferenceTime = 0L

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInferenceTime >= Constants.INFERENCE_INTERVAL_MS) {
            lastInferenceTime = currentTime
            try {
                onFrameAvailable(imageProxy)
            } catch (e: Exception) {
                Logger.e("Error processing camera frame", e)
                imageProxy.close()
            }
        } else {
            // Drop frame to maintain target FPS & avoid UI thread pressure
            imageProxy.close()
        }
    }
}
