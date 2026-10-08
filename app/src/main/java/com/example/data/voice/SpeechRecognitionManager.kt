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

    private fun ensureRecognizerCreated() {
        if (speechRecognizer != null) return

        try {
            if (!isAvailable) {
                Log.w("SpeechManager", "Speech recognition unavailable on this device/emulator.")
                _errorMessage.value = "Speech recognition service unavailable on device."
                return
            }

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        } catch (e: Exception) {
            Log.e("SpeechManager", "Error creating SpeechRecognizer", e)
            _errorMessage.value = "Unable to initialize speech recognizer: ${e.message}"
        }
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
                // In continuous mode, silence or pauses trigger these. DO NOT STOP LISTENING!
                val isNormalPause = error == SpeechRecognizer.ERROR_NO_MATCH ||
                        error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
                        error == SpeechRecognizer.ERROR_CLIENT

                if (isNormalPause) {
                    mainHandler.postDelayed({
                        if (shouldKeepListening) {
                            speechRecognizer?.cancel()
                            startListeningIntent()
                        }
                    }, 200)
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

                // For busy or other errors, delay slightly and restart
                val restartDelay = if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) 600L else 400L
                mainHandler.postDelayed({
                    if (shouldKeepListening) {
                        try {
                            speechRecognizer?.cancel()
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

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()?.trim() ?: ""

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

                // Immediately continue listening for the next phrase/sentence!
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
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull()?.trim() ?: ""
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
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLocale)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, currentLocale)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                // Generous pause allowance (up to 15s) so user can think between words
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 15000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 15000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 4000L)
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
