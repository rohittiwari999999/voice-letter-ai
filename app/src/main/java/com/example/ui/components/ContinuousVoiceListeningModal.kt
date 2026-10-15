package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.ExecutiveBlueLight
import com.example.ui.theme.ExecutiveNavy
import com.example.ui.theme.PaperBorder

@Composable
fun ContinuousVoiceListeningModal(
    isListening: Boolean,
    rmsLevel: Float,
    liveTranscript: String,
    languageLabel: String,
    errorMessage: String? = null,
    titleText: String = "लाइव वॉइस इनपुट (Live Voice Input)",
    subtitleText: String = "बोलते रहिए, बीच में रुक भी सकते हैं — AI लगातार सुन रहा है।",
    buttonActionLabel: String = "पत्र तैयार करें (Create Letter with AI)",
    onToggleMic: () -> Unit,
    onManualTextChange: (String) -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val rippleScale = remember { Animatable(1f) }

    LaunchedEffect(isListening) {
        if (isListening) {
            rippleScale.animateTo(
                targetValue = 1.35f,
                animationSpec = infiniteRepeatable(
                    animation = tween(850, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            rippleScale.snapTo(1f)
        }
    }

    val sampleVoiceChips = listOf(
        "एक पत्र बनाना है प्रधानाचार्य को 3 दिन की छुट्टी के लिए",
        "बीमारी के कारण अवकाश प्रार्थना पत्र",
        "बैंक मैनेजर को खाता बंद कराने का आवेदन",
        "कंपनी से औपचारिक इस्तीफा पत्र",
        "सड़क व स्ट्रीट लाइट खराब होने की शिकायत",
        "टीसी के लिए प्रार्थना पत्र"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .testTag("continuous_voice_modal")
                .fillMaxWidth(0.95f)
                .padding(vertical = 14.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ExecutiveNavy
                        )
                        Text(
                            text = "भाषा: $languageLabel • लाइव स्पीच मोड",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TOP LIVE SPEECH CARD: Placed prominently at the very top as requested!
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isListening) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
                    ),
                    border = BorderStroke(1.5.dp, if (isListening) Color(0xFFF87171) else Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth()
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isListening) "🔴 आप जो बोल रहे हैं (Live Speech Text):" else "🎙️ बोली गई स्पीच (Spoken Text):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isListening) Color(0xFF991B1B) else Color(0xFF166534)
                                )
                            }

                            if (liveTranscript.isNotBlank()) {
                                Text(
                                    text = "${liveTranscript.length} अक्षर",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large high-contrast live speech text box
                        OutlinedTextField(
                            value = liveTranscript,
                            onValueChange = onManualTextChange,
                            placeholder = {
                                Text(
                                    text = if (isListening)
                                        "बोलते रहिए... आपकी आवाज़ यहाँ तुरंत लाइव टेक्स्ट के रूप में दिखेगी...\n\n(Speaking live... Your spoken words will appear here in real time)"
                                    else
                                        "माइक चालू करने के लिए नीचे माइक दबाएं या सैंपल वाक्यों पर टैप करें...\n\n(Tap mic below to speak or tap a sample phrase)",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 20.sp
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ExecutiveNavy,
                                lineHeight = 22.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isListening) Color(0xFFDC2626) else ExecutiveBlue,
                                unfocusedBorderColor = PaperBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 150.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Microphone pulse indicator with toggle action
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(86.dp)
                ) {
                    if (isListening) {
                        val boost = rmsLevel * 0.35f
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .scale(rippleScale.value + boost)
                                .clip(CircleShape)
                                .background(ExecutiveBlueLight.copy(alpha = 0.22f))
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFFDC2626) else ExecutiveBlue)
                            .clickable(onClick = onToggleMic)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = if (isListening) "Pause Mic" else "Resume Mic",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Text(
                    text = if (isListening) "🟢 माइक चालू है — बोलते रहिए..." else "माइक रुका हुआ है — बोलने के लिए माइक दबाएं",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isListening) Color(0xFF16A34A) else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Quick Tap voice sample chips (allows quick speech testing)
                Text(
                    text = "त्वरित वाक्य (Tap to speak / test):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 6.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 14.dp)
                ) {
                    sampleVoiceChips.forEach { chipText ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.clickable {
                                onManualTextChange(chipText)
                            }
                        ) {
                            Text(
                                text = "💬 $chipText",
                                fontSize = 11.sp,
                                color = ExecutiveBlue,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Action Buttons: Clear & Submit
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onClear,
                        enabled = liveTranscript.isNotBlank(),
                        modifier = Modifier.weight(0.35f)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("साफ़ करें", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = liveTranscript.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .testTag("modal_finish_button")
                            .weight(0.65f)
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(buttonActionLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
