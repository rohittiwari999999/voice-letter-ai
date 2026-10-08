package com.example.data.export

import android.content.Context
import com.example.data.model.Letter
import com.example.data.model.LetterAlignment
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DocxExporter {

    fun exportToDocx(context: Context, letter: Letter): File {
        val cleanTitle = letter.title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(30)
        val file = File(context.cacheDir, "${cleanTitle}_${System.currentTimeMillis()}.docx")

        ZipOutputStream(FileOutputStream(file)).use { zos ->
            // 1. [Content_Types].xml
            putZipEntry(zos, "[Content_Types].xml", CONTENT_TYPES_XML)

            // 2. _rels/.rels
            putZipEntry(zos, "_rels/.rels", ROOT_RELS_XML)

            // 3. word/_rels/document.xml.rels
            putZipEntry(zos, "word/_rels/document.xml.rels", WORD_RELS_XML)

            // 4. word/styles.xml
            putZipEntry(zos, "word/styles.xml", STYLES_XML)

            // 5. word/document.xml
            val docXml = generateDocumentXml(letter)
            putZipEntry(zos, "word/document.xml", docXml)
        }

        return file
    }

    private fun putZipEntry(zos: ZipOutputStream, path: String, content: String) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        val bytes = content.toByteArray(Charsets.UTF_8)
        zos.write(bytes, 0, bytes.size)
        zos.closeEntry()
    }

    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun generateDocumentXml(letter: Letter): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">""")
        sb.append("<w:body>")

        val alignCode = when (letter.formatting.alignment) {
            LetterAlignment.CENTER -> "center"
            LetterAlignment.RIGHT -> "right"
            LetterAlignment.JUSTIFY -> "both"
            else -> "left"
        }

        // Half-points (e.g. 13.5sp -> 27)
        val fontSizeHalfPoints = (letter.formatting.fontSizeSp * 2).toInt()

        fun appendParagraph(
            text: String,
            isBold: Boolean = false,
            isItalic: Boolean = false,
            isUnderline: Boolean = false,
            customAlign: String? = null
        ) {
            val lines = text.split("\n")
            sb.append("<w:p>")
            sb.append("<w:pPr>")
            sb.append("""<w:jc w:val="${customAlign ?: alignCode}"/>""")
            sb.append("""<w:spacing w:after="200" w:line="276" w:lineRule="auto"/>""")
            sb.append("</w:pPr>")

            for ((index, line) in lines.withIndex()) {
                if (line.isNotEmpty()) {
                    sb.append("<w:r>")
                    sb.append("<w:rPr>")
                    if (isBold) sb.append("<w:b/>")
                    if (isItalic) sb.append("<w:i/>")
                    if (isUnderline) sb.append("""<w:u w:val="single"/>""")
                    sb.append("""<w:sz w:val="$fontSizeHalfPoints"/>""")
                    sb.append("""<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:cs="Nirmala UI"/>""")
                    sb.append("</w:rPr>")
                    sb.append("""<w:t xml:space="preserve">${escapeXml(line)}</w:t>""")
                    sb.append("</w:r>")
                }
                if (index < lines.size - 1) {
                    sb.append("<w:r><w:br/></w:r>")
                }
            }
            sb.append("</w:p>")
        }

        if (letter.formatting.letterheadText.isNotBlank()) {
            appendParagraph(letter.formatting.letterheadText.trim(), isBold = true, customAlign = "center")
        }

        if (letter.senderDetails.isNotBlank()) {
            appendParagraph(letter.senderDetails.trim(), isBold = letter.formatting.isSenderBold)
        }

        val isHindi = letter.letterLanguage.equals("hi", ignoreCase = true)

        if (letter.formatting.showHeaderDate && letter.dateString.isNotBlank()) {
            val dateLabel = if (isHindi) "दिनांक: " else "Date: "
            appendParagraph("$dateLabel${letter.dateString.trim()}")
        }

        if (letter.recipientDetails.isNotBlank()) {
            val toText = if (isHindi) {
                if (letter.recipientDetails.trim().startsWith("सेवा में")) letter.recipientDetails.trim() else "सेवा में,\n${letter.recipientDetails.trim()}"
            } else {
                if (letter.recipientDetails.trim().startsWith("To,")) letter.recipientDetails.trim() else "To,\n${letter.recipientDetails.trim()}"
            }
            appendParagraph(toText)
        }

        val cleanSub = letter.cleanSubjectText()
        if (cleanSub.isNotBlank()) {
            val subLabel = if (isHindi) "विषय: " else "Subject: "
            appendParagraph(
                "$subLabel$cleanSub",
                isBold = letter.formatting.isSubjectBold,
                isUnderline = letter.formatting.isSubjectUnderlined
            )
        }

        if (letter.salutation.isNotBlank()) {
            appendParagraph(letter.salutation.trim())
        }

        letter.bodyParagraphs.forEach { para ->
            if (para.isNotBlank()) {
                appendParagraph(para.trim(), isItalic = letter.formatting.isItalicBody)
            }
        }

        if (letter.complimentaryClose.isNotBlank()) {
            appendParagraph(letter.complimentaryClose.trim())
        }

        val signerText = buildString {
            if (letter.signerName.isNotBlank()) append(letter.signerName.trim()).append("\n")
            if (letter.signerDesignation.isNotBlank()) append(letter.signerDesignation.trim())
        }
        if (signerText.isNotBlank()) {
            appendParagraph(signerText, isBold = true)
        }

        if (letter.enclosures.isNotBlank()) {
            appendParagraph("Enclosures:\n${letter.enclosures.trim()}")
        }

        if (letter.formatting.footerNote.isNotBlank()) {
            appendParagraph(letter.formatting.footerNote.trim(), isItalic = true)
        }

        // Section properties with A4 page size
        sb.append("<w:sectPr>")
        sb.append("""<w:pgSz w:w="11906" w:h="16838"/>""") // A4 in dxa (twips: 1/20 of a pt)
        sb.append("""<w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/>""")
        sb.append("</w:sectPr>")

        sb.append("</w:body>")
        sb.append("</w:document>")

        return sb.toString()
    }

    private const val CONTENT_TYPES_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>"""

    private const val ROOT_RELS_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

    private const val WORD_RELS_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    private const val STYLES_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rPr>
        <w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:cs="Nirmala UI"/>
        <w:sz w:val="24"/>
        <w:lang w:val="en-US"/>
      </w:rPr>
    </w:rPrDefault>
  </w:docDefaults>
</w:styles>"""
}
