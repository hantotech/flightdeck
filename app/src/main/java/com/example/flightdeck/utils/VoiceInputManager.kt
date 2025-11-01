package com.example.flightdeck.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Manages voice input using Android SpeechRecognizer
 * Converts pilot speech to text for AI processing
 */
class VoiceInputManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val tag = "VoiceInputManager"

    /**
     * Check if speech recognition is available on device
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Start listening for pilot transmission
     * Returns Flow<VoiceResult> with recognition results
     */
    fun startListening(): Flow<VoiceResult> = callbackFlow {
        // Create speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        // Create recognition intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            // Important: This helps with aviation terminology
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }

        // Set up recognition listener
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(tag, "Ready for speech")
                trySend(VoiceResult.Listening)
            }

            override fun onBeginningOfSpeech() {
                Log.d(tag, "Beginning of speech")
                trySend(VoiceResult.Speaking)
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Audio level changed - could use for visual feedback
                trySend(VoiceResult.AudioLevel(rmsdB))
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }

            override fun onEndOfSpeech() {
                Log.d(tag, "End of speech")
                trySend(VoiceResult.Processing)
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error"
                }
                Log.e(tag, "Recognition error: $errorMessage")
                trySend(VoiceResult.Error(errorMessage))
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""

                Log.d(tag, "Recognition result: $text")
                trySend(VoiceResult.Success(text))
                close()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""

                Log.d(tag, "Partial result: $text")
                trySend(VoiceResult.Partial(text))
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Event occurred
            }
        })

        // Start listening
        speechRecognizer?.startListening(intent)

        // Cleanup when flow is closed
        awaitClose {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    /**
     * Cancel and cleanup
     */
    fun cancel() {
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

/**
 * Voice recognition results
 */
sealed class VoiceResult {
    object Listening : VoiceResult()
    object Speaking : VoiceResult()
    object Processing : VoiceResult()
    data class AudioLevel(val level: Float) : VoiceResult()
    data class Partial(val text: String) : VoiceResult()
    data class Success(val text: String) : VoiceResult()
    data class Error(val message: String) : VoiceResult()
}
