package com.example.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechRecognitionManager(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    // Full text accumulated across pauses and multiple sentences
    private val _accumulatedTranscript = MutableStateFlow("")
    val accumulatedTranscript: StateFlow<String> = _accumulatedTranscript.asStateFlow()

    // Live partial words currently being spoken in the active utterance
    private val _currentPartial = MutableStateFlow("")
    val currentPartial: StateFlow<String> = _currentPartial.asStateFlow()

    // Combined live transcript (accumulated + active partial)
    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var currentLocale: String = "hi-IN"
    private var shouldKeepListening: Boolean = false
    private var consecutiveErrorCount: Int = 0
    private var simulationRunnable: Runnable? = null

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startContinuousListening(languageLocale: String = "hi-IN") {
        currentLocale = languageLocale
        shouldKeepListening = true
        consecutiveErrorCount = 0
        _isListening.value = true
        _errorMessage.value = null

        mainHandler.post {
            ensureRecognizerCreated()
            startListeningIntent()
        }
    }

    fun appendToTranscript(extraText: String) {
        if (extraText.isBlank()) return
        val current = _accumulatedTranscript.value.trim()
        val updated = if (current.isEmpty()) {
            extraText.trim()
        } else {
            "$current ${extraText.trim()}"
        }
        _accumulatedTranscript.value = updated
        _currentPartial.value = ""
        _liveTranscript.value = updated
    }

    fun setManualTranscript(text: String) {
        _accumulatedTranscript.value = text
        _currentPartial.value = ""
        _liveTranscript.value = text
    }

    fun clearTranscript() {
        _accumulatedTranscript.value = ""
        _currentPartial.value = ""
        _liveTranscript.value = ""
    }

    /**
     * Simulates natural live speech typing for testing and emulator demonstrations
     * where hardware microphone input may not be piped from the host browser.
     */
    fun simulateLiveSpeechInput(fullPhrase: String, onFinished: (() -> Unit)? = null) {
        simulationRunnable?.let { mainHandler.removeCallbacks(it) }
        clearTranscript()
        _isListening.value = true
        _rmsLevel.value = 0.6f

        val words = fullPhrase.trim().split("\\s+".toRegex())
        var wordIndex = 0
        var currentText = ""

        val stepRunnable = object : Runnable {
            override fun run() {
                if (wordIndex < words.size) {
                    val nextWord = words[wordIndex]
                    currentText = if (currentText.isEmpty()) nextWord else "$currentText $nextWord"
                    _liveTranscript.value = currentText
                    _accumulatedTranscript.value = currentText
                    _rmsLevel.value = (0.3f + (wordIndex % 4) * 0.18f).coerceIn(0.1f, 0.9f)
                    wordIndex++
                    mainHandler.postDelayed(this, 180L)
                } else {
                    _rmsLevel.value = 0f
                    _isListening.value = false
                    onFinished?.invoke()
                }
            }
        }
        simulationRunnable = stepRunnable
        mainHandler.post(stepRunnable)
    }

    private fun ensureRecognizerCreated() {
        if (speechRecognizer != null) return

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
            Log.i("SpeechManager", "SpeechRecognizer initialized successfully")
        } catch (e: Exception) {
            Log.e("SpeechManager", "Error creating SpeechRecognizer", e)
            _errorMessage.value = "Speech recognition error: ${e.message}"
        }
    }

    private fun extractMatches(bundle: Bundle?): List<String> {
        if (bundle == null) return emptyList()

        // Standard speech recognizer keys
        bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { if (it.isNotEmpty()) return it }
        bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)?.let { if (it.isNotEmpty()) return it }
        bundle.getStringArrayList("results_recognition")?.let { if (it.isNotEmpty()) return it }
        bundle.getStringArrayList("android.speech.extra.PARTIAL_RESULTS")?.let { if (it.isNotEmpty()) return it }
        bundle.getStringArrayList("android.speech.extra.UNCONSUMED_INPUT")?.let { if (it.isNotEmpty()) return it }

        // Array of strings fallback
        bundle.getStringArray(SpeechRecognizer.RESULTS_RECOGNITION)?.let { if (it.isNotEmpty()) return it.toList() }
        bundle.getStringArray(RecognizerIntent.EXTRA_RESULTS)?.let { if (it.isNotEmpty()) return it.toList() }

        // Exhaustive check across all bundle keys for any CharSequence / List / Array
        try {
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                if (value is ArrayList<*> && value.isNotEmpty()) {
                    val strings = value.filterIsInstance<CharSequence>().map { it.toString() }
                    if (strings.isNotEmpty()) return strings
                } else if (value is Array<*> && value.isNotEmpty()) {
                    val strings = value.filterIsInstance<CharSequence>().map { it.toString() }
                    if (strings.isNotEmpty()) return strings
                } else if (value is CharSequence && value.isNotBlank() && !key.contains("score", ignoreCase = true)) {
                    return listOf(value.toString())
                }
            }
        } catch (_: Exception) {}

        return emptyList()
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
                consecutiveErrorCount = 0
            }

            override fun onBeginningOfSpeech() {
                _isListening.value = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _rmsLevel.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _rmsLevel.value = 0f
            }

            override fun onError(error: Int) {
                _rmsLevel.value = 0f
                Log.d("SpeechManager", "Speech recognizer error code: $error (continuous: $shouldKeepListening)")

                if (!shouldKeepListening) {
                    _isListening.value = false
                    return
                }

                consecutiveErrorCount++

                // Error 7: ERROR_NO_MATCH, Error 6: ERROR_SPEECH_TIMEOUT, Error 5: ERROR_CLIENT
                val isNormalPause = error == SpeechRecognizer.ERROR_NO_MATCH ||
                        error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
                        error == SpeechRecognizer.ERROR_CLIENT

                if (isNormalPause) {
                    mainHandler.postDelayed({
                        if (shouldKeepListening) {
                            try {
                                speechRecognizer?.cancel()
                                startListeningIntent()
                            } catch (e: Exception) {
                                Log.e("SpeechManager", "Cancel/restart failed", e)
                            }
                        }
                    }, 250)
                    return
                }

                // If language error or server network error, try fallback to system default locale
                if (error == SpeechRecognizer.ERROR_NETWORK ||
                    error == SpeechRecognizer.ERROR_SERVER ||
                    error == 12 || error == 13 || error == 14
                ) {
                    if (currentLocale != "en-US" && currentLocale != Locale.getDefault().toLanguageTag()) {
                        Log.i("SpeechManager", "Falling back from $currentLocale to default locale")
                        currentLocale = Locale.getDefault().toLanguageTag()
                    }
                }

                // For busy or other errors, delay slightly, recreate recognizer if needed, and restart
                val restartDelay = if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) 700L else 400L
                mainHandler.postDelayed({
                    if (shouldKeepListening) {
                        try {
                            if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY || consecutiveErrorCount > 3) {
                                speechRecognizer?.destroy()
                                speechRecognizer = null
                                ensureRecognizerCreated()
                            } else {
                                speechRecognizer?.cancel()
                            }
                            startListeningIntent()
                        } catch (e: Exception) {
                            Log.e("SpeechManager", "Failed to restart after error", e)
                        }
                    }
                }, restartDelay)
            }

            override fun onResults(results: Bundle?) {
                _rmsLevel.value = 0f
                consecutiveErrorCount = 0

                val matches = extractMatches(results)
                val text = matches.firstOrNull()?.trim() ?: ""

                if (text.isNotBlank()) {
                    val currentAcc = _accumulatedTranscript.value.trim()
                    val newAcc = if (currentAcc.isEmpty()) {
                        text
                    } else {
                        "$currentAcc $text"
                    }
                    _accumulatedTranscript.value = newAcc
                    _currentPartial.value = ""
                    _liveTranscript.value = newAcc
                }

                // Immediately continue listening for the next phrase/sentence in continuous mode!
                if (shouldKeepListening) {
                    mainHandler.postDelayed({
                        if (shouldKeepListening) {
                            startListeningIntent()
                        }
                    }, 100)
                } else {
                    _isListening.value = false
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = extractMatches(partialResults)
                val partial = matches.firstOrNull()?.trim() ?: ""
                if (partial.isNotBlank()) {
                    _currentPartial.value = partial
                    val currentAcc = _accumulatedTranscript.value.trim()
                    _liveTranscript.value = if (currentAcc.isEmpty()) partial else "$currentAcc $partial"
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun startListeningIntent() {
        if (!shouldKeepListening) return
        ensureRecognizerCreated()

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLocale)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, currentLocale)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra("android.speech.extra.PARTIAL_RESULTS", true)
                putExtra("android.speech.extra.DICTATION_MODE", true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                // Generous pause allowance (up to 15s) so user can think between words
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 15000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 15000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000L)
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e("SpeechManager", "startListening error", e)
            if (shouldKeepListening) {
                mainHandler.postDelayed({
                    if (shouldKeepListening) startListeningIntent()
                }, 500)
            }
        }
    }

    /**
     * Stops continuous listening and returns the full accumulated transcript.
     */
    fun stopContinuousListening(): String {
        simulationRunnable?.let { mainHandler.removeCallbacks(it) }
        shouldKeepListening = false
        _isListening.value = false
        _rmsLevel.value = 0f

        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e("SpeechManager", "Error in cleanup", e)
            }
        }

        return _liveTranscript.value.trim()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
