package com.example.senseai.ai

import android.content.Context
import com.example.senseai.utils.Logger
import java.io.File

class ModelManager(
    private val context: Context
) {

    companion object {
        private const val MODEL_DIRECTORY = "models"
        private const val MODEL_FILE_NAME = "senseai_detector.tflite"
    }

    private var modelAvailable = false

    init {
        checkModel()
    }

    private fun checkModel() {
        try {
            val modelFile = getModelFile()

            modelAvailable = modelFile.exists() &&
                    modelFile.isFile &&
                    modelFile.length() > 0L

            if (modelAvailable) {
                Logger.i(
                    "SenseAI model found: ${modelFile.absolutePath}"
                )
            } else {
                Logger.w(
                    "SenseAI model not found"
                )
            }

        } catch (e: Exception) {
            modelAvailable = false

            Logger.e(
                "Failed to check SenseAI model",
                e
            )
        }
    }

    fun isModelAvailable(): Boolean {
        return modelAvailable
    }

    fun getModelFile(): File {
        val directory =
            File(
                context.filesDir,
                MODEL_DIRECTORY
            )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        return File(
            directory,
            MODEL_FILE_NAME
        )
    }

    fun getModelPath(): String? {
        val file = getModelFile()

        return if (
            file.exists() &&
            file.isFile &&
            file.length() > 0L
        ) {
            file.absolutePath
        } else {
            null
        }
    }

    fun refreshModelStatus(): Boolean {
        checkModel()
        return modelAvailable
    }
}