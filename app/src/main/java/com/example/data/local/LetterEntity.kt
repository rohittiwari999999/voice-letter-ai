package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Letter
import com.example.data.model.LetterAlignment
import com.example.data.model.LetterFontFamily
import com.example.data.model.LetterFormatOptions
import com.example.data.model.LetterMargin
import com.example.data.model.LetterTone
import com.example.data.model.LetterType

@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val letterType: String,
    val tone: String,
    val inputLanguage: String,
    val letterLanguage: String,
    val senderDetails: String,
    val dateString: String,
    val recipientDetails: String,
    val subject: String,
    val salutation: String,
    val bodyParagraphsSeparated: String, // paragraphs joined by \n---PARA---\n
    val complimentaryClose: String,
    val signerName: String,
    val signerDesignation: String,
    val enclosures: String,
    val fontFamily: String,
    val fontSizeSp: Float,
    val lineSpacingMultiplier: Float,
    val alignment: String,
    val margin: String,
    val isSubjectBold: Boolean,
    val isSubjectUnderlined: Boolean,
    val isSenderBold: Boolean,
    val isItalicBody: Boolean,
    val showPageNumber: Boolean,
    val showSignatureLine: Boolean,
    val showHeaderDate: Boolean,
    val letterheadText: String,
    val footerNote: String,
    val isTemplate: Boolean,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toLetter(): Letter {
        val paras = if (bodyParagraphsSeparated.isBlank()) {
            emptyList()
        } else {
            bodyParagraphsSeparated.split("\n---PARA---\n")
        }

        val formatOptions = LetterFormatOptions(
            fontFamily = runCatching { LetterFontFamily.valueOf(fontFamily) }.getOrDefault(LetterFontFamily.SERIF),
            fontSizeSp = fontSizeSp,
            lineSpacingMultiplier = lineSpacingMultiplier,
            alignment = runCatching { LetterAlignment.valueOf(alignment) }.getOrDefault(LetterAlignment.LEFT),
            margin = runCatching { LetterMargin.valueOf(margin) }.getOrDefault(LetterMargin.STANDARD),
            isSubjectBold = isSubjectBold,
            isSubjectUnderlined = isSubjectUnderlined,
            isSenderBold = isSenderBold,
            isItalicBody = isItalicBody,
            showPageNumber = showPageNumber,
            showSignatureLine = showSignatureLine,
            showHeaderDate = showHeaderDate,
            letterheadText = letterheadText,
            footerNote = footerNote
        )

        return Letter(
            id = id,
            title = title,
            letterType = LetterType.fromString(letterType),
            tone = LetterTone.fromString(tone),
            inputLanguage = inputLanguage,
            letterLanguage = letterLanguage,
            senderDetails = senderDetails,
            dateString = dateString,
            recipientDetails = recipientDetails,
            subject = subject,
            salutation = salutation,
            bodyParagraphs = paras,
            complimentaryClose = complimentaryClose,
            signerName = signerName,
            signerDesignation = signerDesignation,
            enclosures = enclosures,
            formatting = formatOptions,
            isTemplate = isTemplate,
            isFavorite = isFavorite,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromLetter(letter: Letter): LetterEntity {
            return LetterEntity(
                id = letter.id,
                title = letter.title,
                letterType = letter.letterType.name,
                tone = letter.tone.name,
                inputLanguage = letter.inputLanguage,
                letterLanguage = letter.letterLanguage,
                senderDetails = letter.senderDetails,
                dateString = letter.dateString,
                recipientDetails = letter.recipientDetails,
                subject = letter.subject,
                salutation = letter.salutation,
                bodyParagraphsSeparated = letter.bodyParagraphs.joinToString("\n---PARA---\n"),
                complimentaryClose = letter.complimentaryClose,
                signerName = letter.signerName,
                signerDesignation = letter.signerDesignation,
                enclosures = letter.enclosures,
                fontFamily = letter.formatting.fontFamily.name,
                fontSizeSp = letter.formatting.fontSizeSp,
                lineSpacingMultiplier = letter.formatting.lineSpacingMultiplier,
                alignment = letter.formatting.alignment.name,
                margin = letter.formatting.margin.name,
                isSubjectBold = letter.formatting.isSubjectBold,
                isSubjectUnderlined = letter.formatting.isSubjectUnderlined,
                isSenderBold = letter.formatting.isSenderBold,
                isItalicBody = letter.formatting.isItalicBody,
                showPageNumber = letter.formatting.showPageNumber,
                showSignatureLine = letter.formatting.showSignatureLine,
                showHeaderDate = letter.formatting.showHeaderDate,
                letterheadText = letter.formatting.letterheadText,
                footerNote = letter.formatting.footerNote,
                isTemplate = letter.isTemplate,
                isFavorite = letter.isFavorite,
                createdAt = letter.createdAt,
                updatedAt = letter.updatedAt
            )
        }
    }
}
