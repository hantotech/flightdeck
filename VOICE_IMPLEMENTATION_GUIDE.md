# Voice Integration Implementation Guide

**Priority**: P0 - Critical Path Feature
**Timeline**: Week 1-2
**Goal**: Voice input + voice output for ATC practice

---

## 🎤 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                    USER                              │
│  (Speaks: "Palo Alto Ground, Skyhawk 12345...")    │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│          ANDROID SPEECHRECOGNIZER                    │
│  - Listens to microphone                            │
│  - Converts speech to text                          │
│  - Returns: "Palo Alto Ground, Skyhawk 12345..."   │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│              AI ATC SERVICE                          │
│  - Receives pilot text                              │
│  - Generates ATC response                           │
│  - Returns: "Skyhawk 12345, taxi runway 31..."     │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│          ANDROID TEXTTOSPEECH                        │
│  - Converts text to speech                          │
│  - Plays through speaker                            │
│  - User hears ATC response                          │
└─────────────────────────────────────────────────────┘
```

---

## 📱 Part 1: Voice Input (SpeechRecognizer)

### Step 1: Add Permissions

**AndroidManifest.xml**
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
```

### Step 2: Create VoiceInputManager

**File**: `app/src/main/java/com/example/flightdeck/utils/VoiceInputManager.kt`

```kotlin
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
```

### Step 3: Request Runtime Permissions

**In your Fragment/Activity:**

```kotlin
private fun requestMicrophonePermission() {
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            MICROPHONE_PERMISSION_REQUEST_CODE
        )
    }
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    when (requestCode) {
        MICROPHONE_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, can use voice
                initializeVoice()
            } else {
                // Permission denied
                showPermissionDeniedMessage()
            }
        }
    }
}
```

---

## 🔊 Part 2: Voice Output (TextToSpeech)

### Step 1: Create VoiceOutputManager

**File**: `app/src/main/java/com/example/flightdeck/utils/VoiceOutputManager.kt`

```kotlin
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
        result = result.replace(Regex("runway (\\d{1,2}[LRC]?)")) { matchResult ->
            val runway = matchResult.groupValues[1]
            "runway " + runway.map {
                when {
                    it.isDigit() -> numberToWord(it)
                    it == 'L' -> "left"
                    it == 'R' -> "right"
                    it == 'C' -> "center"
                    else -> it.toString()
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
```

---

## 🎯 Part 3: Integration with ATC Practice

### Updated ATCPracticeFragment

**File**: `app/src/main/java/com/example/flightdeck/ui/atc/ATCPracticeFragment.kt`

