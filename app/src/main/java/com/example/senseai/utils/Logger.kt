package com.example.senseai.utils

import android.util.Log

object Logger {

    private const val TAG = "SenseAI"

    private var debugEnabled = true

    fun setDebugEnabled(enabled: Boolean) {
        debugEnabled = enabled
    }

    fun d(message: String) {
        if (debugEnabled) {
            Log.d(TAG, message)
        }
    }

    fun i(message: String) {
        if (debugEnabled) {
            Log.i(TAG, message)
        }
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }

    fun e(
        message: String,
        throwable: Throwable? = null
    ) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }

    fun detection(
        objectName: String,
        confidence: Float,
        direction: String,
        distance: String
    ) {
        if (!debugEnabled) return

        Log.d(
            TAG,
            "DETECTION | " +
                    "object=$objectName | " +
                    "confidence=${(confidence * 100).toInt()}% | " +
                    "direction=$direction | " +
                    "distance=$distance"
        )
    }

    fun risk(
        objectName: String,
        riskLevel: String,
        trustLevel: String
    ) {
        if (!debugEnabled) return

        Log.d(
            TAG,
            "RISK | " +
                    "object=$objectName | " +
                    "risk=$riskLevel | " +
                    "trust=$trustLevel"
        )
    }

    fun performance(
        processingTimeMs: Long
    ) {
        if (!debugEnabled) return

        Log.d(
            TAG,
            "PERFORMANCE | processingTime=${processingTimeMs}ms"
        )
    }
}