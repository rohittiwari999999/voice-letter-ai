package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Letter
import com.example.data.model.LetterAlignment
import com.example.data.model.LetterFontFamily
import com.example.ui.theme.AccentGold
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PaperBackground
import com.example.ui.theme.PaperBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun A4DocumentCanvas(
    letter: Letter,
    onEditSection: (String) -> Unit,
    onPlaceholderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = when (letter.formatting.fontFamily) {
        LetterFontFamily.SERIF -> FontFamily.Serif
        LetterFontFamily.MONOSPACE -> FontFamily.Monospace
        LetterFontFamily.SANS_SERIF -> FontFamily.SansSerif
    }

    val textAlign = when (letter.formatting.alignment) {
        LetterAlignment.CENTER -> TextAlign.Center
        LetterAlignment.RIGHT -> TextAlign.End
        LetterAlignment.JUSTIFY -> TextAlign.Justify
        else -> TextAlign.Start
    }

    val paddingH = letter.formatting.margin.paddingDp.dp
    val placeholders = letter.extractPlaceholders()

    Column(
        modifier = modifier
            .testTag("a4_document_canvas")
            .fillMaxWidth()
    ) {
        // Placeholders notification chip row if any unfilled placeholders exist
        if (placeholders.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Placeholders",
                            tint = AccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val isCanvasHindi = letter.letterLanguage.equals("hi", ignoreCase = true)
                        Text(
                            text = if (isCanvasHindi) "रिक्त स्थान भरें (${placeholders.size}) - भरने हेतु टैप करें:" else "Unfilled placeholders (${placeholders.size}) - Tap to fill:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        placeholders.forEach { ph ->
                            SuggestionChip(
                                onClick = { onPlaceholderClick(ph) },
                                label = { Text(ph, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color.White,
                                    labelColor = Color(0xFFB45309)
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = Color(0xFFFCD34D)
                                ),
                                modifier = Modifier.padding(end = 6.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // A4 Paper Sheet View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(4.dp), spotColor = Color(0x33000000))
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(1.dp, PaperBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = paddingH, vertical = 36.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Letterhead if present
                if (letter.formatting.letterheadText.isNotBlank()) {
                    Text(
                        text = letter.formatting.letterheadText,
                        fontFamily = fontFamily,
                        fontSize = (letter.formatting.fontSizeSp + 3f).sp,
                        fontWeight = FontWeight.Bold,
                        color = ExecutiveBlue,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("letterhead") }
                            .padding(bottom = 8.dp)
                    )
                    HorizontalDivider(color = PaperBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sender Details
                if (letter.senderDetails.isNotBlank()) {
                    Text(
                        text = letter.senderDetails,
                        fontFamily = fontFamily,
                        fontSize = letter.formatting.fontSizeSp.sp,
                        fontWeight = if (letter.formatting.isSenderBold) FontWeight.Bold else FontWeight.Normal,
                        color = InkPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("sender") }
                            .padding(bottom = 12.dp)
                    )
                }

                val isHindi = letter.letterLanguage.equals("hi", ignoreCase = true)

                // Header Date
                if (letter.formatting.showHeaderDate && letter.dateString.isNotBlank()) {
                    val datePrefix = if (isHindi) "दिनांक: " else "Date: "
                    Text(
                        text = "$datePrefix${letter.dateString}",
                        fontFamily = fontFamily,
                        fontSize = letter.formatting.fontSizeSp.sp,
                        color = InkSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("date") }
                            .padding(bottom = 14.dp)
                    )
                }

                // Recipient Details
                if (letter.recipientDetails.isNotBlank()) {
                    val recipientFormatted = if (isHindi) {
                        if (letter.recipientDetails.trim().startsWith("सेवा में")) letter.recipientDetails else "सेवा में,\n${letter.recipientDetails}"
                    } else {
                        if (letter.recipientDetails.trim().startsWith("To,")) letter.recipientDetails else "To,\n${letter.recipientDetails}"
                    }
                    Text(
                        text = recipientFormatted,
                        fontFamily = fontFamily,
                        fontSize = letter.formatting.fontSizeSp.sp,
                        color = InkPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("recipient") }
                            .padding(bottom = 14.dp)
                    )
                }

                // Subject Line (Cleaned)
                val cleanSub = letter.cleanSubjectText()
                if (cleanSub.isNotBlank()) {
                    val subPrefix = if (isHindi) "विषय: " else "Subject: "
                    Text(
                        text = "$subPrefix$cleanSub",
                        fontFamily = fontFamily,
                        fontSize = (letter.formatting.fontSizeSp + 0.5f).sp,
                        fontWeight = if (letter.formatting.isSubjectBold) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (letter.formatting.isSubjectUnderlined) TextDecoration.Underline else TextDecoration.None,
                        color = InkPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("subject") }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .background(if (letter.formatting.isSubjectBold) Color(0xFFF1F5F9) else Color.Transparent)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Salutation
                if (letter.salutation.isNotBlank()) {
                    Text(
                        text = letter.salutation,
                        fontFamily = fontFamily,
                        fontSize = letter.formatting.fontSizeSp.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("salutation") }
                            .padding(bottom = 12.dp)
                    )
                }

                // Body Paragraphs
                letter.bodyParagraphs.forEachIndexed { index, paragraph ->
                    if (paragraph.isNotBlank()) {
                        Text(
                            text = paragraph,
                            fontFamily = fontFamily,
                            fontSize = letter.formatting.fontSizeSp.sp,
                            fontStyle = if (letter.formatting.isItalicBody) FontStyle.Italic else FontStyle.Normal,
                            lineHeight = (letter.formatting.fontSizeSp * letter.formatting.lineSpacingMultiplier).sp,
                            textAlign = textAlign,
                            color = InkPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEditSection("body_$index") }
                                .padding(bottom = letter.formatting.paragraphSpacingDp.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Complimentary Close
                if (letter.complimentaryClose.isNotBlank()) {
                    Text(
                        text = letter.complimentaryClose,
                        fontFamily = fontFamily,
                        fontSize = letter.formatting.fontSizeSp.sp,
                        color = InkPrimary,
                        modifier = Modifier
                            .clickable { onEditSection("close") }
                            .padding(bottom = 16.dp)
                    )
                }

                // Signature Area
                if (letter.formatting.showSignatureLine) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(1.dp)
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (letter.signerName.isNotBlank() || letter.signerDesignation.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .clickable { onEditSection("signer") }
                            .padding(bottom = 12.dp)
                    ) {
                        if (letter.signerName.isNotBlank()) {
                            Text(
                                text = letter.signerName,
                                fontFamily = fontFamily,
                                fontSize = letter.formatting.fontSizeSp.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                        }
                        if (letter.signerDesignation.isNotBlank()) {
                            Text(
                                text = letter.signerDesignation,
                                fontFamily = fontFamily,
                                fontSize = (letter.formatting.fontSizeSp - 1f).sp,
                                color = InkSecondary
                            )
                        }
                    }
                }

                // Enclosures
                if (letter.enclosures.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val encHeader = if (isHindi) "संलग्नक (Enclosures):\n" else "Enclosures:\n"
                    Text(
                        text = "$encHeader${letter.enclosures}",
                        fontFamily = fontFamily,
                        fontSize = (letter.formatting.fontSizeSp - 1f).sp,
                        color = InkSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSection("enclosures") }
                    )
                }

                // Footer Note
                if (letter.formatting.footerNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = PaperBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = letter.formatting.footerNote,
                        fontFamily = fontFamily,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = InkMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
