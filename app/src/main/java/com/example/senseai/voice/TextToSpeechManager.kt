package com.example.senseai.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.senseai.utils.Logger
import java.util.Locale

class TextToSpeechManager(
    context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false
    private var lastSpokenText: String = ""
    private var lastSpokenTimestamp: Long = 0L

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.e("TTS language not supported.")
                isInitialized = false
                onInitComplete(false)
            } else {
                isInitialized = true
                Logger.i("TTS initialized successfully.")
                onInitComplete(true)
            }
        } else {
            Logger.e("TTS initialization failed.")
            isInitialized = false
            onInitComplete(false)
        }
    }

    fun setSpeechRate(rate: Float) {
        if (isInitialized) {
            tts?.setSpeechRate(rate)
        }
    }

    fun speak(
        text: String,
        isUrgent: Boolean = false,
        minRepeatIntervalMs: Long = 4000L
    ) {
        if (!isInitialized || text.isBlank()) return

        val now = System.currentTimeMillis()
        if (!isUrgent && text == lastSpokenText && (now - lastSpokenTimestamp) < minRepeatIntervalMs) {
            return // Skip repeating identical alert
        }

        lastSpokenText = text
        lastSpokenTimestamp = now

        val queueMode = if (isUrgent) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, "senseai_tts_${now}")
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
            Logger.i("TTS shutdown cleanly.")
        } catch (e: Exception) {
            Logger.e("Error shutting down TTS", e)
        }
    }
}
