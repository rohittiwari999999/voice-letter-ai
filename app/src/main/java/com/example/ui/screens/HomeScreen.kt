package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Letter
import com.example.ui.components.ContinuousVoiceListeningModal
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.VoiceMicWaveform
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.ExecutiveBlueLight
import com.example.ui.theme.ExecutiveNavy
import com.example.ui.theme.PaperBackground
import com.example.ui.theme.PaperBorder
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLetterViewModel

@Composable
fun HomeScreen(
    viewModel: VoiceLetterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val voiceLang by viewModel.voiceInputLanguage.collectAsStateWithLifecycle()
    val letterLang by viewModel.letterOutputLanguage.collectAsStateWithLifecycle()
    val isListening by viewModel.speechManager.isListening.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.speechManager.rmsLevel.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val statusMessage by viewModel.generationStatusMessage.collectAsStateWithLifecycle()
    val recentLetters by viewModel.recentLetters.collectAsStateWithLifecycle()
    val showContinuousVoiceModal by viewModel.showContinuousVoiceModal.collectAsStateWithLifecycle()
    val speechError by viewModel.speechManager.errorMessage.collectAsStateWithLifecycle()

    var showVoiceLangDialog by remember { mutableStateOf(false) }
    var showLetterLangDialog by remember { mutableStateOf(false) }
    var manualPromptInput by remember { mutableStateOf("") }
    var showTextInputDialog by remember { mutableStateOf(false) }

    // Audio permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.openVoiceModal(VoiceLetterViewModel.VoiceTarget.CREATE_NEW)
        }
    }

    val sampleVoiceCommands = listOf(
        "एक पत्र बनाना है (औपचारिक प्रार्थना पत्र)",
        "बीमारी के कारण 3 दिन की छुट्टी के लिए प्रार्थना पत्र",
        "बैंक मैनेजर को खाता बंद कराने का पत्र",
        "प्रधानाचार्य को 3 दिन की छुट्टी का प्रार्थना पत्र",
        "कंपनी से औपचारिक त्यागपत्र (Resignation Letter)",
        "सड़क व स्ट्रीट लाइट खराब होने की शिकायत",
        "Write sick leave application to principal",
        "Make letter to bank manager for account closure"
    )

    if (showVoiceLangDialog) {
        LanguageSelectorDialog(
            title = "Select Voice Input Language",
            currentSelectedCode = voiceLang.code,
            onLanguageSelected = {
                viewModel.setVoiceInputLanguage(it, syncLetterOutput = true)
                showVoiceLangDialog = false
            },
            onDismissRequest = { showVoiceLangDialog = false }
        )
    }

    if (showLetterLangDialog) {
        LanguageSelectorDialog(
            title = "Select Letter Output Language",
            currentSelectedCode = letterLang.code,
            onLanguageSelected = {
                viewModel.setLetterOutputLanguage(it)
                showLetterLangDialog = false
            },
            onDismissRequest = { showLetterLangDialog = false }
        )
    }

    if (showContinuousVoiceModal) {
        ContinuousVoiceListeningModal(
            isListening = isListening,
            rmsLevel = rmsLevel,
            liveTranscript = liveTranscript,
            languageLabel = voiceLang.fullLabel,
            errorMessage = speechError,
            titleText = "Speak Naturally & Create Letter",
            subtitleText = "बोलते रहिए, बीच में रुक भी सकते हैं — AI लगातार सुन रहा है। पूरा होने पर टैप करें।",
            buttonActionLabel = "Create Letter with AI",
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
        containerColor = PaperBackground,
        modifier = modifier.testTag("home_screen")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ExecutiveBlue,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "VoiceLetter AI",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "VoiceLetter AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ExecutiveNavy
                            )
                            Text(
                                text = "Speak naturally • Instant professional letters",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val isHindi = letterLang.code.equals("hi", ignoreCase = true)

                    // Multilingual Language Selectors Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        AssistChip(
                            onClick = { showVoiceLangDialog = true },
                            leadingIcon = {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "Speak in", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                            },
                            label = { Text(if (isHindi) "बोलें: ${voiceLang.displayName}" else "Speak: ${voiceLang.displayName}", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White,
                                labelColor = ExecutiveNavy
                            ),
                            border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = PaperBorder)
                        )

                        AssistChip(
                            onClick = { showLetterLangDialog = true },
                            leadingIcon = {
                                Icon(Icons.Default.Translate, contentDescription = "Letter in", tint = AccentTeal, modifier = Modifier.size(16.dp))
                            },
                            label = { Text(if (isHindi) "पत्र: ${letterLang.displayName}" else "Letter in: ${letterLang.displayName}", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White,
                                labelColor = ExecutiveNavy
                            ),
                            border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = PaperBorder)
                        )
                    }
                }
            }

            // Big Microphone Voice Card Hero with Prominent Top Live Speech Box
            item {
                val isHindi = letterLang.code.equals("hi", ignoreCase = true)
                ElevatedCard(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (isListening)
                                (if (isHindi) "🔴 लाइव सुन रहा है — बोलते रहिए..." else "🔴 Listening… Speak now")
                            else
                                (if (isHindi) "बोलें और पत्र तैयार करें" else "Speak & Create Letter"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isListening) Color(0xFFDC2626) else ExecutiveBlue
                        )
                        Text(
                            text = if (isListening)
                                (if (isHindi) "आप जो बोलेंगे वह नीचे लाइव टेक्स्ट के रूप में प्रकट होगा" else "Say what you need — words appear live below as text")
                            else
                                (if (isHindi) "माइक दबाकर बोलें — आपके शब्द यहाँ तुरंत लाइव टेक्स्ट के रूप में दिखेंगे" else "Tap microphone — spoken words will appear live as text right here"),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // 🔴 PROMINENT LIVE SPEECH MONITOR BOX (Placed above so user sees spoken text live!)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isListening) Color(0xFFFEF2F2) else Color(0xFFF8FAFC)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isListening) Color(0xFFF87171) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_live_speech_box")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (isListening) Color(0xFFDC2626) else Color(0xFF16A34A))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isListening)
                                                "🔴 लाइव आवाज़ टेक्स्ट (Live Speech Text):"
                                            else
                                                "🎙️ बोली गई आवाज़ टेक्स्ट (Live Speech Transcript):",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isListening) Color(0xFF991B1B) else ExecutiveNavy
                                        )
                                    }

                                    if (liveTranscript.isNotBlank()) {
                                        Text(
                                            text = "साफ़ करें",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.clickable { viewModel.speechManager.clearTranscript() }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PaperBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        if (isGenerating) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    strokeWidth = 2.dp,
                                                    color = ExecutiveBlue
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = statusMessage.ifBlank { "AI पत्र तैयार कर रहा है..." },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = ExecutiveBlue,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        } else if (liveTranscript.isNotBlank()) {
                                            Text(
                                                text = "\"$liveTranscript\"",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ExecutiveNavy,
                                                lineHeight = 22.sp
                                            )
                                        } else {
                                            Text(
                                                text = if (isListening)
                                                    "बोलते रहिए... आपकी आवाज़ यहाँ लाइव टेक्स्ट के रूप में प्रकट होगी..."
                                                else
                                                    "माइक दबाकर बोलें — आपकी बोली हुई स्पीच यहाँ तुरंत टेक्स्ट के रूप में लाइव दिखेगी...",
                                                fontSize = 13.sp,
                                                color = Color(0xFF94A3B8),
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }

                                // Quick action button if transcript is available
                                if (liveTranscript.isNotBlank() && !isGenerating) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.generateLetterFromVoice(liveTranscript) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("generate_letter_from_speech_button")
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHindi) "इस बोली गई आवाज़ से पत्र बनाएं" else "Create Letter from Spoken Text",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Animated Mic Waveform Button
                        VoiceMicWaveform(
                            isListening = isListening,
                            rmsLevel = rmsLevel,
                            onClick = {
                                val permCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                )
                                if (permCheck == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.openVoiceModal(VoiceLetterViewModel.VoiceTarget.CREATE_NEW)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            size = 76.dp
                        )

                        // Alternative Direct Text Input bar
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = manualPromptInput,
                                onValueChange = { manualPromptInput = it },
                                placeholder = {
                                    Text(
                                        if (isHindi) "या यहाँ निर्देश लिखें (जैसे: छुट्टी के लिए पत्र)..." else "Or type instructions here…",
                                        fontSize = 13.sp
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ExecutiveBlue,
                                    unfocusedBorderColor = PaperBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (manualPromptInput.isNotBlank()) {
                                        val prompt = manualPromptInput
                                        manualPromptInput = ""
                                        viewModel.generateLetterFromVoice(prompt)
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ExecutiveBlue)
                                    .size(48.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Prompt",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Voice Command Examples Carousel
            item {
                val isHindi = letterLang.code.equals("hi", ignoreCase = true)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isHindi) "बोलकर देखें (Tap to test voice):" else "Try Saying… (Tap to Generate):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ExecutiveNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        sampleVoiceCommands.forEach { cmd ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, PaperBorder),
                                modifier = Modifier
                                    .clickable { viewModel.generateLetterFromVoice(cmd) }
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardVoice,
                                        contentDescription = "Sample",
                                        tint = AccentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cmd,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ExecutiveNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Action Grid: Type Letter, Templates, My Documents
            item {
                val isHindi = letterLang.code.equals("hi", ignoreCase = true)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionTile(
                        icon = Icons.Default.EditNote,
                        iconTint = ExecutiveBlue,
                        title = if (isHindi) "पत्र लिखें" else "Type Letter",
                        subtitle = if (isHindi) "नया पत्र" else "Blank document",
                        modifier = Modifier.weight(1f),
                        testTag = "type_letter_button",
                        onClick = { viewModel.createNewBlankLetter() }
                    )

                    ActionTile(
                        icon = Icons.Default.Description,
                        iconTint = AccentGold,
                        title = if (isHindi) "प्रारूप" else "Templates",
                        subtitle = if (isHindi) "तैयार प्रारूप" else "Ready formats",
                        modifier = Modifier.weight(1f),
                        testTag = "templates_button",
                        onClick = { viewModel.navigateTo(AppScreen.TEMPLATES) }
                    )

                    ActionTile(
                        icon = Icons.Default.Folder,
                        iconTint = AccentTeal,
                        title = if (isHindi) "सहेजे गए पत्र" else "My Documents",
                        subtitle = if (isHindi) "दस्तावेज़ सूची" else "Saved letters",
                        modifier = Modifier.weight(1f),
                        testTag = "my_documents_button",
                        onClick = { viewModel.navigateTo(AppScreen.DOCUMENTS) }
                    )
                }
            }

            // Recent Letters Section
            item {
                val isHindi = letterLang.code.equals("hi", ignoreCase = true)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isHindi) "हाल के पत्र (Recent Letters)" else "Recent Letters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ExecutiveNavy
                    )
                    Text(
                        text = if (isHindi) "सभी देखें" else "View All",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ExecutiveBlue,
                        modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.DOCUMENTS) }
                    )
                }
            }

            if (recentLetters.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No letters created yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "Tap the mic above or pick a template to begin",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            } else {
                items(recentLetters) { letter ->
                    RecentLetterItemCard(
                        letter = letter,
                        onClick = { viewModel.openExistingLetter(letter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = ExecutiveNavy
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun RecentLetterItemCard(
    letter: Letter,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ExecutiveBlue.copy(alpha = 0.08f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = ExecutiveBlue,
                    modifier = Modifier
                        .padding(9.dp)
                        .size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = letter.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ExecutiveNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (letter.subject.isNotBlank()) letter.subject else letter.letterType.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${letter.dateString} • ${letter.tone.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentTeal
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
