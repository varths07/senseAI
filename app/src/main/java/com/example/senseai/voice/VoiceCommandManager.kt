package com.example.senseai.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import java.util.Locale

class VoiceCommandManager(
    private val context: Context,
    private val voiceManager: VoiceManager
) {

    enum class Command {
        START_ASSISTANCE,
        STOP_ASSISTANCE,
        READ_TEXT,
        DESCRIBE_SCENE,
        UNKNOWN
    }

    fun parseCommand(text: String): Command {
        val command = text.trim().lowercase(Locale.getDefault())

        return when {
            command.contains("start") ||
                    command.contains("begin") ||
                    command.contains("assist") -> {
                Command.START_ASSISTANCE
            }

            command.contains("stop") ||
                    command.contains("exit") ||
                    command.contains("quit") -> {
                Command.STOP_ASSISTANCE
            }

            command.contains("read") ||
                    command.contains("text") ||
                    command.contains("ocr") -> {
                Command.READ_TEXT
            }

            command.contains("describe") ||
                    command.contains("scene") ||
                    command.contains("what do you see") -> {
                Command.DESCRIBE_SCENE
            }

            else -> Command.UNKNOWN
        }
    }

    fun handleCommand(
        text: String,
        onCommand: (Command) -> Unit
    ) {
        val command = parseCommand(text)

        if (command == Command.UNKNOWN) {
            voiceManager.speak(
                "Sorry, I did not understand that command."
            )
            return
        }

        onCommand(command)
    }

    fun createSpeechRecognizerIntent(): Intent {
        return Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Say a command"
            )
        }
    }

    fun setLanguage(
        language: VoiceManager.Language
    ) {
        voiceManager.setLanguage(language)
    }

    fun speakCommandHelp() {
        voiceManager.speak(
            """
            Available commands are:
            start assistance,
            stop assistance,
            read text,
            and describe scene.
            """.trimIndent()
        )
    }

    fun destroy() {
        voiceManager.stop()
    }
}