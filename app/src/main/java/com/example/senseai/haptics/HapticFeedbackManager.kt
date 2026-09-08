package com.example.senseai.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.senseai.utils.Logger
import java.util.Locale

class TextToSpeechManager(
    context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? =
        TextToSpeech(context.applicationContext, this)

    private var initialized = false

    private var currentLanguage = Language.ENGLISH

    private var speechRate = 1.0f
    private var pitch = 1.0f

    private var lastSpokenText = ""
    private var lastSpokenTime = 0L

    enum class Language {
        ENGLISH,
        TAMIL
    }

    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) {
            initialized = false
            Logger.e("Text-to-Speech initialization failed")
            onInitComplete(false)
            return
        }

        initialized = true

        textToSpeech?.setSpeechRate(speechRate)
        textToSpeech?.setPitch(pitch)

        val success = setLanguage(currentLanguage)

        if (!success) {
            Logger.e("Required TTS language is not available")
        }

        Logger.i("Text-to-Speech initialized")

        onInitComplete(success)
    }

    fun isReady(): Boolean {
        return initialized && textToSpeech != null
    }

    fun setLanguage(language: Language): Boolean {

        if (!initialized) {
            currentLanguage = language
            return false
        }

        val locale = when (language) {
            Language.ENGLISH -> Locale.US
            Language.TAMIL -> Locale("ta", "IN")
        }

        val result =
            textToSpeech?.setLanguage(locale)
                ?: TextToSpeech.ERROR

        val supported =
            result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED

        if (supported) {
            currentLanguage = language

            Logger.i(
                "Voice language changed to ${language.name}"
            )
        } else {
            Logger.e(
                "TTS language not supported: ${language.name}"
            )
        }

        return supported
    }

    fun setEnglish(): Boolean {
        return setLanguage(Language.ENGLISH)
    }

    fun setTamil(): Boolean {
        return setLanguage(Language.TAMIL)
    }

    fun getCurrentLanguage(): Language {
        return currentLanguage
    }

    fun setSpeechRate(rate: Float) {

        speechRate =
            rate.coerceIn(0.5f, 2.0f)

        textToSpeech?.setSpeechRate(speechRate)
    }

    fun setPitch(value: Float) {

        pitch =
            value.coerceIn(0.5f, 2.0f)

        textToSpeech?.setPitch(pitch)
    }

    fun speak(
        text: String,
        urgent: Boolean = false
    ) {

        if (!isReady()) {
            Logger.w("TTS is not ready")
            return
        }

        val cleanText =
            text
                .replace("\\s+".toRegex(), " ")
                .trim()

        if (cleanText.isEmpty()) {
            return
        }

        val currentTime =
            System.currentTimeMillis()

        /*
         * Prevent the same detection from
         * being announced continuously.
         */
        if (!urgent &&
            cleanText.equals(
                lastSpokenText,
                ignoreCase = true
            ) &&
            currentTime - lastSpokenTime < 3500L
        ) {
            return
        }

        lastSpokenText = cleanText
        lastSpokenTime = currentTime

        val queueMode =
            if (urgent) {
                TextToSpeech.QUEUE_FLUSH
            } else {
                TextToSpeech.QUEUE_ADD
            }

        textToSpeech?.speak(
            cleanText,
            queueMode,
            null,
            "senseai_${currentTime}"
        )
    }

    fun speakUrgent(text: String) {
        speak(
            text = text,
            urgent = true
        )
    }

    fun speakEnglish(text: String) {

        if (setEnglish()) {
            speak(text)
        }
    }

    fun speakTamil(text: String) {

        if (setTamil()) {
            speak(text)
        }
    }

    fun stop() {

        try {
            textToSpeech?.stop()

            lastSpokenText = ""
            lastSpokenTime = 0L

        } catch (e: Exception) {
            Logger.e(
                "Error stopping TTS",
                e
            )
        }
    }

    fun shutdown() {

        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()

            textToSpeech = null
            initialized = false

            lastSpokenText = ""
            lastSpokenTime = 0L

        } catch (e: Exception) {
            Logger.e(
                "Error shutting down TTS",
                e
            )
        }
    }
}