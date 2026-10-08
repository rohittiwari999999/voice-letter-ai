package com.example.data.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.model.Letter
import com.example.data.model.LetterAlignment
import com.example.data.model.LetterFontFamily
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    // A4 dimensions at standard 72 DPI (points)
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842

    fun exportToPdf(context: Context, letter: Letter): File {
        val pdfDocument = PdfDocument()

        val marginHorizontal = when (letter.formatting.margin.name) {
            "COMPACT" -> 40
            "WIDE" -> 70
            else -> 54 // Standard
        }
        val marginTop = 50
        val marginBottom = 50
        val printableWidth = PAGE_WIDTH - (marginHorizontal * 2)
        val printableHeight = PAGE_HEIGHT - marginTop - marginBottom

        val typeface = when (letter.formatting.fontFamily) {
            LetterFontFamily.SERIF -> Typeface.SERIF
            LetterFontFamily.MONOSPACE -> Typeface.MONOSPACE
            LetterFontFamily.SANS_SERIF -> Typeface.SANS_SERIF
        }

        val textPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = letter.formatting.fontSizeSp
            this.typeface = typeface
            isAntiAlias = true
        }

        val boldPaint = TextPaint(textPaint).apply {
            this.typeface = Typeface.create(typeface, Typeface.BOLD)
        }

        val italicPaint = TextPaint(textPaint).apply {
            this.typeface = Typeface.create(typeface, Typeface.ITALIC)
        }

        // Measure and prepare sections to draw
        data class RenderBlock(val text: String, val paint: TextPaint, val isSpacingAfter: Boolean = true, val isSubject: Boolean = false)
        val blocks = mutableListOf<RenderBlock>()

        if (letter.formatting.letterheadText.isNotBlank()) {
            blocks.add(RenderBlock(letter.formatting.letterheadText.trim(), boldPaint))
        }

        if (letter.senderDetails.isNotBlank()) {
            blocks.add(RenderBlock(letter.senderDetails.trim(), if (letter.formatting.isSenderBold) boldPaint else textPaint))
        }

        val isHindi = letter.letterLanguage.equals("hi", ignoreCase = true)

        if (letter.formatting.showHeaderDate && letter.dateString.isNotBlank()) {
            val dateLabel = if (isHindi) "दिनांक: " else "Date: "
            blocks.add(RenderBlock("$dateLabel${letter.dateString.trim()}", textPaint))
        }

        if (letter.recipientDetails.isNotBlank()) {
            val toText = if (isHindi) {
                if (letter.recipientDetails.trim().startsWith("सेवा में")) letter.recipientDetails.trim() else "सेवा में,\n${letter.recipientDetails.trim()}"
            } else {
                if (letter.recipientDetails.trim().startsWith("To,")) letter.recipientDetails.trim() else "To,\n${letter.recipientDetails.trim()}"
            }
            blocks.add(RenderBlock(toText, textPaint))
        }

        val cleanSub = letter.cleanSubjectText()
        if (cleanSub.isNotBlank()) {
            val subLabel = if (isHindi) "विषय: " else "Subject: "
            blocks.add(RenderBlock("$subLabel$cleanSub", if (letter.formatting.isSubjectBold) boldPaint else textPaint, isSubject = true))
        }

        if (letter.salutation.isNotBlank()) {
            blocks.add(RenderBlock(letter.salutation.trim(), textPaint))
        }

        letter.bodyParagraphs.forEach { para ->
            if (para.isNotBlank()) {
                blocks.add(RenderBlock(para.trim(), if (letter.formatting.isItalicBody) italicPaint else textPaint))
            }
        }

        if (letter.complimentaryClose.isNotBlank()) {
            blocks.add(RenderBlock(letter.complimentaryClose.trim(), textPaint, isSpacingAfter = false))
        }

        val signerText = buildString {
            if (letter.signerName.isNotBlank()) append(letter.signerName.trim()).append("\n")
            if (letter.signerDesignation.isNotBlank()) append(letter.signerDesignation.trim())
        }
        if (signerText.isNotBlank()) {
            blocks.add(RenderBlock(signerText, boldPaint))
        }

        if (letter.enclosures.isNotBlank()) {
            blocks.add(RenderBlock("Enclosures: ${letter.enclosures.trim()}", textPaint))
        }

        if (letter.formatting.footerNote.isNotBlank()) {
            blocks.add(RenderBlock(letter.formatting.footerNote.trim(), italicPaint))
        }

        val alignment = when (letter.formatting.alignment) {
            LetterAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
            LetterAlignment.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_NORMAL
        }

        // Layout creation helper
        fun createLayout(text: String, paint: TextPaint): StaticLayout {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(text, 0, text.length, paint, printableWidth)
                    .setAlignment(alignment)
                    .setLineSpacing(0f, letter.formatting.lineSpacingMultiplier)
                    .setIncludePad(true)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(text, paint, printableWidth, alignment, letter.formatting.lineSpacingMultiplier, 0f, true)
            }
        }

        val layouts = blocks.map { createLayout(it.text, it.paint) to it }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var currentY = marginTop.toFloat()

        fun finishAndNewPage() {
            // Draw page number footer if enabled
            if (letter.formatting.showPageNumber) {
                val footerPaint = Paint().apply {
                    color = Color.DKGRAY
                    textSize = 9f
                    this.typeface = Typeface.SERIF
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("- Page $pageNumber -", PAGE_WIDTH / 2f, (PAGE_HEIGHT - 25).toFloat(), footerPaint)
            }

            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            currentY = marginTop.toFloat()
        }

        for ((layout, block) in layouts) {
            val layoutHeight = layout.height
            val spacing = letter.formatting.paragraphSpacingDp.toFloat()

            if (currentY + layoutHeight > PAGE_HEIGHT - marginBottom) {
                finishAndNewPage()
            }

            canvas.save()
            canvas.translate(marginHorizontal.toFloat(), currentY)
            layout.draw(canvas)
            canvas.restore()

            // If subject has underline formatting
            if (block.isSubject && letter.formatting.isSubjectUnderlined) {
                val linePaint = Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 1f
                }
                canvas.drawLine(
                    marginHorizontal.toFloat(),
                    currentY + layoutHeight + 2f,
                    marginHorizontal.toFloat() + layout.getLineWidth(0).coerceAtMost(printableWidth.toFloat()),
                    currentY + layoutHeight + 2f,
                    linePaint
                )
            }

            currentY += layoutHeight + spacing
        }

        // Draw final page footer
        if (letter.formatting.showPageNumber) {
            val footerPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 9f
                this.typeface = Typeface.SERIF
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("- Page $pageNumber -", PAGE_WIDTH / 2f, (PAGE_HEIGHT - 25).toFloat(), footerPaint)
        }

        pdfDocument.finishPage(page)

        // Write to file
        val cleanTitle = letter.title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(30)
        val file = File(context.cacheDir, "${cleanTitle}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return file
    }
}
