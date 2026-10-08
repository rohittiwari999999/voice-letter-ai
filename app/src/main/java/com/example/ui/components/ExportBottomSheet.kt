package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExecutiveBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    onExportPdf: () -> Unit,
    onExportDocx: () -> Unit,
    onExportTxt: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit,
    onCopyText: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .testTag("export_bottom_sheet")
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Export & Share Letter",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ExecutiveBlue
            )
            Text(
                text = "Choose your preferred professional format or action",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // PDF Option
            ExportOptionRow(
                icon = Icons.Default.PictureAsPdf,
                iconTint = Color(0xFFDC2626),
                title = "Export as PDF (A4 Document)",
                subtitle = "Preserves page layout, margins, Unicode fonts, and formatting",
                testTag = "export_pdf_button",
                onClick = onExportPdf
            )

            // DOCX Option
            ExportOptionRow(
                icon = Icons.Default.Description,
                iconTint = Color(0xFF2563EB),
                title = "Export as Microsoft Word (.docx)",
                subtitle = "Editable document with native Word headings and paragraph styles",
                testTag = "export_docx_button",
                onClick = onExportDocx
            )

            // TXT Option
            ExportOptionRow(
                icon = Icons.AutoMirrored.Filled.TextSnippet,
                iconTint = Color(0xFF0D9488),
                title = "Export as Plain Text (.txt)",
                subtitle = "Clean unicode plain text suitable for email or chat",
                testTag = "export_txt_button",
                onClick = onExportTxt
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

            // Share
            ExportOptionRow(
                icon = Icons.Default.Share,
                iconTint = ExecutiveBlue,
                title = "Share Document",
                subtitle = "Send via WhatsApp, Gmail, Drive, or any app",
                testTag = "share_letter_button",
                onClick = onShare
            )

            // Print
            ExportOptionRow(
                icon = Icons.Default.Print,
                iconTint = Color(0xFF475569),
                title = "Print Document",
                subtitle = "Send directly to wireless printer or Google Cloud Print",
                testTag = "print_letter_button",
                onClick = onPrint
            )

            // Copy Text
            ExportOptionRow(
                icon = Icons.Default.ContentCopy,
                iconTint = Color(0xFFD97706),
                title = "Copy Full Text",
                subtitle = "Copy formatted letter text directly to clipboard",
                testTag = "copy_text_button",
                onClick = onCopyText
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ExportOptionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconTint.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
