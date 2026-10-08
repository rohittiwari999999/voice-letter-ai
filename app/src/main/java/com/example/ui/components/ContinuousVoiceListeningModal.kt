package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    titleText: String = "Listening Continuously…",
    subtitleText: String = "Speak naturally, pause anytime—I am listening continuously until you tap Done",
    buttonActionLabel: String = "Create Letter with AI",
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
                    animation = tween(900, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            rippleScale.snapTo(1f)
        }
    }

    val sampleVoiceChips = listOf(
        "एक पत्र बनाना है",
        "ये शब्द हटा दो",
        "पिछला वाक्य हटा दो",
        "प्रधानाचार्य को 3 दिन की छुट्टी के लिए",
        "बैंक मैनेजर को खाता बंद कराने हेतु",
        "बीमारी के कारण अवकाश प्रार्थना पत्र",
        "नौकरी के लिए आवेदन पत्र",
        "Delete this word",
        "Write sick leave application to principal"
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
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
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
                            text = "Language: $languageLabel • Continuous Mode",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Pulsing Sound Wave Graphic with interactive toggle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(94.dp)
                ) {
                    if (isListening) {
                        val boost = rmsLevel * 0.35f
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .scale(rippleScale.value + boost)
                                .clip(CircleShape)
                                .background(ExecutiveBlueLight.copy(alpha = 0.22f))
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFFDC2626) else ExecutiveBlue)
                            .clickable(onClick = onToggleMic)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = if (isListening) "Pause Mic" else "Resume Mic",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = if (isListening) "🟢 लाइव सुन रहा है — बोलते रहिए..." else "माइक रुका हुआ है — शुरू करने के लिए माइक दबाएं",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isListening) Color(0xFF16A34A) else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
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

                // Quick tap voice sample chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 10.dp)
                ) {
                    sampleVoiceChips.forEach { chipText ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.clickable {
                                val current = liveTranscript.trim()
                                val updated = if (current.isEmpty()) chipText else "$current $chipText"
                                onManualTextChange(updated)
                            }
                        ) {
                            Text(
                                text = "💬 $chipText",
                                fontSize = 11.sp,
                                color = ExecutiveBlue,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Live Transcription Box (Editable text area)
                OutlinedTextField(
                    value = liveTranscript,
                    onValueChange = onManualTextChange,
                    placeholder = {
                        Text(
                            "आपका बोला हुआ वाक्य यहाँ लाइव दिखेगा…\n\nउदाहरण:\n• \"एक पत्र बनाना है प्रधानाचार्य को 3 दिन की छुट्टी के लिए\"\n• \"बैंक मैनेजर को खाता बंद कराने का प्रार्थना पत्र\"\n• \"सब्जेक्ट को बोल्ड करो और आज की तारीख डालो\"",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExecutiveBlue,
                        unfocusedBorderColor = PaperBorder,
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom toolbar
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onClear,
                        enabled = liveTranscript.isNotBlank()
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", fontSize = 12.sp)
                    }

                    Text(
                        text = "${liveTranscript.length} chars",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Primary Action Button (Finish & Generate)
                Button(
                    onClick = onConfirm,
                    enabled = liveTranscript.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .testTag("modal_finish_button")
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buttonActionLabel,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