```kotlin
class ATCPracticeFragment : Fragment() {

    private var _binding: FragmentAtcPracticeBinding? = null
    private val binding get() = _binding!!

    private lateinit var voiceInputManager: VoiceInputManager
    private lateinit var voiceOutputManager: VoiceOutputManager

    private var isListening = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize voice managers
        voiceInputManager = VoiceInputManager(requireContext())
        voiceOutputManager = VoiceOutputManager(requireContext()) { success ->
            if (!success) {
                showError("Text-to-speech not available")
            }
        }

        // Check microphone permission
        if (checkMicrophonePermission()) {
            setupVoiceButton()
        } else {
            requestMicrophonePermission()
        }
    }

    private fun setupVoiceButton() {
        binding.pttButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start listening (Push-to-Talk pressed)
                    startVoiceInput()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Stop listening (PTT released)
                    stopVoiceInput()
                    true
                }
                else -> false
            }
        }
    }

    private fun startVoiceInput() {
        if (!isListening) {
            isListening = true
            binding.pttButton.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.error, null))

            lifecycleScope.launch {
                voiceInputManager.startListening().collect { result ->
                    handleVoiceResult(result)
                }
            }
        }
    }

    private fun stopVoiceInput() {
        if (isListening) {
            isListening = false
            binding.pttButton.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary, null))
            voiceInputManager.stopListening()
        }
    }

    private fun handleVoiceResult(result: VoiceResult) {
        when (result) {
            is VoiceResult.Listening -> {
                binding.statusText.text = "Listening..."
            }
            is VoiceResult.Speaking -> {
                binding.statusText.text = "Speaking..."
            }
            is VoiceResult.Processing -> {
                binding.statusText.text = "Processing..."
            }
            is VoiceResult.Partial -> {
                binding.statusText.text = result.text
            }
            is VoiceResult.Success -> {
                // Got pilot transmission!
                handlePilotTransmission(result.text)
            }
            is VoiceResult.Error -> {
                binding.statusText.text = "Error: ${result.message}"
            }
            is VoiceResult.AudioLevel -> {
                // Could show visual audio level indicator
            }
        }
    }

    private fun handlePilotTransmission(pilotText: String) {
        // Display what pilot said
        addMessageToChat(pilotText, isFromPilot = true)

        // Send to AI for ATC response
        lifecycleScope.launch {
            val app = requireActivity().application as FlightDeckApplication
            val atcRepo = app.appContainer.atcRepository

            // Get AI ATC response
            val result = atcRepo.sendCommunication(
                sessionId = currentSessionId,
                pilotMessage = pilotText
            )

            result.onSuccess { atcResponse ->
                // Display ATC response
                addMessageToChat(atcResponse, isFromPilot = false)

                // SPEAK the ATC response
                voiceOutputManager.speak(atcResponse)
            }

            result.onFailure { error ->
                showError("Error: ${error.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        voiceInputManager.cancel()
        voiceOutputManager.shutdown()
        _binding = null
    }
}
```

---

## 🎨 Part 4: UI Components

### Push-to-Talk Button Layout

**File**: `res/layout/fragment_atc_practice.xml`

```xml
<!-- Push-to-Talk Button -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/pttButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_margin="16dp"
    android:src="@drawable/ic_mic"
    android:contentDescription="Push to Talk"
    app:backgroundTint="@color/primary"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />

<!-- Status Text -->
<TextView
    android:id="@+id/statusText"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Press and hold to talk"
    android:textSize="14sp"
    android:textColor="@color/text_secondary"
    app:layout_constraintBottom_toTopOf="@id/pttButton"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent" />
```

---

## 🧪 Testing Voice Integration

### Test Cases

1. **Basic Recognition**
   - Say: "Palo Alto Ground, Skyhawk 12345, ready to taxi"
   - Expected: Text appears in chat, AI responds, response is spoken

2. **Aviation Terminology**
   - Say: "November 12345, taxi to runway 31 via alpha"
   - Expected: Proper recognition of phonetic alphabet and runways

3. **Background Noise**
   - Test with typical ambient noise
   - Verify recognition accuracy

4. **PTT Behavior**
   - Hold button: Should start listening
   - Release button: Should stop and process
   - Visual feedback: Button color changes

5. **TTS Pronunciation**
   - Verify "N12345" spoken as "November One Two Three Four Five"
   - Verify "runway 31" spoken as "runway three one"
   - Verify altimeter settings spoken correctly

---

## 📋 Implementation Checklist

- [ ] Add microphone permission to manifest
- [ ] Create VoiceInputManager.kt
- [ ] Create VoiceOutputManager.kt
- [ ] Update ATCPracticeFragment with voice integration
- [ ] Add PTT button to layout
- [ ] Test voice recognition accuracy
- [ ] Test TTS pronunciation
- [ ] Handle permission denied gracefully
- [ ] Add visual feedback for listening state
- [ ] Test on real device (voice doesn't work well in emulator)

---

## 🚀 Next Steps After Voice Works

1. **Improve Recognition Accuracy**
   - Custom language model for aviation terms
   - Noise cancellation
   - Background noise filtering

2. **Better TTS Voice**
   - Explore premium TTS voices
   - Add radio static effect
   - Multiple controller voices

3. **Enhanced UX**
   - Wake word detection
   - Auto-detect end of transmission
   - Visual waveform display

---

This gets you voice working. Start with basic implementation, then iterate based on real pilot feedback.
