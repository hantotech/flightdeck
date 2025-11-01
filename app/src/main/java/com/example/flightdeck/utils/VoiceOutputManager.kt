package com.example.flightdeck.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

/**
 * Manages voice output using Android TextToSpeech
 * Converts ATC responses to spoken audio
 */
class VoiceOutputManager(
    private val context: Context,
    private val onInitialized: (Boolean) -> Unit
) {

    private var textToSpeech: TextToSpeech? = null
    private val tag = "VoiceOutputManager"
    private var isInitialized = false

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS

            if (isInitialized) {
                configureVoice()
                onInitialized(true)
            } else {
                Log.e(tag, "TTS initialization failed")
                onInitialized(false)
            }
        }
    }

    private fun configureVoice() {
        textToSpeech?.apply {
            // Set language
            val result = setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(tag, "Language not supported")
            }

            // Configure voice parameters for ATC-like sound
            setPitch(1.0f)  // Normal pitch
            setSpeechRate(0.9f)  // Slightly slower for clarity

            // Set utterance progress listener
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(tag, "Started speaking: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(tag, "Finished speaking: $utteranceId")
                }

                override fun onError(utteranceId: String?) {
                    Log.e(tag, "Error speaking: $utteranceId")
                }
            })
        }
    }

    /**
     * Speak ATC response
     */
    fun speak(text: String, utteranceId: String = UUID.randomUUID().toString()) {
        if (!isInitialized) {
            Log.e(tag, "TTS not initialized")
            return
        }

        // Clean text for better TTS pronunciation
        val cleanedText = prepareTextForSpeech(text)

        textToSpeech?.speak(
            cleanedText,
            TextToSpeech.QUEUE_FLUSH,  // Clear queue and speak immediately
            null,
            utteranceId
        )
    }

    /**
     * Prepare text for better speech synthesis
     * Handle aviation-specific terminology
     */
    private fun prepareTextForSpeech(text: String): String {
        var result = text

        // Spell out aircraft callsigns phonetically
        // Example: "N12345" -> "November One Two Three Four Five"
        result = result.replace(Regex("N(\\d+)")) { matchResult ->
            val numbers = matchResult.groupValues[1]
            "November " + numbers.map { numberToWord(it) }.joinToString(" ")
        }

        // Handle runway numbers
        // Example: "runway 31" -> "runway three one"
        result = result.replace(Regex("runway (\\d{1,2}[LRC]?)", RegexOption.IGNORE_CASE)) { matchResult ->
            val runway = matchResult.groupValues[1]
            "runway " + runway.toCharArray().map { char ->
                when {
                    char.isDigit() -> numberToWord(char)
                    char == 'L' || char == 'l' -> "left"
                    char == 'R' || char == 'r' -> "right"
                    char == 'C' || char == 'c' -> "center"
                    else -> char.toString()
                }
            }.joinToString(" ")
        }

        // Handle altimeter settings
        // Example: "30.12" -> "three zero point one two"
        result = result.replace(Regex("(\\d{2})\\.(\\d{2})")) { matchResult ->
            val before = matchResult.groupValues[1]
            val after = matchResult.groupValues[2]
            before.map { numberToWord(it) }.joinToString(" ") + " point " +
            after.map { numberToWord(it) }.joinToString(" ")
        }

        return result
    }

    private fun numberToWord(digit: Char): String {
        return when (digit) {
            '0' -> "zero"
            '1' -> "one"
            '2' -> "two"
            '3' -> "three"
            '4' -> "four"
            '5' -> "five"
            '6' -> "six"
            '7' -> "seven"
            '8' -> "eight"
            '9' -> "nine"
            else -> digit.toString()
        }
    }

    /**
     * Stop speaking
     */
    fun stop() {
        textToSpeech?.stop()
    }

    /**
     * Check if currently speaking
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }

    /**
     * Cleanup
     */
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}
