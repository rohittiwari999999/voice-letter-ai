package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LetterAlignment
import com.example.data.model.LetterFontFamily
import com.example.data.model.LetterFormatOptions
import com.example.data.model.LetterMargin
import com.example.data.model.LetterTone
import com.example.ui.theme.ExecutiveBlue

@Composable
fun FormatToolbar(
    formatOptions: LetterFormatOptions,
    currentTone: LetterTone,
    onFormatChange: (LetterFormatOptions) -> Unit,
    onToneChange: (LetterTone) -> Unit,
    onAiGrammarCheck: () -> Unit,
    onAiShorten: () -> Unit,
    onAiExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = modifier
            .testTag("format_toolbar")
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // First Row: Typography & Alignment controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp)
            ) {
                // Bold Subject
                FilterChip(
                    selected = formatOptions.isSubjectBold,
                    onClick = {
                        onFormatChange(formatOptions.copy(isSubjectBold = !formatOptions.isSubjectBold))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold Subject", modifier = Modifier.size(16.dp))
                    },
                    label = { Text("Bold Subject", fontSize = 12.sp) }
                )

                // Underline Subject
                FilterChip(
                    selected = formatOptions.isSubjectUnderlined,
                    onClick = {
                        onFormatChange(formatOptions.copy(isSubjectUnderlined = !formatOptions.isSubjectUnderlined))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline Subject", modifier = Modifier.size(16.dp))
                    },
                    label = { Text("Underline", fontSize = 12.sp) }
                )

                // Font Family switcher
                FilterChip(
                    selected = formatOptions.fontFamily == LetterFontFamily.SERIF,
                    onClick = {
                        val next = when (formatOptions.fontFamily) {
                            LetterFontFamily.SERIF -> LetterFontFamily.SANS_SERIF
                            LetterFontFamily.SANS_SERIF -> LetterFontFamily.MONOSPACE
                            LetterFontFamily.MONOSPACE -> LetterFontFamily.SERIF
                        }
                        onFormatChange(formatOptions.copy(fontFamily = next))
                    },
                    label = { Text("Font: ${formatOptions.fontFamily.name}", fontSize = 12.sp) }
                )

                // Font size buttons
                IconButton(
                    onClick = {
                        if (formatOptions.fontSizeSp > 11f) {
                            onFormatChange(formatOptions.copy(fontSizeSp = formatOptions.fontSizeSp - 1f))
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("A-", fontSize = 13.sp)
                }

                Text("${formatOptions.fontSizeSp.toInt()}pt", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

                IconButton(
                    onClick = {
                        if (formatOptions.fontSizeSp < 18f) {
                            onFormatChange(formatOptions.copy(fontSizeSp = formatOptions.fontSizeSp + 1f))
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("A+", fontSize = 13.sp)
                }

                // Alignments
                IconButton(
                    onClick = { onFormatChange(formatOptions.copy(alignment = LetterAlignment.LEFT)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.FormatAlignLeft,
                        contentDescription = "Left",
                        tint = if (formatOptions.alignment == LetterAlignment.LEFT) ExecutiveBlue else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = { onFormatChange(formatOptions.copy(alignment = LetterAlignment.CENTER)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.FormatAlignCenter,
                        contentDescription = "Center",
                        tint = if (formatOptions.alignment == LetterAlignment.CENTER) ExecutiveBlue else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = { onFormatChange(formatOptions.copy(alignment = LetterAlignment.JUSTIFY)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.FormatAlignJustify,
                        contentDescription = "Justify",
                        tint = if (formatOptions.alignment == LetterAlignment.JUSTIFY) ExecutiveBlue else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Margins
                FilterChip(
                    selected = false,
                    onClick = {
                        val nextMargin = when (formatOptions.margin) {
                            LetterMargin.STANDARD -> LetterMargin.WIDE
                            LetterMargin.WIDE -> LetterMargin.COMPACT
                            LetterMargin.COMPACT -> LetterMargin.STANDARD
                        }
                        onFormatChange(formatOptions.copy(margin = nextMargin))
                    },
                    label = { Text("Margin: ${formatOptions.margin.name}", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Second Row: AI Tone & Grammar tools
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp)
            ) {
                // AI Grammar Check button
                FilterChip(
                    selected = false,
                    onClick = onAiGrammarCheck,
                    leadingIcon = {
                        Icon(Icons.Default.Spellcheck, contentDescription = "Proofread", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("AI Grammar Check", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFFEFF6FF))
                )

                // AI Shorten
                FilterChip(
                    selected = false,
                    onClick = onAiShorten,
                    leadingIcon = {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Shorten", tint = ExecutiveBlue, modifier = Modifier.size(14.dp))
                    },
                    label = { Text("Shorten", fontSize = 12.sp) }
                )

                // AI Expand
                FilterChip(
                    selected = false,
                    onClick = onAiExpand,
                    leadingIcon = {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Expand", tint = ExecutiveBlue, modifier = Modifier.size(14.dp))
                    },
                    label = { Text("Expand Details", fontSize = 12.sp) }
                )

                // Tone Chips
                LetterTone.entries.forEach { tone ->
                    FilterChip(
                        selected = currentTone == tone,
                        onClick = { onToneChange(tone) },
                        label = { Text(tone.label, fontSize = 12.sp) }
                    )
                }
            }
        }
    }
}
