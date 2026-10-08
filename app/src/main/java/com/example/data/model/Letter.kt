package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Letter(
    val id: Long = 0,
    val title: String = "Untitled Letter",
    val letterType: LetterType = LetterType.CUSTOM_LETTER,
    val tone: LetterTone = LetterTone.FORMAL,
    val inputLanguage: String = "en",
    val letterLanguage: String = "en",
    val senderDetails: String = "",
    val dateString: String = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
    val recipientDetails: String = "",
    val subject: String = "",
    val salutation: String = "Respected Sir / Madam,",
    val bodyParagraphs: List<String> = emptyList(),
    val complimentaryClose: String = "Yours faithfully,",
    val signerName: String = "",
    val signerDesignation: String = "",
    val enclosures: String = "",
    val formatting: LetterFormatOptions = LetterFormatOptions(),
    val isTemplate: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Cleans redundant "Subject:", "विषय:", "Regarding", etc. prefixes from subject text.
     */
    fun cleanSubjectText(): String {
        var s = subject.trim()
        var changed = true
        while (changed) {
            changed = false
            val lower = s.lowercase(Locale.ROOT)
            if (lower.startsWith("subject:") || lower.startsWith("subject :") ||
                lower.startsWith("विषय:") || lower.startsWith("विषय :") ||
                lower.startsWith("re:") || lower.startsWith("regarding:")
            ) {
                s = s.substringAfter(":").trim()
                changed = true
            } else if (lower.startsWith("regarding ") || lower.startsWith("regarding\t")) {
                s = s.substring(9).trim()
                changed = true
            } else if (lower.startsWith("application regarding ") || lower.startsWith("letter regarding ")) {
                s = s.substringAfter("regarding").trim()
                changed = true
            } else if (lower.startsWith("के संबंध में:") || lower.startsWith("के संबंध में :")) {
                s = s.substringAfter(":").trim()
                changed = true
            }
        }
        return s.trim()
    }

    /**
     * Assembles all letter components into a full, standard formatted text representation.
     */
    fun assembleFullText(): String {
        val sb = StringBuilder()
        val isHindi = letterLanguage.equals("hi", ignoreCase = true)

        if (formatting.letterheadText.isNotBlank()) {
            sb.append(formatting.letterheadText.trim()).append("\n\n")
        }

        if (senderDetails.isNotBlank()) {
            sb.append(senderDetails.trim()).append("\n\n")
        }

        if (formatting.showHeaderDate && dateString.isNotBlank()) {
            val dateLabel = if (isHindi) "दिनांक: " else "Date: "
            sb.append(dateLabel).append(dateString.trim()).append("\n\n")
        }

        if (recipientDetails.isNotBlank()) {
            val toLabel = if (isHindi) "सेवा में,\n" else "To,\n"
            sb.append(toLabel).append(recipientDetails.trim()).append("\n\n")
        }

        val cleanSub = cleanSubjectText()
        if (cleanSub.isNotBlank()) {
            val subLabel = if (isHindi) "विषय: " else "Subject: "
            sb.append(subLabel).append(cleanSub).append("\n\n")
        }

        if (salutation.isNotBlank()) {
            sb.append(salutation.trim()).append("\n\n")
        }

        if (bodyParagraphs.isNotEmpty()) {
            bodyParagraphs.forEach { para ->
                if (para.isNotBlank()) {
                    sb.append(para.trim()).append("\n\n")
                }
            }
        }

        if (complimentaryClose.isNotBlank()) {
            sb.append(complimentaryClose.trim()).append("\n\n")
        }

        if (signerName.isNotBlank()) {
            sb.append(signerName.trim()).append("\n")
        }
        if (signerDesignation.isNotBlank()) {
            sb.append(signerDesignation.trim()).append("\n")
        }

        if (enclosures.isNotBlank()) {
            val encLabel = if (isHindi) "\nसंलग्नक: " else "\nEnclosures: "
            sb.append(encLabel).append(enclosures.trim()).append("\n")
        }

        if (formatting.footerNote.isNotBlank()) {
            sb.append("\n").append(formatting.footerNote.trim())
        }

        return sb.toString()
    }

    /**
     * Scans for placeholder tokens like [NAME], [DATE], [PRINCIPAL NAME], [ACCOUNT NUMBER], etc.
     */
    fun extractPlaceholders(): List<String> {
        val regex = Regex("\\[([A-Z0-9 _\\-/]+)\\]")
        val fullText = assembleFullText()
        return regex.findAll(fullText).map { it.value }.distinct().toList()
    }

    /**
     * Replaces a specific placeholder across all fields of the letter.
     */
    fun replacePlaceholder(placeholder: String, replacement: String): Letter {
        return copy(
            senderDetails = senderDetails.replace(placeholder, replacement),
            recipientDetails = recipientDetails.replace(placeholder, replacement),
            subject = subject.replace(placeholder, replacement),
            salutation = salutation.replace(placeholder, replacement),
            bodyParagraphs = bodyParagraphs.map { it.replace(placeholder, replacement) },
            signerName = signerName.replace(placeholder, replacement),
            signerDesignation = signerDesignation.replace(placeholder, replacement),
            enclosures = enclosures.replace(placeholder, replacement),
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Removes a specific word or phrase across body paragraphs and subject.
     */
    fun removeWord(target: String): Letter {
        val cleanTarget = target.trim()
        if (cleanTarget.isBlank()) return this
        val regex = Regex("(?i)\\b${Regex.escape(cleanTarget)}\\b")
        val cleanSubject = regex.replace(subject, "").replace(Regex("\\s+"), " ").trim()
        val cleanParas = bodyParagraphs.map { para ->
            regex.replace(para, "").replace(Regex("\\s+"), " ").replace(Regex("\\s+([.,!?।])"), "$1").trim()
        }.filter { it.isNotBlank() }

        return copy(
            subject = if (cleanSubject.isBlank()) subject else cleanSubject,
            bodyParagraphs = if (cleanParas.isEmpty()) bodyParagraphs else cleanParas,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Removes the last sentence or last line of the letter body.
     */
    fun removeLastSentence(): Letter {
        if (bodyParagraphs.isEmpty()) return this
        val mutableParas = bodyParagraphs.toMutableList()
        val lastPara = mutableParas.last().trim()

        val sentences = lastPara.split(Regex("(?<=[.!?।])\\s+")).filter { it.isNotBlank() }
        if (sentences.size > 1) {
            mutableParas[mutableParas.size - 1] = sentences.dropLast(1).joinToString(" ")
        } else {
            if (mutableParas.size > 1) {
                mutableParas.removeAt(mutableParas.size - 1)
            } else {
                val words = lastPara.split(Regex("\\s+"))
                if (words.size > 3) {
                    mutableParas[0] = words.dropLast(3).joinToString(" ")
                }
            }
        }
        return copy(bodyParagraphs = mutableParas, updatedAt = System.currentTimeMillis())
    }

    /**
     * Removes the last word of the letter body.
     */
    fun removeLastWord(): Letter {
        if (bodyParagraphs.isEmpty()) return this
        val mutableParas = bodyParagraphs.toMutableList()
        val lastPara = mutableParas.last().trim()
        val words = lastPara.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size > 1) {
            mutableParas[mutableParas.size - 1] = words.dropLast(1).joinToString(" ")
        } else if (mutableParas.size > 1) {
            mutableParas.removeAt(mutableParas.size - 1)
        }
        return copy(bodyParagraphs = mutableParas, updatedAt = System.currentTimeMillis())
    }

    /**
     * Replaces target word or phrase with replacement across the letter.
     */
    fun replaceWord(target: String, replacement: String): Letter {
        val cleanTarget = target.trim()
        if (cleanTarget.isBlank()) return this
        val regex = Regex("(?i)\\b${Regex.escape(cleanTarget)}\\b")
        val newSubject = regex.replace(subject, replacement)
        val newParas = bodyParagraphs.map { regex.replace(it, replacement) }
        return copy(
            subject = newSubject,
            bodyParagraphs = newParas,
            updatedAt = System.currentTimeMillis()
        )
    }
}
