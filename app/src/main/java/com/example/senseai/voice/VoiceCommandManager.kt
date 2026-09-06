package com.example.senseai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.senseai.utils.Logger

sealed class VoiceCommand {
    object WhatIsAhead : VoiceCommand()
    object DescribeScene : VoiceCommand()
    object ReadThis : VoiceCommand()
    object RepeatLast : VoiceCommand()
    object StopAssistance : VoiceCommand()
    data class Unknown(val rawText: String) : VoiceCommand()
}

class VoiceCommandManager(
    private val context: Context,
    private val onCommandReceived: (VoiceCommand) -> Unit
) : RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Logger.w("Speech recognition not available on this device.")
            return
        }

        try {
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@VoiceCommandManager)
                }
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
            Logger.i("Started voice command listening...")
        } catch (e: Exception) {
            Logger.e("Error starting speech recognizer", e)
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            isListening = false
        } catch (e: Exception) {
            Logger.e("Error stopping speech recognizer", e)
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
        } catch (e: Exception) {
            Logger.e("Error destroying speech recognizer", e)
        }
    }

    override fun onResults(results: Bundle?) {
        isListening = false
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val spokenText = matches?.firstOrNull()?.lowercase()?.trim() ?: return

        Logger.i("Voice command heard: $spokenText")

        val command = parseCommand(spokenText)
        onCommandReceived(command)
    }

    private fun parseCommand(text: String): VoiceCommand {
        return when {
            text.contains("what is ahead") || text.contains("what's ahead") || text.contains("ahead") -> VoiceCommand.WhatIsAhead
            text.contains("describe") || text.contains("around me") || text.contains("scene") -> VoiceCommand.DescribeScene
            text.contains("read") || text.contains("text") || text.contains("sign") -> VoiceCommand.ReadThis
            text.contains("repeat") || text.contains("again") -> VoiceCommand.RepeatLast
            text.contains("stop") || text.contains("cancel") || text.contains("pause") -> VoiceCommand.StopAssistance
            else -> VoiceCommand.Unknown(text)
        }
    }

    override fun onError(error: Int) {
        isListening = false
        Logger.w("Speech recognition error code: $error")
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
