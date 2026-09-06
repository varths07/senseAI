package com.example.senseai.ai

import android.content.Context
import com.example.senseai.utils.Logger
import java.io.File

/**
 * ModelManager handles locating and initializing on-device vision models.
 *
 * For custom TFLite models:
 * - Model file path: `app/src/main/assets/models/senseai_detector.tflite`
 * - Expected Input: Bitmap / TensorImage [1, 300, 300, 3] (UINT8 or FLOAT32)
 * - Expected Output:
 *     Output 0: Bounding boxes [1, 10, 4]
 *     Output 1: Class indices [1, 10]
 *     Output 2: Scores/Confidences [1, 10]
 *     Output 3: Detection count [1]
 */
class ModelManager(private val context: Context) {

    companion object {
        const val DEFAULT_CUSTOM_MODEL_ASSET_PATH = "models/senseai_detector.tflite"
    }

    fun isCustomModelAvailable(): Boolean {
        return try {
            val assets = context.assets.list("models")
            assets?.contains("senseai_detector.tflite") == true
        } catch (e: Exception) {
            false
        }
    }

    fun getModelSpecification(): String {
        return """
            SenseAI Model Specification:
            - Asset location: app/src/main/assets/models/senseai_detector.tflite
            - Default On-Device Engine: ML Kit Vision Object Detection + Tracking (Default)
            - Fallback/Custom Engine: TFLite Support Task Vision API
            - Target Classes: Person, Vehicle/Car, Chair, Table, Door, Stairs, Obstacle
        """.trimIndent()
    }
}
