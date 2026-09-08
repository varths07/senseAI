package com.example.senseai.ai

import android.content.Context
import com.example.senseai.utils.Logger
import java.io.File
import java.io.FileOutputStream

/**
 * Manages the AI model life cycle in SenseAI.
 *
 * Responsibilities:
 * - Packages the model in APK assets.
 * - Extracts model to internal storage for TensorFlow Lite.
 * - Verifies model integrity.
 */
class ModelManager(
    private val context: Context
) {

    companion object {
        private const val MODEL_DIRECTORY = "models"
        private const val MODEL_FILE_NAME = "senseai_detector.tflite"
    }

    private var modelAvailable = false

    init {
        setupModel()
    }

    /**
     * Ensures the model is available in internal storage.
     * Extracts from assets if necessary.
     */
    private fun setupModel() {
        try {
            val internalFile = getModelFile()

            // 1. If model doesn't exist internally, try to extract from assets
            if (!internalFile.exists() || internalFile.length() <= 0L) {
                Logger.i("ModelManager: Extracting model from assets...")
                extractModelFromAssets(internalFile)
            }

            // 2. Verify availability
            modelAvailable = internalFile.exists() &&
                    internalFile.isFile &&
                    internalFile.length() > 0L

            if (modelAvailable) {
                Logger.i("ModelManager: SenseAI model ready at ${internalFile.absolutePath}")
            } else {
                Logger.e("ModelManager: SenseAI model not found in assets or storage")
            }

        } catch (e: Exception) {
            modelAvailable = false
            Logger.e("ModelManager: Setup failed", e)
        }
    }

    /**
     * Copies the model file from assets to the internal app storage.
     */
    private fun extractModelFromAssets(targetFile: File) {
        try {
            // Ensure directory exists
            targetFile.parentFile?.let {
                if (!it.exists()) it.mkdirs()
            }

            context.assets.open(MODEL_FILE_NAME).use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Logger.i("ModelManager: Model extracted successfully")
        } catch (e: Exception) {
            Logger.e("ModelManager: Failed to extract model from assets", e)
        }
    }

    fun isModelAvailable(): Boolean {
        return modelAvailable
    }

    fun getModelFile(): File {
        val directory = File(context.filesDir, MODEL_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, MODEL_FILE_NAME)
    }

    fun getModelPath(): String? {
        val file = getModelFile()
        return if (file.exists()) file.absolutePath else null
    }

    /**
     * Force a re-check of the model status.
     */
    fun refreshModelStatus(): Boolean {
        setupModel()
        return modelAvailable
    }
}
