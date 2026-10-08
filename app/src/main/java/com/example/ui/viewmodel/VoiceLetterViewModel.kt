package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiLetterGenerator
import com.example.data.ai.GeminiClient
import com.example.data.export.DocxExporter
import com.example.data.export.FileShareHelper
import com.example.data.export.PdfExporter
import com.example.data.local.AppDatabase
import com.example.data.model.LanguageOption
import com.example.data.model.Letter
import com.example.data.model.LetterFormatOptions
import com.example.data.model.LetterTone
import com.example.data.model.TemplateItem
import com.example.data.repository.LetterRepository
import com.example.data.voice.SpeechRecognitionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class AppScreen {
    HOME,
    EDITOR,
    DOCUMENTS,
    TEMPLATES
}

class VoiceLetterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LetterRepository
    val speechManager: SpeechRecognitionManager

    init {
        val database = AppDatabase.getInstance(application)
        repository = LetterRepository(database.letterDao())
        speechManager = SpeechRecognitionManager(application)
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active Letter
    private val _currentLetter = MutableStateFlow<Letter?>(null)
    val currentLetter: StateFlow<Letter?> = _currentLetter.asStateFlow()

    // Loading & Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStatusMessage = MutableStateFlow("")
    val generationStatusMessage: StateFlow<String> = _generationStatusMessage.asStateFlow()

    // Languages (Default to Hindi as requested by user, with 20+ other languages supported)
    private val _voiceInputLanguage = MutableStateFlow(LanguageOption.DEFAULT_HINDI)
    val voiceInputLanguage: StateFlow<LanguageOption> = _voiceInputLanguage.asStateFlow()

    private val _letterOutputLanguage = MutableStateFlow(LanguageOption.DEFAULT_HINDI)
    val letterOutputLanguage: StateFlow<LanguageOption> = _letterOutputLanguage.asStateFlow()

    // Database Streams
    val savedDocuments: StateFlow<List<Letter>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLetters: StateFlow<List<Letter>> = repository.getRecentLetters(6)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customTemplates: StateFlow<List<Letter>> = repository.customTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Last Prompt Used
    private val _lastPromptUsed = MutableStateFlow("")
    val lastPromptUsed: StateFlow<String> = _lastPromptUsed.asStateFlow()

    // Custom API Key
    private val _apiKeyInput = MutableStateFlow(GeminiClient.getEffectiveApiKey())
    val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

    fun updateCustomApiKey(key: String) {
        _apiKeyInput.value = key
        GeminiClient.customApiKey = key
        val prefs = getApplication<Application>().getSharedPreferences("voiceletter_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("gemini_api_key", key).apply()
    }

    // Continuous Voice Modal State
    private val _showContinuousVoiceModal = MutableStateFlow(false)
    val showContinuousVoiceModal: StateFlow<Boolean> = _showContinuousVoiceModal.asStateFlow()

    private val _voiceSessionTarget = MutableStateFlow<VoiceTarget>(VoiceTarget.CREATE_NEW)
    val voiceSessionTarget: StateFlow<VoiceTarget> = _voiceSessionTarget.asStateFlow()

    enum class VoiceTarget {
        CREATE_NEW,
        EDIT_EXISTING
    }

    fun openVoiceModal(target: VoiceTarget = VoiceTarget.CREATE_NEW) {
        _voiceSessionTarget.value = target
        _showContinuousVoiceModal.value = true
        speechManager.startContinuousListening(_voiceInputLanguage.value.speechLocale)
    }

    fun closeVoiceModal() {
        speechManager.stopContinuousListening()
        _showContinuousVoiceModal.value = false
    }

    fun finishVoiceModalAndProcess() {
        val transcript = speechManager.stopContinuousListening()
        _showContinuousVoiceModal.value = false

        if (transcript.isNotBlank()) {
            when (_voiceSessionTarget.value) {
                VoiceTarget.CREATE_NEW -> {
                    generateLetterFromVoice(transcript)
                }
                VoiceTarget.EDIT_EXISTING -> {
                    processVoiceCommand(transcript)
                }
            }
        }
    }

    // Navigation
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setVoiceInputLanguage(lang: LanguageOption, syncLetterOutput: Boolean = true) {
        _voiceInputLanguage.value = lang
        if (syncLetterOutput) {
            _letterOutputLanguage.value = lang
        }
    }

    fun setLetterOutputLanguage(lang: LanguageOption) {
        _letterOutputLanguage.value = lang
    }

    fun applyQuickPrompt(prompt: String) {
        speechManager.setManualTranscript(prompt)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Direct Voice Trigger
    fun startHomeVoiceInput(onComplete: (String) -> Unit) {
        openVoiceModal(VoiceTarget.CREATE_NEW)
    }

    fun stopVoiceInput() {
        speechManager.stopContinuousListening()
    }

    // Letter Generation Flow
    fun generateLetterFromVoice(prompt: String) {
        if (prompt.isBlank()) return
        _lastPromptUsed.value = prompt.trim()
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatusMessage.value = "AI is understanding instructions and formatting letter…"
            try {
                val letter = AiLetterGenerator.generateLetter(
                    prompt = prompt,
                    targetLanguageCode = _letterOutputLanguage.value.code,
                    voiceInputLanguageCode = _voiceInputLanguage.value.code
                )
                // Save initially to database
                val savedId = repository.saveLetter(letter)
                val fullLetter = letter.copy(id = savedId)
                _currentLetter.value = fullLetter
                _currentScreen.value = AppScreen.EDITOR
            } catch (e: Exception) {
                Log.e("VoiceLetterVM", "Failed to generate letter", e)
            } finally {
                _isGenerating.value = false
                _generationStatusMessage.value = ""
            }
        }
    }

    fun loadTemplate(templateItem: TemplateItem) {
        viewModelScope.launch {
            val initial = templateItem.initialLetter.copy(
                letterLanguage = _letterOutputLanguage.value.code,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val id = repository.saveLetter(initial)
            _currentLetter.value = initial.copy(id = id)
            _currentScreen.value = AppScreen.EDITOR
        }
    }

    fun createNewBlankLetter() {
        viewModelScope.launch {
            val blank = Letter(
                title = "New Letter",
                letterLanguage = _letterOutputLanguage.value.code,
                bodyParagraphs = listOf("I am writing to formally request...")
            )
            val id = repository.saveLetter(blank)
            _currentLetter.value = blank.copy(id = id)
            _currentScreen.value = AppScreen.EDITOR
        }
    }

    fun openExistingLetter(letter: Letter) {
        _currentLetter.value = letter
        _currentScreen.value = AppScreen.EDITOR
    }

    // In-Editor Modifications
    fun updateCurrentLetter(updatedLetter: Letter) {
        _currentLetter.value = updatedLetter
        viewModelScope.launch {
            repository.updateLetter(updatedLetter)
        }
    }

    fun updateLetterFormatting(options: LetterFormatOptions) {
        _currentLetter.value?.let { letter ->
            val updated = letter.copy(formatting = options, updatedAt = System.currentTimeMillis())
            updateCurrentLetter(updated)
        }
    }

    fun replacePlaceholderInCurrentLetter(placeholder: String, replacement: String) {
        _currentLetter.value?.let { letter ->
            val updated = letter.replacePlaceholder(placeholder, replacement)
            updateCurrentLetter(updated)
        }
    }

    fun processVoiceCommand(command: String) {
        val letter = _currentLetter.value ?: return
        if (command.isBlank()) return

        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatusMessage.value = "Processing voice instruction: \"$command\""
            try {
                val updated = AiLetterGenerator.processVoiceEdit(
                    currentLetter = letter,
                    command = command,
                    targetLanguageCode = _letterOutputLanguage.value.code
                )
                updateCurrentLetter(updated)
            } catch (e: Exception) {
                Log.e("VoiceLetterVM", "Error executing voice command", e)
            } finally {
                _isGenerating.value = false
                _generationStatusMessage.value = ""
            }
        }
    }

    fun translateCurrentLetter(targetLang: LanguageOption) {
        val letter = _currentLetter.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatusMessage.value = "Translating letter to ${targetLang.displayName}…"
            try {
                val translated = AiLetterGenerator.translateLetter(letter, targetLang.code)
                _letterOutputLanguage.value = targetLang
                updateCurrentLetter(translated)
            } catch (e: Exception) {
                Log.e("VoiceLetterVM", "Translation error", e)
            } finally {
                _isGenerating.value = false
                _generationStatusMessage.value = ""
            }
        }
    }

    fun adjustTone(tone: LetterTone) {
        val letter = _currentLetter.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatusMessage.value = "Adapting letter to ${tone.label} tone…"
            try {
                val updated = AiLetterGenerator.adjustTone(letter, tone)
                updateCurrentLetter(updated)
            } catch (e: Exception) {
                Log.e("VoiceLetterVM", "Tone adjustment error", e)
            } finally {
                _isGenerating.value = false
                _generationStatusMessage.value = ""
            }
        }
    }

    fun proofreadLetter() {
        val letter = _currentLetter.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatusMessage.value = "AI is proofreading grammar and phrasing…"
            try {
                val updated = AiLetterGenerator.proofreadLetter(letter)
                updateCurrentLetter(updated)
            } catch (e: Exception) {
                Log.e("VoiceLetterVM", "Proofreading error", e)
            } finally {
                _isGenerating.value = false
                _generationStatusMessage.value = ""
            }
        }
    }

    fun saveCurrentAsTemplate() {
        val letter = _currentLetter.value ?: return
        viewModelScope.launch {
            val templateVersion = letter.copy(
                id = 0,
                isTemplate = true,
                title = "${letter.title} (Template)",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveLetter(templateVersion)
            Toast.makeText(getApplication(), "Saved as custom template!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteLetter(id: Long) {
        viewModelScope.launch {
            repository.deleteLetter(id)
            if (_currentLetter.value?.id == id) {
                _currentLetter.value = null
                _currentScreen.value = AppScreen.HOME
            }
        }
    }

    // Export Helpers
    fun exportPdf(context: Context): File? {
        val letter = _currentLetter.value ?: return null
        return try {
            val file = PdfExporter.exportToPdf(context, letter)
            Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show()
            file
        } catch (e: Exception) {
            Toast.makeText(context, "PDF export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun exportDocx(context: Context): File? {
        val letter = _currentLetter.value ?: return null
        return try {
            val file = DocxExporter.exportToDocx(context, letter)
            Toast.makeText(context, "DOCX generated successfully!", Toast.LENGTH_SHORT).show()
            file
        } catch (e: Exception) {
            Toast.makeText(context, "DOCX export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun exportTxt(context: Context): File? {
        val letter = _currentLetter.value ?: return null
        return try {
            val file = FileShareHelper.exportToTxt(context, letter)
            Toast.makeText(context, "Text file exported!", Toast.LENGTH_SHORT).show()
            file
        } catch (e: Exception) {
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun shareCurrentLetter(context: Context) {
        val letter = _currentLetter.value ?: return
        val pdfFile = exportPdf(context)
        if (pdfFile != null) {
            FileShareHelper.shareFile(context, pdfFile, "application/pdf", letter.title)
        }
    }

    fun printCurrentLetter(context: Context) {
        val letter = _currentLetter.value ?: return
        val pdfFile = exportPdf(context)
        if (pdfFile != null) {
            FileShareHelper.printPdf(context, pdfFile, letter.title)
        }
    }

    fun copyCurrentLetterText(context: Context) {
        val letter = _currentLetter.value ?: return
        FileShareHelper.copyToClipboard(context, letter.assembleFullText(), letter.title)
    }
}
