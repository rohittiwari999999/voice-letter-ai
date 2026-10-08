package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LanguageOption
import com.example.data.model.Letter
import com.example.ui.components.A4DocumentCanvas
import com.example.ui.components.ContinuousVoiceListeningModal
import com.example.ui.components.ExportBottomSheet
import com.example.ui.components.FormatToolbar
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.theme.AccentGold
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.ExecutiveNavy
import com.example.ui.theme.PaperBackground
import com.example.ui.theme.PaperBorder
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLetterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterEditorScreen(
    viewModel: VoiceLetterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLetter by viewModel.currentLetter.collectAsStateWithLifecycle()
    val isListening by viewModel.speechManager.isListening.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.speechManager.rmsLevel.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val statusMessage by viewModel.generationStatusMessage.collectAsStateWithLifecycle()
    val voiceLang by viewModel.voiceInputLanguage.collectAsStateWithLifecycle()
    val showContinuousVoiceModal by viewModel.showContinuousVoiceModal.collectAsStateWithLifecycle()
    val lastPromptUsed by viewModel.lastPromptUsed.collectAsStateWithLifecycle()
    val speechError by viewModel.speechManager.errorMessage.collectAsStateWithLifecycle()

    var showExportSheet by remember { mutableStateOf(false) }
    var showTranslateDialog by remember { mutableStateOf(false) }
    var showEditDialogForSection by remember { mutableStateOf<String?>(null) }
    var sectionEditText by remember { mutableStateOf("") }
    var selectedPlaceholderToFill by remember { mutableStateOf<String?>(null) }
    var placeholderReplacementText by remember { mutableStateOf("") }
    var commandInputText by remember { mutableStateOf("") }
    var showTitleEditDialog by remember { mutableStateOf(false) }
    var letterTitleInput by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.openVoiceModal(VoiceLetterViewModel.VoiceTarget.EDIT_EXISTING)
        }
    }

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    if (currentLetter == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ExecutiveBlue)
        }
        return
    }

    val letter = currentLetter!!

    // Edit Section Dialog
    if (showEditDialogForSection != null) {
        AlertDialog(
            onDismissRequest = { showEditDialogForSection = null },
            title = {
                Text(
                    text = "Edit Section",
                    fontWeight = FontWeight.Bold,
                    color = ExecutiveNavy
                )
            },
            text = {
                OutlinedTextField(
                    value = sectionEditText,
                    onValueChange = { sectionEditText = it },
                    label = { Text("Content") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val section = showEditDialogForSection
                        if (section != null) {
                            val updated = when {
                                section == "sender" -> letter.copy(senderDetails = sectionEditText)
                                section == "recipient" -> letter.copy(recipientDetails = sectionEditText)
                                section == "date" -> letter.copy(dateString = sectionEditText)
                                section == "subject" -> letter.copy(subject = sectionEditText)
                                section == "salutation" -> letter.copy(salutation = sectionEditText)
                                section == "close" -> letter.copy(complimentaryClose = sectionEditText)
                                section == "signer" -> letter.copy(signerName = sectionEditText)
                                section == "enclosures" -> letter.copy(enclosures = sectionEditText)
                                section == "letterhead" -> letter.copy(
                                    formatting = letter.formatting.copy(letterheadText = sectionEditText)
                                )
                                section.startsWith("body_") -> {
                                    val idx = section.removePrefix("body_").toIntOrNull() ?: 0
                                    val paras = letter.bodyParagraphs.toMutableList()
                                    if (idx < paras.size) {
                                        paras[idx] = sectionEditText
                                    }
                                    letter.copy(bodyParagraphs = paras)
                                }
                                else -> letter
                            }
                            viewModel.updateCurrentLetter(updated)
                        }
                        showEditDialogForSection = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialogForSection = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Placeholder Replacement Dialog
    if (selectedPlaceholderToFill != null) {
        val ph = selectedPlaceholderToFill!!
        AlertDialog(
            onDismissRequest = { selectedPlaceholderToFill = null },
            title = {
                Text("Fill $ph", fontWeight = FontWeight.Bold, color = ExecutiveNavy)
            },
            text = {
                Column {
                    Text("Enter value to replace across this letter:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = placeholderReplacementText,
                        onValueChange = { placeholderReplacementText = it },
                        placeholder = { Text("e.g. John Doe / 24 October 2026") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (placeholderReplacementText.isNotBlank()) {
                            viewModel.replacePlaceholderInCurrentLetter(ph, placeholderReplacementText)
                        }
                        selectedPlaceholderToFill = null
                        placeholderReplacementText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text("Replace")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlaceholderToFill = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Title Edit Dialog
    if (showTitleEditDialog) {
        AlertDialog(
            onDismissRequest = { showTitleEditDialog = false },
            title = { Text("Rename Letter", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = letterTitleInput,
                    onValueChange = { letterTitleInput = it },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (letterTitleInput.isNotBlank()) {
                            viewModel.updateCurrentLetter(letter.copy(title = letterTitleInput))
                        }
                        showTitleEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTitleEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Language Selector for Translation
    if (showTranslateDialog) {
        LanguageSelectorDialog(
            title = "Translate Letter To",
            currentSelectedCode = letter.letterLanguage,
            onLanguageSelected = { lang ->
                viewModel.translateCurrentLetter(lang)
                showTranslateDialog = false
            },
            onDismissRequest = { showTranslateDialog = false }
        )
    }

    // Export Bottom Sheet
    if (showExportSheet) {
        ExportBottomSheet(
            onExportPdf = {
                showExportSheet = false
                viewModel.exportPdf(context)
            },
            onExportDocx = {
                showExportSheet = false
                viewModel.exportDocx(context)
            },
            onExportTxt = {
                showExportSheet = false
                viewModel.exportTxt(context)
            },
            onShare = {
                showExportSheet = false
                viewModel.shareCurrentLetter(context)
            },
            onPrint = {
                showExportSheet = false
                viewModel.printCurrentLetter(context)
            },
            onCopyText = {
                showExportSheet = false
                viewModel.copyCurrentLetterText(context)
            },
            onDismissRequest = { showExportSheet = false }
        )
    }

    if (showContinuousVoiceModal) {
        ContinuousVoiceListeningModal(
            isListening = isListening,
            rmsLevel = rmsLevel,
            liveTranscript = liveTranscript,
            languageLabel = voiceLang.fullLabel,
            errorMessage = speechError,
            titleText = "Voice Editing & Dictation",
            subtitleText = "Speak instructions, dictations, or corrections—AI will listen continuously until you tap Apply.",
            buttonActionLabel = "Apply Voice Commands",
            onToggleMic = {
                if (isListening) {
                    viewModel.speechManager.stopContinuousListening()
                } else {
                    viewModel.speechManager.startContinuousListening(voiceLang.speechLocale)
                }
            },
            onManualTextChange = {
                viewModel.speechManager.setManualTranscript(it)
            },
            onClear = {
                viewModel.speechManager.clearTranscript()
            },
            onConfirm = {
                viewModel.finishVoiceModalAndProcess()
            },
            onDismiss = {
                viewModel.closeVoiceModal()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            letterTitleInput = letter.title
                            showTitleEditDialog = true
                        }
                    ) {
                        Text(
                            text = letter.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = ExecutiveNavy
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Rename",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ExecutiveNavy
                        )
                    }
                },
                actions = {
                    // Translate Button
                    IconButton(
                        onClick = { showTranslateDialog = true },
                        modifier = Modifier.testTag("editor_translate_button")
                    ) {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = "Translate",
                            tint = ExecutiveBlue
                        )
                    }

                    // Save as Template
                    IconButton(
                        onClick = { viewModel.saveCurrentAsTemplate() },
                        modifier = Modifier.testTag("editor_save_template_button")
                    ) {
                        Icon(
                            Icons.Default.BookmarkAdd,
                            contentDescription = "Save Template",
                            tint = AccentGold
                        )
                    }

                    // Export Button
                    IconButton(
                        onClick = { showExportSheet = true },
                        modifier = Modifier.testTag("editor_export_button")
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Export & Share",
                            tint = ExecutiveBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Live Voice Command & Quick Dictation Bar
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .testTag("editor_bottom_voice_bar")
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Voice prompt suggestions carousel
                    val editorVoiceHints = listOf(
                        "ये शब्द हटा दो",
                        "पिछला वाक्य हटा दो",
                        "सब्जेक्ट बोल्ड करो",
                        "Make the subject bold",
                        "Delete this word",
                        "Delete previous sentence",
                        "Align center",
                        "हिंदी में अनुवाद करो",
                        "Translate to English",
                        "Change font size to 14",
                        "Save as PDF"
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 6.dp)
                    ) {
                        editorVoiceHints.forEach { hint ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.clickable {
                                    if (hint == "Save as PDF") {
                                        viewModel.exportPdf(context)
                                    } else {
                                        viewModel.processVoiceCommand(hint)
                                    }
                                }
                            ) {
                                Text(
                                    text = hint,
                                    fontSize = 11.sp,
                                    color = ExecutiveNavy,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Live mic indicator / command input
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Mic Button
                        IconButton(
                            onClick = {
                                val permCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                )
                                if (permCheck == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.openVoiceModal(VoiceLetterViewModel.VoiceTarget.EDIT_EXISTING)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier
                                .testTag("editor_mic_button")
                                .clip(CircleShape)
                                .background(if (isListening) Color(0xFFDC2626) else ExecutiveBlue)
                                .size(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = if (isListening) "Stop" else "Voice Command",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Text field to type command or dictation
                        OutlinedTextField(
                            value = if (isListening) liveTranscript else commandInputText,
                            onValueChange = { commandInputText = it },
                            placeholder = {
                                Text(
                                    if (isListening) "Listening voice command…" else "Speak or type voice command…",
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ExecutiveBlue,
                                unfocusedBorderColor = PaperBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = {
                                if (commandInputText.isNotBlank()) {
                                    val cmd = commandInputText
                                    commandInputText = ""
                                    viewModel.processVoiceCommand(cmd)
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ExecutiveBlue)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Apply Command",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = PaperBackground,
        modifier = modifier.testTag("letter_editor_screen")
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Formatting Toolbar on top of document
                FormatToolbar(
                    formatOptions = letter.formatting,
                    currentTone = letter.tone,
                    onFormatChange = { viewModel.updateLetterFormatting(it) },
                    onToneChange = { viewModel.adjustTone(it) },
                    onAiGrammarCheck = { viewModel.proofreadLetter() },
                    onAiShorten = { viewModel.processVoiceCommand("Shorten the letter and make it concise") },
                    onAiExpand = { viewModel.processVoiceCommand("Expand the letter with professional supporting details") }
                )

                // Scrollable Document Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    if (lastPromptUsed.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ExecutiveBlue.copy(alpha = 0.12f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardVoice,
                                        contentDescription = null,
                                        tint = ExecutiveBlue,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (letter.letterLanguage == "hi") "आपका वॉइस प्रॉम्प्ट (Spoken Voice Prompt):" else "Spoken Voice Prompt:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF1E40AF),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "\"$lastPromptUsed\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = ExecutiveNavy
                                    )
                                }
                            }
                        }
                    }

                    A4DocumentCanvas(
                        letter = letter,
                        onEditSection = { section ->
                            val currentText = when {
                                section == "sender" -> letter.senderDetails
                                section == "recipient" -> letter.recipientDetails
                                section == "date" -> letter.dateString
                                section == "subject" -> letter.subject
                                section == "salutation" -> letter.salutation
                                section == "close" -> letter.complimentaryClose
                                section == "signer" -> letter.signerName
                                section == "enclosures" -> letter.enclosures
                                section == "letterhead" -> letter.formatting.letterheadText
                                section.startsWith("body_") -> {
                                    val idx = section.removePrefix("body_").toIntOrNull() ?: 0
                                    letter.bodyParagraphs.getOrElse(idx) { "" }
                                }
                                else -> ""
                            }
                            sectionEditText = currentText
                            showEditDialogForSection = section
                        },
                        onPlaceholderClick = { placeholder ->
                            selectedPlaceholderToFill = placeholder
                            placeholderReplacementText = ""
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // AI Loading / Processing Overlay
            AnimatedVisibility(
                visible = isGenerating,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 12.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExecutiveBlue.copy(alpha = 0.2f)),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = ExecutiveBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = statusMessage.ifBlank { "Processing with AI…" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ExecutiveNavy
                        )
                    }
                }
            }
        }
    }
}
