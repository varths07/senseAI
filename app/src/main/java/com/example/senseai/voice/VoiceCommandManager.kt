package com.example.senseai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.senseai.utils.Logger
import java.util.Locale

class VoiceCommandManager(
    context: Context,
    private val onCommand: (String) -> Unit
) {

    private val appContext = context.applicationContext

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    private var currentLanguage =
        TextToSpeechManager.Language.ENGLISH

    init {
        initializeRecognizer()
    }

    private fun initializeRecognizer() {

        if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
            Logger.e("Speech recognition is not available")
            return
        }

        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(appContext)

        speechRecognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
                    Logger.i("Voice recognition ready")
                }

                override fun onBeginningOfSpeech() {
                    Logger.i("User started speaking")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Audio level callback.
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Raw audio callback.
                }

                override fun onEndOfSpeech() {
                    isListening = false
                    Logger.i("User stopped speaking")
                }

                override fun onError(error: Int) {

                    isListening = false

                    Logger.w(
                        "Voice recognition error: $error"
                    )
                }

                override fun onResults(results: Bundle?) {

                    isListening = false

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val command =
                        matches
                            ?.firstOrNull()
                            ?.trim()

                    if (!command.isNullOrEmpty()) {

                        Logger.i(
                            "Voice command: $command"
                        )

                        onCommand(command)
                    }
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {
                    // Partial recognition can be handled later.
                }

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {
                    // Reserved for future use.
                }
            }
        )
    }

    fun setLanguage(
        language: TextToSpeechManager.Language
    ) {

        currentLanguage = language

        Logger.i(
            "Voice command language: ${language.name}"
        )
    }

    fun setEnglish() {
        setLanguage(
            TextToSpeechManager.Language.ENGLISH
        )
    }

    fun setTamil() {
        setLanguage(
            TextToSpeechManager.Language.TAMIL
        )
    }

    fun startListening() {

        if (isListening) {
            return
        }

        if (speechRecognizer == null) {
            Logger.e(
                "Speech recognizer is not initialized"
            )
            return
        }

        val languageTag =
            when (currentLanguage) {

                TextToSpeechManager.Language.ENGLISH ->
                    Locale.US.toLanguageTag()

                TextToSpeechManager.Language.TAMIL ->
                    Locale("ta", "IN").toLanguageTag()
            }

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    languageTag
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                    languageTag
                )

                putExtra(
                    RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                    false
                )

                putExtra(
                    RecognizerIntent.EXTRA_MAX_RESULTS,
                    3
                )
            }

        try {

            isListening = true

            speechRecognizer?.startListening(intent)

            Logger.i(
                "Started listening in $languageTag"
            )

        } catch (e: Exception) {

            isListening = false

            Logger.e(
                "Failed to start voice recognition",
                e
            )
        }
    }

    fun stopListening() {

        if (!isListening) {
            return
        }

        try {

            speechRecognizer?.stopListening()

            isListening = false

        } catch (e: Exception) {

            Logger.e(
                "Failed to stop voice recognition",
                e
            )
        }
    }

    fun cancelListening() {

        try {

            speechRecognizer?.cancel()

            isListening = false

        } catch (e: Exception) {

            Logger.e(
                "Failed to cancel voice recognition",
                e
            )
        }
    }

    fun isListening(): Boolean {
        return isListening
    }

    fun isAvailable(): Boolean {
        return speechRecognizer != null
    }

    fun shutdown() {

        try {

            speechRecognizer?.cancel()
            speechRecognizer?.destroy()

            speechRecognizer = null
            isListening = false

            Logger.i(
                "Voice command manager shutdown"
            )

        } catch (e: Exception) {

            Logger.e(
                "Error shutting down voice recognition",
                e
            )
        }
    }
}