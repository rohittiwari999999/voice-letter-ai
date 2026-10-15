package com.example.data.ai

import android.util.Log
import com.example.data.model.LanguageOption
import com.example.data.model.Letter
import com.example.data.model.LetterAlignment
import com.example.data.model.LetterFontFamily
import com.example.data.model.LetterFormatOptions
import com.example.data.model.LetterMargin
import com.example.data.model.LetterTone
import com.example.data.model.LetterType
import com.example.data.model.TemplateItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AiLetterGenerator {
    private const val TAG = "AiLetterGenerator"

    private const val SYSTEM_INSTRUCTION = """
You are VoiceLetter AI, an elite multilingual letter-writing engine.
Analyze user voice instructions and generate a complete, structured, ready-to-use formal letter.

CRITICAL RULES:
1. STRICT FILTERING OF CONVERSATIONAL INTENT:
   Users often speak conversationally, e.g. "ek letter banana hai", "mujhe ek letter likhna hai", "write a letter saying that I have a fever", "ek letter bana do".
   YOU MUST STRIP ALL conversational phrases like "ek letter banana hai", "ek letter likh do", "write a letter stating", "application bana do", "mujhe ek patra likhna hai".
   NEVER include meta-instructions like "ek letter banana hai" in the letter body or subject!
   If the user only said "ek letter banana hai" without stating a reason, generate a clean, elegant formal application letter for urgent matter with clear placeholders.
2. STRICT LANGUAGE INTEGRITY:
   If the target language is Hindi ("hi"), EVERY SINGLE WORD of the letter (Sender, Recipient, Date, Subject, Salutation, Body paragraphs, Closing, Signer) MUST be in authentic formal HINDI (हिन्दी - Devanagari script). DO NOT MIX English into a Hindi letter!
   If the target language is English ("en"), the letter must be in authentic formal English.
3. SUBJECT FORMAT:
   Return ONLY the clean subject text (e.g. "बीमारी के कारण अवकाश हेतु प्रार्थना पत्र" or "Application for sick leave due to fever"). DO NOT prefix it with "Subject:" or "विषय:" because the app UI formats that automatically!
4. DO NOT invent sensitive private facts (names, phone numbers, account numbers, roll numbers). When details are not provided in the prompt, mark them clearly as uppercase bracketed placeholders like [आपका नाम] or [YOUR NAME], [कक्षा] or [CLASS], [रोल नंबर] or [ROLL NO], [विद्यालय का नाम] or [SCHOOL NAME], [बैंक खाता संख्या] or [BANK A/C NO], [दिनांक] or [DATE], [शहर] or [CITY].
5. IN-STREAM EDITING, CORRECTIONS & DELETIONS (MULTILINGUAL - ALL LANGUAGES):
   If the user dictates speech or issues voice commands containing deletion or correction instructions in ANY language:
   Examples:
   - "ye shabd delete kar do" / "ye shabd hata do" / "delete this word" / "remove this word"
   - "[word] delete kar do" / "[word] hata do" / "[word] mita do" / "delete [word]" / "remove [word]"
   - "pichla vakya hata do" / "aakhiri line hata do" / "delete previous sentence" / "remove last sentence"
   - "Monday nahi Tuesday" / "replace Monday with Tuesday" / "Monday ki jagah Tuesday kar do"
   - Regional & world languages: "X kete dao" (Bengali), "X kadhun taka" (Marathi), "X theeseyandi" (Telugu), "X neekavum" (Tamil), "borrar X" (Spanish), "supprimer X" (French), etc.
   YOU MUST EXECUTE THE REQUESTED DELETION/CORRECTION ACCURATELY!
   NEVER include the command phrases (e.g. "ye shabd delete kar do", "hata do", "delete that") in the letter output text.
6. Return ONLY a single raw JSON object with keys:
{
  "title": "Title of Letter",
  "letterType": "LEAVE_APPLICATION",
  "tone": "RESPECTFUL",
  "senderDetails": "...",
  "dateString": "...",
  "recipientDetails": "...",
  "subject": "Clean subject text without Subject: prefix",
  "salutation": "...",
  "bodyParagraphs": ["Para 1...", "Para 2..."],
  "complimentaryClose": "...",
  "signerName": "...",
  "signerDesignation": "...",
  "enclosures": "",
  "formatting": {
    "isSubjectBold": true,
    "alignment": "LEFT"
  }
}
"""

    suspend fun generateLetter(
        prompt: String,
        targetLanguageCode: String = "hi",
        voiceInputLanguageCode: String = "hi"
    ): Letter {
        val targetLang = LanguageOption.findByCode(targetLanguageCode)
        val cleanedTopic = cleanUserConversationalFluff(prompt)

        val promptPayload = """
User Voice Prompt: "$prompt"
Extracted Core Intent Topic: "${if (cleanedTopic.isBlank()) "General Formal Request / Application" else cleanedTopic}"
Target Output Language for the Letter: ${targetLang.displayName} (${targetLang.nativeName}, code: ${targetLang.code})
Input Audio Language: $voiceInputLanguageCode

Generate the complete structured letter entirely in ${targetLang.displayName}.
CRITICAL: Ensure zero conversational meta-phrases like "ek letter banana hai" appear in the letter text.
If target language is Hindi (${targetLang.code} == 'hi'), the ENTIRE letter MUST be written in Devanagari Hindi.
"""

        val result = GeminiClient.generateContent(promptPayload, SYSTEM_INSTRUCTION)
        if (result.isSuccess) {
            val rawResponse = result.getOrNull() ?: ""
            val parsed = parseLetterJson(rawResponse, cleanedTopic, targetLanguageCode)
            if (parsed != null) {
                return parsed
            }
        }

        // Smart local rule-based letter engine with full native Hindi and English generators
        Log.i(TAG, "Using smart local multilingual rule-based letter builder")
        return smartLocalFallback(prompt, targetLanguageCode)
    }

    suspend fun processVoiceEdit(
        currentLetter: Letter,
        command: String,
        targetLanguageCode: String
    ): Letter {
        val cleanCmd = command.trim().lowercase(Locale.ROOT)

        // Instant local formatting checks
        if (cleanCmd.contains("subject bold") || cleanCmd.contains("make the subject bold") || cleanCmd.contains("बोल्ड करो") || cleanCmd.contains("बोल्ड")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(isSubjectBold = true),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("subject normal") || cleanCmd.contains("unbold subject")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(isSubjectBold = false),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("center") || cleanCmd.contains("align center") || cleanCmd.contains("सेंटर")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(alignment = LetterAlignment.CENTER),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("justify") || cleanCmd.contains("align justify")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(alignment = LetterAlignment.JUSTIFY),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("left align") || cleanCmd.contains("align left")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(alignment = LetterAlignment.LEFT),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("font size 14") || cleanCmd.contains("size 14") || cleanCmd.contains("फॉन्ट 14")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(fontSizeSp = 14f),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("font size 16") || cleanCmd.contains("size 16") || cleanCmd.contains("फॉन्ट 16")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(fontSizeSp = 16f),
                updatedAt = System.currentTimeMillis()
            )
        }
        if (cleanCmd.contains("underline") || cleanCmd.contains("अंडरलाइन")) {
            return currentLetter.copy(
                formatting = currentLetter.formatting.copy(isSubjectUnderlined = true),
                updatedAt = System.currentTimeMillis()
            )
        }

        // 1. "ye shabd delete kar do" / "ye shabd hata do" / "delete this word" / "remove this word"
        if (cleanCmd.contains("ye shabd delete") || cleanCmd.contains("ye shabd hata") ||
            cleanCmd.contains("yeh shabd delete") || cleanCmd.contains("yeh shabd hata") ||
            cleanCmd.contains("delete this word") || cleanCmd.contains("remove this word") ||
            cleanCmd.contains("delete the word") || cleanCmd.contains("remove the word") ||
            cleanCmd.contains("is shabd ko hata") || cleanCmd.contains("is shabd ko delete") ||
            cleanCmd.contains("pichla shabd hata") || cleanCmd.contains("pichla shabd delete") ||
            cleanCmd.contains("delete last word") || cleanCmd.contains("delete previous word") ||
            cleanCmd.contains("ye word delete") || cleanCmd.contains("ye word hata") ||
            cleanCmd.contains("is word ko hata") || cleanCmd.contains("shabd hata do")
        ) {
            return currentLetter.removeLastWord()
        }

        // 2. Sentence / line deletion: "pichla vakya hata do" / "last line hata do" / "delete previous sentence"
        if (cleanCmd.contains("pichla sentence") || cleanCmd.contains("pichla vakya") ||
            cleanCmd.contains("last sentence") || cleanCmd.contains("previous sentence") ||
            cleanCmd.contains("last line") || cleanCmd.contains("aakhiri line") ||
            cleanCmd.contains("aakhri line") || cleanCmd.contains("remove last line") ||
            cleanCmd.contains("delete last line") || cleanCmd.contains("delete the last sentence") ||
            cleanCmd.contains("line hata do") || cleanCmd.contains("sentence hata do") ||
            cleanCmd.contains("vakya hata do")
        ) {
            return currentLetter.removeLastSentence()
        }

        // 3. Word replacement: "replace X with Y" / "X ki jagah Y" / "X hata kar Y"
        val replacePatterns = listOf(
            Regex("replace\\s+(.+?)\\s+with\\s+(.+)", RegexOption.IGNORE_CASE),
            Regex("(.+?)\\s+ki\\s+jagah\\s+(.+?)\\s+(kar do|likho|rakho|karo)?", RegexOption.IGNORE_CASE),
            Regex("(.+?)\\s+hata\\s+kar\\s+(.+?)\\s+(kar do|likho|karo)?", RegexOption.IGNORE_CASE),
            Regex("(.+?)\\s+nahi\\s+(.+)", RegexOption.IGNORE_CASE)
        )
        for (pattern in replacePatterns) {
            val match = pattern.find(command)
            if (match != null) {
                val oldWord = match.groupValues[1].trim()
                val newWord = match.groupValues[2].trim()
                if (oldWord.isNotBlank() && newWord.isNotBlank()) {
                    return currentLetter.replaceWord(oldWord, newWord)
                }
            }
        }

        // 4. Specific word deletion: "[word] delete kar do" / "[word] hata do" / "[word] mita do" / "delete [word]" / "remove [word]"
        val deleteWordPatterns = listOf(
            Regex("(.+?)\\s+(ko\\s+)?(delete\\s+kar\\s+do|delete\\s+karo|delete|hata\\s+do|hatao|mita\\s+do|nikal\\s+do)", RegexOption.IGNORE_CASE),
            Regex("(delete|remove|erase|clear)\\s+(word\\s+|the\\s+word\\s+|shabd\\s+)?(.+)", RegexOption.IGNORE_CASE),
            Regex("(.+?)\\s+(kete\\s+dao|muche\\s+dao|kadhun\\s+taka|theeseyandi|neekavum|kadhi\\s+nakho)", RegexOption.IGNORE_CASE),
            Regex("(eliminar|borrar|supprimer|effacer|lösche|entferne)\\s+(.+)", RegexOption.IGNORE_CASE)
        )
        for (pattern in deleteWordPatterns) {
            val match = pattern.find(command)
            if (match != null) {
                val wordGroup = if (match.groupValues[1].equals("delete", true) ||
                    match.groupValues[1].equals("remove", true) ||
                    match.groupValues[1].equals("erase", true) ||
                    match.groupValues[1].equals("clear", true) ||
                    match.groupValues[1].equals("eliminar", true) ||
                    match.groupValues[1].equals("borrar", true) ||
                    match.groupValues[1].equals("supprimer", true) ||
                    match.groupValues[1].equals("effacer", true) ||
                    match.groupValues[1].equals("lösche", true) ||
                    match.groupValues[1].equals("entferne", true)
                ) {
                    match.groupValues.last().trim()
                } else {
                    match.groupValues[1].trim()
                }

                val cleanTarget = wordGroup.removePrefix("word ").removePrefix("the word ").removePrefix("shabd ").trim()
                if (cleanTarget.isNotBlank() && cleanTarget.length < 50 &&
                    !cleanTarget.equals("ye", true) && !cleanTarget.equals("yeh", true) && !cleanTarget.equals("this", true)
                ) {
                    return currentLetter.removeWord(cleanTarget)
                }
            }
        }

        // Check if command is to translate to Hindi
        if (cleanCmd.contains("translate") && (cleanCmd.contains("hindi") || cleanCmd.contains("हिन्दी") || cleanCmd.contains("हिंदी"))) {
            return translateLetter(currentLetter, "hi")
        }
        if (cleanCmd.contains("translate") && cleanCmd.contains("english")) {
            return translateLetter(currentLetter, "en")
        }

        // Call Gemini for context-aware refinement if API available
        val currentJson = JSONObject().apply {
            put("title", currentLetter.title)
            put("senderDetails", currentLetter.senderDetails)
            put("dateString", currentLetter.dateString)
            put("recipientDetails", currentLetter.recipientDetails)
            put("subject", currentLetter.cleanSubjectText())
            put("salutation", currentLetter.salutation)
            val paras = JSONArray()
            currentLetter.bodyParagraphs.forEach { paras.put(it) }
            put("bodyParagraphs", paras)
            put("complimentaryClose", currentLetter.complimentaryClose)
            put("signerName", currentLetter.signerName)
            put("signerDesignation", currentLetter.signerDesignation)
            put("enclosures", currentLetter.enclosures)
        }

        val prompt = """
Current Letter JSON:
$currentJson

User Voice Instruction to modify the letter:
"$command"

Target Language: $targetLanguageCode

Apply the user's modifications precisely. If the user asked to change a paragraph, delete a sentence, add details, change tone, or translate, update the JSON accordingly. Return ONLY the modified JSON object.
"""

        val result = GeminiClient.generateContent(prompt, SYSTEM_INSTRUCTION)
        if (result.isSuccess) {
            val parsed = parseLetterJson(result.getOrNull() ?: "", currentLetter.title, targetLanguageCode)
            if (parsed != null) {
                return parsed.copy(
                    id = currentLetter.id,
                    formatting = currentLetter.formatting,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }

        // Direct append paragraph if requested
        if (cleanCmd.startsWith("add paragraph") || cleanCmd.startsWith("new paragraph") || cleanCmd.startsWith("पैराग्राफ जोड़ो")) {
            val textToAdd = command.substringAfter("paragraph", "").substringAfter("जोड़ो", "").trim()
            if (textToAdd.isNotBlank()) {
                val updatedParas = currentLetter.bodyParagraphs + textToAdd
                return currentLetter.copy(bodyParagraphs = updatedParas, updatedAt = System.currentTimeMillis())
            }
        }

        return currentLetter
    }

    suspend fun translateLetter(currentLetter: Letter, targetLanguageCode: String): Letter {
        val targetLang = LanguageOption.findByCode(targetLanguageCode)

        // If translating to Hindi, run local pure Hindi engine or translate cleanly
        if (targetLanguageCode.equals("hi", ignoreCase = true)) {
            val prompt = currentLetter.cleanSubjectText() + " " + currentLetter.bodyParagraphs.joinToString(" ")
            val hindiLetter = generateHindiLetter(prompt)
            return hindiLetter.copy(
                id = currentLetter.id,
                letterLanguage = "hi",
                formatting = currentLetter.formatting,
                updatedAt = System.currentTimeMillis()
            )
        }

        val prompt = """
Translate the following letter completely and accurately into ${targetLang.displayName} (${targetLang.nativeName}).
Keep all formal letter structural components intact and use culturally appropriate formal phrasing.

Input Subject: ${currentLetter.cleanSubjectText()}
Input Salutation: ${currentLetter.salutation}
Input Body:
${currentLetter.bodyParagraphs.joinToString("\n\n")}
Input Close: ${currentLetter.complimentaryClose}

Return ONLY a JSON object with keys: title, subject, salutation, bodyParagraphs, complimentaryClose, recipientDetails, senderDetails, signerName, signerDesignation, enclosures.
"""
        val result = GeminiClient.generateContent(prompt, SYSTEM_INSTRUCTION)
        if (result.isSuccess) {
            val raw = result.getOrNull() ?: ""
            val parsed = parseLetterJson(raw, currentLetter.title, targetLanguageCode)
            if (parsed != null) {
                return parsed.copy(
                    id = currentLetter.id,
                    letterLanguage = targetLanguageCode,
                    formatting = currentLetter.formatting,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }

        return currentLetter.copy(
            letterLanguage = targetLanguageCode,
            title = "${currentLetter.title} [${targetLang.displayName}]",
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun adjustTone(currentLetter: Letter, tone: LetterTone): Letter {
        val prompt = """
Rewrite the following letter to have a ${tone.label} tone (${tone.description}).
Keep the core facts, dates, placeholders, and structure intact, but refine the vocabulary, salutation, and closing to match the requested tone.

Current Subject: ${currentLetter.cleanSubjectText()}
Current Body:
${currentLetter.bodyParagraphs.joinToString("\n\n")}

Return ONLY a JSON object with: subject, salutation, bodyParagraphs, complimentaryClose.
"""
        val result = GeminiClient.generateContent(prompt, SYSTEM_INSTRUCTION)
        if (result.isSuccess) {
            val json = cleanAndParseJsonObject(result.getOrNull() ?: "")
            if (json != null) {
                val newSubject = sanitizeSubject(json.optString("subject", currentLetter.cleanSubjectText()), currentLetter.letterLanguage)
                val newSalutation = json.optString("salutation", currentLetter.salutation)
                val newClose = json.optString("complimentaryClose", currentLetter.complimentaryClose)
                val newParasArray = json.optJSONArray("bodyParagraphs")
                val newParas = mutableListOf<String>()
                if (newParasArray != null) {
                    for (i in 0 until newParasArray.length()) {
                        newParas.add(newParasArray.getString(i))
                    }
                } else {
                    newParas.addAll(currentLetter.bodyParagraphs)
                }

                return currentLetter.copy(
                    tone = tone,
                    subject = newSubject,
                    salutation = newSalutation,
                    complimentaryClose = newClose,
                    bodyParagraphs = newParas,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }

        return currentLetter.copy(tone = tone, updatedAt = System.currentTimeMillis())
    }

    suspend fun proofreadLetter(currentLetter: Letter): Letter {
        val prompt = """
Proofread and correct all grammar, spelling, punctuation, and wording in this letter without changing its core meaning:

Subject: ${currentLetter.cleanSubjectText()}
Body:
${currentLetter.bodyParagraphs.joinToString("\n\n")}

Return ONLY a JSON object with: subject, bodyParagraphs.
"""
        val result = GeminiClient.generateContent(prompt, SYSTEM_INSTRUCTION)
        if (result.isSuccess) {
            val json = cleanAndParseJsonObject(result.getOrNull() ?: "")
            if (json != null) {
                val newSubject = sanitizeSubject(json.optString("subject", currentLetter.cleanSubjectText()), currentLetter.letterLanguage)
                val newParasArray = json.optJSONArray("bodyParagraphs")
                val newParas = mutableListOf<String>()
                if (newParasArray != null) {
                    for (i in 0 until newParasArray.length()) {
                        newParas.add(newParasArray.getString(i))
                    }
                } else {
                    newParas.addAll(currentLetter.bodyParagraphs)
                }

                return currentLetter.copy(
                    subject = newSubject,
                    bodyParagraphs = newParas,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }

        return currentLetter
    }

    /**
     * Strips conversational carrier phrases such as "ek letter banana hai", "mujhe ek letter likhna hai ki...", etc.
     * Returns empty string if the input only consisted of conversational intent.
     */
    fun cleanUserConversationalFluff(raw: String): String {
        var text = raw.trim()

        val fluffPhrases = listOf(
            "एक लेटर बनाना है जिसमें यह हो कि",
            "एक लेटर बनाना है जिसमें",
            "एक लेटर बनाना है कि",
            "एक लेटर बनाना है",
            "लेटर बनाना है जिसमें यह हो कि",
            "लेटर बनाना है कि",
            "लेटर बनाना है",
            "एक लेटर बना दो जिसमें",
            "एक लेटर बना दो कि",
            "एक लेटर बना दो",
            "लेटर बना दो",
            "एक लेटर लिख दो जिसमें",
            "एक लेटर लिख दो कि",
            "एक लेटर लिख दो",
            "लेटर लिख दो",
            "एक पत्र लिखना है कि",
            "एक पत्र लिखना है",
            "एक पत्र बनाना है कि",
            "एक पत्र बनाना है जिसमें",
            "एक पत्र बनाना है",
            "पत्र बनाना है कि",
            "पत्र बनाना है",
            "पत्र लिखना है कि",
            "पत्र लिखना है",
            "एक पत्र बना दो कि",
            "एक पत्र बना दो",
            "एक प्रार्थना पत्र लिखना है कि",
            "एक प्रार्थना पत्र लिखना है",
            "एक प्रार्थना पत्र बनाना है कि",
            "एक प्रार्थना पत्र बनाना है",
            "प्रार्थना पत्र लिखना है",
            "प्रार्थना पत्र बनाना है",
            "एक एप्लीकेशन बनाना है कि",
            "एक एप्लीकेशन बनाना है",
            "एप्लीकेशन बनाना है",
            "एक एप्लीकेशन बना दो कि",
            "एक एप्लीकेशन बना दो",
            "एक एप्लीकेशन लिख दो कि",
            "एक एप्लीकेशन लिख दो",
            "mujhe ek letter banana hai jisme",
            "mujhe ek letter banana hai ki",
            "mujhe ek letter banana hai",
            "mujhe ek letter likhna hai ki",
            "mujhe ek letter likhna hai",
            "mujhe ek patra likhna hai ki",
            "mujhe ek patra likhna hai",
            "mujhe ek application likhni hai ki",
            "mujhe ek application likhni hai",
            "humko ek letter banana hai",
            "ek letter banana hai jisme yeh ho ki",
            "ek letter banana hai jisme ye ho ki",
            "ek letter banana hai jisme",
            "ek letter banana hai ki",
            "ek letter banana hai",
            "letter banana hai jisme",
            "letter banana hai ki",
            "letter banana hai",
            "ek letter bana do jisme",
            "ek letter bana do ki",
            "ek letter bana do",
            "letter bana do",
            "ek letter likh do ki",
            "ek letter likh do",
            "ek letter likhna hai ki",
            "ek letter likhna hai",
            "letter likhna hai",
            "ek application banana hai jisme",
            "ek application banana hai ki",
            "ek application banana hai",
            "application banana hai",
            "ek application likh do ki",
            "ek application likh do",
            "ek application likhna hai",
            "ek patra likhna hai ki",
            "ek patra likhna hai",
            "patra likhna hai",
            "write a letter stating that",
            "write a letter saying that",
            "write a letter regarding",
            "write a letter to",
            "write a letter for",
            "write a letter about",
            "write a letter",
            "write an application saying that",
            "write an application stating that",
            "write an application regarding",
            "write an application for",
            "write an application to",
            "write an application",
            "make a letter regarding",
            "make a letter for",
            "make a letter",
            "make an application regarding",
            "make an application for",
            "make an application",
            "create a letter regarding",
            "create a letter for",
            "create a letter",
            "create an application for",
            "create an application",
            "I want to write a letter",
            "I want to make a letter",
            "I want to create a letter"
        )

        for (fluff in fluffPhrases) {
            if (text.startsWith(fluff, ignoreCase = true)) {
                text = text.substring(fluff.length).trim()
                break
            }
        }

        // Strip connector fluff from the beginning
        val connectors = listOf(
            "जिसमें यह हो कि", "जिसमें ये हो कि", "जिसमें", "कि मैं", "कि मुझे", "कि",
            "jisme yeh ho ki", "jisme ye ho ki", "jisme", "ki mai", "ki mujhe", "ki",
            "stating that", "saying that", "regarding", "for", "about", "to"
        )
        for (conn in connectors) {
            if (text.startsWith(conn, ignoreCase = true)) {
                text = text.substring(conn.length).trim()
                break
            }
        }

        // Clean punctuation
        text = text.trim(',', '.', '!', '?', '-', ':')

        // In-stream speech corrections: e.g. "X ye shabd delete kar do" or "X hata do" or "delete X"
        text = text.replace(Regex("(?i)\\b(\\w+)\\s+(ko\\s+)?(ye\\s+shabd\\s+)?(delete\\s+kar\\s+do|delete\\s+karo|hata\\s+do|mita\\s+do|nikal\\s+do)"), "")
        text = text.replace(Regex("(?i)\\b(delete|remove)\\s+(\\w+)"), "")
        text = text.replace(Regex("(?i)(ye|yeh|this)\\s+(shabd|word)?\\s*(delete\\s+kar\\s+do|delete\\s+karo|hata\\s+do|mita\\s+do)"), "")
        text = text.replace(Regex("\\s+"), " ").trim()

        return text.trim()
    }

    private fun sanitizeSubject(sub: String, languageCode: String): String {
        var s = sub.trim()
        while (s.startsWith("Subject:", ignoreCase = true) ||
            s.startsWith("Regarding", ignoreCase = true) ||
            s.startsWith("विषय:", ignoreCase = true) ||
            s.startsWith("विषय :", ignoreCase = true) ||
            s.startsWith("के संबंध में:", ignoreCase = true)
        ) {
            s = s.substringAfter(":").trim()
            if (s.startsWith("Regarding", ignoreCase = true)) {
                s = s.removePrefix("Regarding").trim()
            }
        }

        if (s.isBlank() || s.equals("ek letter banana hai", ignoreCase = true)) {
            return if (languageCode == "hi") "आवश्यक कार्य हेतु प्रार्थना पत्र" else "Application for Urgent Request"
        }
        return s
    }

    private fun isMainlyLatin(text: String): Boolean {
        val latin = text.count { it in 'a'..'z' || it in 'A'..'Z' }
        val total = text.count { it.isLetter() }
        return total > 0 && (latin.toFloat() / total > 0.35f)
    }

    private fun parseLetterJson(raw: String, fallbackTopic: String, languageCode: String): Letter? {
        val json = cleanAndParseJsonObject(raw) ?: return null

        val isHindi = languageCode.equals("hi", ignoreCase = true)

        val defaultTitle = if (isHindi) "औपचारिक प्रार्थना पत्र" else "Formal Application"
        val rawTitle = json.optString("title", defaultTitle)
        val title = if (rawTitle.isBlank() || rawTitle.contains("ek letter banana hai", true) || (isHindi && isMainlyLatin(rawTitle))) defaultTitle else rawTitle

        val letterTypeStr = json.optString("letterType", "CUSTOM_LETTER")
        val toneStr = json.optString("tone", "FORMAL")

        val defaultSender = if (isHindi) "[आपका नाम]\n[पता / संपर्क सूत्र]" else "[YOUR NAME]\n[ADDRESS]"
        val senderDetails = json.optString("senderDetails", defaultSender).let {
            if (isHindi && (it.contains("[YOUR NAME]") || isMainlyLatin(it))) defaultSender else it
        }

        val todayDate = if (isHindi) {
            SimpleDateFormat("dd MMMM yyyy", Locale("hi", "IN")).format(Date())
        } else {
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        }
        val dateStr = json.optString("dateString", todayDate)

        val defaultRecipient = if (isHindi) {
            "सक्षम अधिकारी महोदय,\n[संस्थान / कार्यालय का नाम],\n[स्थान / शहर]"
        } else {
            "The Concerned Authority,\n[ORGANIZATION / INSTITUTION NAME],\n[ADDRESS]"
        }
        val recipientDetails = json.optString("recipientDetails", defaultRecipient).let {
            if (isHindi && (it.contains("The Concerned", true) || it.contains("[ORGANIZATION", true) || isMainlyLatin(it))) defaultRecipient else it
        }

        val rawSubject = json.optString("subject", "")
        var cleanSubject = sanitizeSubject(rawSubject, languageCode)
        if (isHindi && isMainlyLatin(cleanSubject)) {
            cleanSubject = if (fallbackTopic.isNotBlank()) "$fallbackTopic के संबंध में प्रार्थना पत्र" else "आवश्यक कार्य हेतु प्रार्थना पत्र"
        }

        val defaultSalutation = if (isHindi) "आदरणीय महोदय / महोदया," else "Dear Sir/Madam,"
        val rawSalutation = json.optString("salutation", defaultSalutation)
        val salutation = if (isHindi && (rawSalutation.contains("Dear", true) || rawSalutation.contains("Sir", true) || isMainlyLatin(rawSalutation))) defaultSalutation else rawSalutation

        val defaultClose = if (isHindi) "सधन्यवाद,\nभवदीय," else "Yours sincerely,"
        val rawClose = json.optString("complimentaryClose", defaultClose)
        val complimentaryClose = if (isHindi && (rawClose.contains("sincerely", true) || rawClose.contains("Yours", true) || isMainlyLatin(rawClose))) defaultClose else rawClose

        val defaultSigner = if (isHindi) "[आपका नाम]" else "[YOUR NAME]"
        val signerName = json.optString("signerName", defaultSigner).let {
            if (isHindi && (it.contains("[YOUR NAME]") || isMainlyLatin(it))) defaultSigner else it
        }

        val signerDesignation = json.optString("signerDesignation", if (isHindi) "[प्रार्थी / पद]" else "").let {
            if (isHindi && isMainlyLatin(it)) "[प्रार्थी / पद]" else it
        }
        val enclosures = json.optString("enclosures", "").let {
            if (isHindi && isMainlyLatin(it)) "" else it
        }

        val paras = mutableListOf<String>()
        val parasArray = json.optJSONArray("bodyParagraphs")
        if (parasArray != null) {
            for (i in 0 until parasArray.length()) {
                val p = parasArray.getString(i).trim()
                if (p.isNotBlank()) paras.add(p)
            }
        } else {
            val body = json.optString("body", "")
            if (body.isNotBlank()) {
                paras.addAll(body.split("\n\n").filter { it.isNotBlank() })
            }
        }

        val hasEnglishBody = isHindi && paras.any { isMainlyLatin(it) }
        val finalParas = if (paras.isEmpty() || hasEnglishBody || paras.any { it.contains("ek letter banana hai", true) }) {
            if (isHindi) {
                // Return high-quality native Hindi paragraphs matched to the topic
                val fallbackModel = generateHindiLetter(fallbackTopic)
                fallbackModel.bodyParagraphs
            } else {
                listOf(
                    "I am writing this formal application with reference to the subject mentioned above.",
                    "Kindly look into this request with favorable consideration and advise on the next steps.",
                    "Thank you for your valuable time and cooperation."
                )
            }
        } else paras

        val formatObj = json.optJSONObject("formatting")
        val isSubjectBold = formatObj?.optBoolean("isSubjectBold", true) ?: true

        return Letter(
            title = title,
            letterType = LetterType.fromString(letterTypeStr),
            tone = LetterTone.fromString(toneStr),
            letterLanguage = languageCode,
            senderDetails = senderDetails,
            dateString = dateStr,
            recipientDetails = recipientDetails,
            subject = cleanSubject,
            salutation = salutation,
            bodyParagraphs = finalParas,
            complimentaryClose = complimentaryClose,
            signerName = signerName,
            signerDesignation = signerDesignation,
            enclosures = enclosures,
            formatting = LetterFormatOptions(isSubjectBold = isSubjectBold),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun cleanAndParseJsonObject(raw: String): JSONObject? {
        try {
            var text = raw.trim()
            if (text.startsWith("```json")) {
                text = text.removePrefix("```json")
            } else if (text.startsWith("```")) {
                text = text.removePrefix("```")
            }
            if (text.endsWith("```")) {
                text = text.removeSuffix("```")
            }
            text = text.trim()

            val startIndex = text.indexOf('{')
            val endIndex = text.lastIndexOf('}')
            if (startIndex != -1 && endIndex > startIndex) {
                val jsonSubstring = text.substring(startIndex, endIndex + 1)
                return JSONObject(jsonSubstring)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed parsing JSON", e)
        }
        return null
    }

    /**
     * Smart local rule-based letter builder with complete native Hindi & English engines
     */
    private fun smartLocalFallback(prompt: String, languageCode: String): Letter {
        val isHindi = languageCode.equals("hi", ignoreCase = true) || containsDevanagari(prompt)

        return if (isHindi) {
            generateHindiLetter(prompt)
        } else {
            generateEnglishLetter(prompt)
        }
    }

    private fun containsDevanagari(text: String): Boolean {
        for (char in text) {
            if (char.code in 0x0900..0x097F) return true
        }
        return false
    }

    /**
     * Complete native formal Hindi letter generator
     */
    private fun generateHindiLetter(prompt: String): Letter {
        val lower = prompt.lowercase(Locale.ROOT)
        val cleanTopic = cleanUserConversationalFluff(prompt)
        val todayHindiDate = SimpleDateFormat("dd MMMM yyyy", Locale("hi", "IN")).format(Date())

        // 1. Sickness / Medical / Illness
        if (lower.contains("बीमार") || lower.contains("बुखार") || lower.contains("तबीयत") ||
            lower.contains("अस्वस्थ") || lower.contains("डॉक्टर") || lower.contains("bimar") ||
            lower.contains("bukhar") || lower.contains("fever") || lower.contains("sick")
        ) {
            return Letter(
                title = "बीमारी के कारण अवकाश प्रार्थना पत्र",
                letterType = LetterType.LEAVE_APPLICATION,
                tone = LetterTone.RESPECTFUL,
                letterLanguage = "hi",
                senderDetails = "[आपका नाम]\nकक्षा: [कक्षा] - वर्ग [वर्ग]\nअनुक्रमांक (Roll No): [रोल नंबर]\n[विद्यालय का नाम]",
                dateString = todayHindiDate,
                recipientDetails = "प्रधानाचार्य महोदय,\n[विद्यालय का नाम],\n[शहर / स्थान]",
                subject = "अस्वस्थता (बीमारी) के कारण तीन दिवसीय अवकाश हेतु प्रार्थना पत्र",
                salutation = "आदरणीय महोदय / महोदया,",
                bodyParagraphs = listOf(
                    "सविनय निवेदन यह है कि मैं कल रात्रि से तीव्र ज्वर (बुखार) से पीड़ित हूँ तथा मेरा स्वास्थ्य अत्यधिक अस्वस्थ है। चिकित्सक ने मुझे स्वास्थ्य लाभ हेतु तीन दिन तक पूर्ण विश्राम करने की सलाह दी है।",
                    "अतः आपसे विनम्र प्रार्थना है कि मुझे [आरंभिक दिनांक] से [अंतिम दिनांक] तक तीन दिन का अवकाश प्रदान करने की महती कृपा करें। इस अवकाश अवधि में छूटा हुआ समस्त अध्ययन कार्य मैं विद्यालय लौटने पर शीघ्र पूर्ण कर लूँगा/लूँगी।",
                    "संलग्नक के रूप में चिकित्सक का परामर्श पत्र प्रस्तुत है।"
                ),
                complimentaryClose = "सधन्यवाद,\nआपका आज्ञाकारी शिष्य / शिष्या,",
                signerName = "[आपका नाम]",
                signerDesignation = "कक्षा: [कक्षा], रोल नं: [रोल नंबर]",
                enclosures = "1. चिकित्सक का परामर्श प्रमाण पत्र (Medical Certificate)",
                formatting = LetterFormatOptions(isSubjectBold = true)
            )
        }

        // 2. School / Principal Leave / Transfer Certificate
        if (lower.contains("प्रधानाचार्य") || lower.contains("प्रिंसिपल") || lower.contains("स्कूल") ||
            lower.contains("टीसी") || lower.contains("स्थानांतरण") || lower.contains("विद्यालय") ||
            lower.contains("chhutti") || lower.contains("leave") || lower.contains("principal")
        ) {
            return Letter(
                title = "अवकाश प्रार्थना पत्र",
                letterType = LetterType.SCHOOL_APPLICATION,
                tone = LetterTone.RESPECTFUL,
                letterLanguage = "hi",
                senderDetails = "[विद्यार्थी का नाम]\nकक्षा: [कक्षा]\nअनुक्रमांक: [रोल नंबर]",
                dateString = todayHindiDate,
                recipientDetails = "प्रधानाचार्य महोदय,\n[विद्यालय / कॉलेज का नाम],\n[शहर / स्थान]",
                subject = "अपरिहार्य कार्यवश अवकाश स्वीकृत करने हेतु प्रार्थना पत्र",
                salutation = "आदरणीय महोदय,",
                bodyParagraphs = listOf(
                    "सविनय निवेदन यह है कि मुझे एक अति आवश्यक पारिवारिक कार्य हेतु सपरिवार बाहर जाना पड़ रहा है। इस कारणवश मैं आगामी कुछ दिनों तक कक्षा में उपस्थित होने में असमर्थ रहूँगा/रहूँगी।",
                    "अतः आपसे सादर अनुरोध है कि मुझे [दिनांक] से [दिनांक] तक का अवकाश स्वीकृत करने की कृपा करें। इसके लिए मैं आपका सदैव आभारी रहूँगा/रहूँगी।"
                ),
                complimentaryClose = "सधन्यवाद,\nआपका आज्ञाकारी छात्र,",
                signerName = "[विद्यार्थी का नाम]",
                signerDesignation = "कक्षा [कक्षा]",
                formatting = LetterFormatOptions(isSubjectBold = true)
            )
        }

        // 3. Bank Account Closure / Cheque / ATM
        if (lower.contains("बैंक") || lower.contains("खाता") || lower.contains("शाखा") ||
            lower.contains("प्रबंधक") || lower.contains("चेक") || lower.contains("एटीएम") ||
            lower.contains("bank") || lower.contains("account") || lower.contains("khata")
        ) {
            return Letter(
                title = "बचत बैंक खाता बंद कराने हेतु प्रार्थना पत्र",
                letterType = LetterType.BANK_LETTER,
                tone = LetterTone.FORMAL,
                letterLanguage = "hi",
                senderDetails = "[खाताधारक का नाम]\n[स्थायी पता]\nमोबाइल: [मोबाइल नंबर]\nईमेल: [ईमेल आईडी]",
                dateString = todayHindiDate,
                recipientDetails = "शाखा प्रबंधक महोदय,\n[बैंक का नाम],\n[शाखा का नाम एवं शहर]",
                subject = "बचत खाता संख्या [खाता संख्या] को स्थायी रूप से बंद करने के संबंध में",
                salutation = "महोदय / महोदया,",
                bodyParagraphs = listOf(
                    "सविनय निवेदन है कि मेरी आपकी बैंक शाखा में बचत खाता संख्या [खाता संख्या] संचालित है। व्यक्तिगत कारणों एवं अन्य स्थान पर स्थानांतरण होने के कारण मैं इस खाते का आगे संचालन करने में असमर्थ हूँ।",
                    "अतः आपसे विनम्र अनुरोध है कि उक्त खाते को बंद करने की कृपा करें तथा खाते में शेष जमा धनराशि मेरे अन्य बैंक खाते (खाता सं: [वैकल्पिक खाता संख्या], IFSC: [IFSC कोड], बैंक: [बैंक नाम]) में अंतरित करने या बैंक ड्राफ्ट जारी करने का कष्ट करें।",
                    "मैं अपनी अप्रयुक्त चेकबुक, पासबुक एवं एटीएम डेबिट कार्ड इस प्रार्थना पत्र के साथ संलग्न कर रहा/रही हूँ।"
                ),
                complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
                signerName = "[खाताधारक का नाम]",
                signerDesignation = "खाताधारक",
                enclosures = "1. मूल पासबुक\n2. अप्रयुक्त चेक पत्ते\n3. एटीएम/डेबिट कार्ड\n4. पहचान पत्र की प्रति",
                formatting = LetterFormatOptions(isSubjectBold = true)
            )
        }

        // 4. Resignation / Office / Workplace
        if (lower.contains("इस्तीफा") || lower.contains("त्यागपत्र") || lower.contains("ऑफिस") ||
            lower.contains("कंपनी") || lower.contains("कार्यालय") || lower.contains("resign")
        ) {
            return Letter(
                title = "औपचारिक त्यागपत्र (Resignation Letter)",
                letterType = LetterType.OFFICE_LETTER,
                tone = LetterTone.FORMAL,
                letterLanguage = "hi",
                senderDetails = "[कर्मचारी का नाम]\nपद: [वर्तमान पद]\nकर्मचारी आईडी: [आईडी नंबर]\nविभाग: [विभाग का नाम]",
                dateString = todayHindiDate,
                recipientDetails = "विभागाध्यक्ष / प्रबंधक महोदय,\n[कंपनी / संस्थान का नाम],\n[कार्यालय का पता]",
                subject = "[पद का नाम] के पद से त्यागपत्र प्रस्तुत करने के संबंध में",
                salutation = "आदरणीय महोदय / महोदया,",
                bodyParagraphs = listOf(
                    "कृपया इस पत्र को मेरे वर्तमान पद [पद का नाम] से औपचारिक त्यागपत्र के रूप में स्वीकार करें। नियमानुसार मेरा नोटिस पीरियड [नोटिस अवधि e.g. 30 दिन] रहेगा तथा मेरी अंतिम कार्य दिवस [अंतिम कार्य दिनांक] होगी।",
                    "मैं इस संस्थान में अपने कार्यकाल के दौरान मिले निरंतर मार्गदर्शन एवं सहयोग के लिए हृदय से आभारी हूँ। इस संस्था के साथ कार्य करने का अनुभव मेरे पेशेवर जीवन के लिए अत्यंत मूल्यवान रहा है।",
                    "नोटिस अवधि के दौरान मैं अपनी सभी वर्तमान जिम्मेदारियों को पूरा करूँगा/करूँगी तथा नए सहयोगी को कार्यभार सौंपने में पूर्ण सहयोग प्रदान करूँगा/करूँगी।"
                ),
                complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
                signerName = "[कर्मचारी का नाम]",
                signerDesignation = "[वर्तमान पद]",
                formatting = LetterFormatOptions(isSubjectBold = true)
            )
        }

        // 5. Complaint / Grievance
        if (lower.contains("शिकायत") || lower.contains("सड़क") || lower.contains("बिजली") ||
            lower.contains("पानी") || lower.contains("खराब") || lower.contains("complaint")
        ) {
            val topicText = if (cleanTopic.isBlank()) "स्थानीय जनसमस्या" else cleanTopic
            return Letter(
                title = "औपचारिक शिकायत प्रार्थना पत्र",
                letterType = LetterType.COMPLAINT_LETTER,
                tone = LetterTone.FORMAL,
                letterLanguage = "hi",
                senderDetails = "[शिकायतकर्ता का नाम]\n[निवासी पता]\nसंपर्क नंबर: [फोन नंबर]",
                dateString = todayHindiDate,
                recipientDetails = "संबंधित सक्षम अधिकारी महोदय,\n[विभाग / कार्यालय का नाम],\n[शहर / जिला]",
                subject = "$topicText के संबंध में शिकायत प्रार्थना पत्र",
                salutation = "महोदय / महोदया,",
                bodyParagraphs = listOf(
                    "सविनय निवेदन यह है कि मैं आपका ध्यान हमारे क्षेत्र में उत्पन्न $topicText की गंभीर समस्या की ओर आकृष्ट करना चाहता/चाहती हूँ।",
                    "इस समस्या के कारण स्थानीय निवासियों को प्रतिदिन भारी कठिनाइयों का सामना करना पड़ रहा है। पूर्व में भी इस संबंध में सूचित किया गया था, परंतु अब तक कोई समाधान नहीं हुआ है।",
                    "अतः आपसे सादर अनुरोध है कि जनहित को ध्यान में रखते हुए इस समस्या का त्वरित एवं स्थाई समाधान कराने की कृपा करें।"
                ),
                complimentaryClose = "सधन्यवाद,\nभवदीय,",
                signerName = "[आपका नाम]",
                signerDesignation = "स्थानीय निवासी",
                formatting = LetterFormatOptions(isSubjectBold = true)
            )
        }

        // 6. Default Formal Hindi Letter (when prompt was generic e.g. "ek letter banana hai" or general inquiry)
        val hasSpecificTopic = cleanTopic.isNotBlank() && !cleanTopic.equals("ek letter banana hai", ignoreCase = true)
        val subjectLine = if (hasSpecificTopic) "$cleanTopic के संबंध में प्रार्थना पत्र" else "आवश्यक कार्य हेतु औपचारिक प्रार्थना पत्र"
        val bodyContent = if (hasSpecificTopic) {
            "विषयवस्तु के संबंध में सादर निवेदन है कि $cleanTopic। कृपया इस प्रकरण पर सहानुभूतिपूर्वक विचार करते हुए आवश्यक कार्यवाही करने की कृपा करें।"
        } else {
            "महोदय, मुझे एक अति आवश्यक कार्यवश यह औपचारिक प्रार्थना पत्र प्रस्तुत करना पड़ रहा है। [यहाँ अपना कारण या मुख्य विवरण जोड़ें, जैसे: अवकाश, कार्य निष्पादन, या अनुरोध]। कृपया इस प्रकरण पर सहानुभूतिपूर्वक विचार करने की कृपा करें।"
        }

        return Letter(
            title = "औपचारिक प्रार्थना पत्र",
            letterType = LetterType.CUSTOM_LETTER,
            tone = LetterTone.FORMAL,
            letterLanguage = "hi",
            senderDetails = "[आपका नाम]\n[आपका पता / संपर्क सूत्र]",
            dateString = todayHindiDate,
            recipientDetails = "सक्षम अधिकारी महोदय,\n[संस्थान / कार्यालय का नाम],\n[स्थान / शहर]",
            subject = subjectLine,
            salutation = "आदरणीय महोदय / महोदया,",
            bodyParagraphs = listOf(
                "सविनय निवेदन यह है कि मैं उपरोक्त विषय के संदर्भ में यह प्रार्थना पत्र सादर प्रेषित कर रहा/रही हूँ।",
                bodyContent,
                "अतः आपसे सादर अनुरोध है कि उचित कार्यवाही करने की कृपा करें। आपके बहुमूल्य समय एवं सहयोग के लिए मैं सदैव आभारी रहूँगा/रहूँगी।"
            ),
            complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
            signerName = "[आपका नाम]",
            signerDesignation = "[पद / प्रार्थी]",
            formatting = LetterFormatOptions(isSubjectBold = true)
        )
    }

    /**
     * Complete native formal English letter generator
     */
    private fun generateEnglishLetter(prompt: String): Letter {
        val lower = prompt.lowercase(Locale.ROOT)
        val cleanTopic = cleanUserConversationalFluff(prompt)
        val todayStr = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())

        if (lower.contains("sick") || lower.contains("fever") || lower.contains("illness") ||
            lower.contains("medical") || lower.contains("doctor")
        ) {
            val template = TemplateItem.ALL.find { it.id == "school_sick_leave" }?.initialLetter
            if (template != null) {
                return template.copy(
                    title = "Sick Leave Application",
                    dateString = todayStr,
                    letterLanguage = "en",
                    subject = "Application for 3 days sick leave due to viral fever"
                )
            }
        }

        if (lower.contains("bank") || lower.contains("closure") || lower.contains("account") ||
            lower.contains("cheque") || lower.contains("atm")
        ) {
            val template = TemplateItem.ALL.find { it.id == "bank_account_closure" }?.initialLetter
            if (template != null) {
                return template.copy(
                    title = "Bank Account Closure Request",
                    dateString = todayStr,
                    letterLanguage = "en"
                )
            }
        }

        if (lower.contains("resign") || lower.contains("resignation") || lower.contains("notice period")) {
            val template = TemplateItem.ALL.find { it.id == "office_resignation" }?.initialLetter
            if (template != null) {
                return template.copy(
                    title = "Formal Resignation Letter",
                    dateString = todayStr,
                    letterLanguage = "en"
                )
            }
        }

        if (lower.contains("job") || lower.contains("apply") || lower.contains("cover letter") || lower.contains("interview")) {
            val template = TemplateItem.ALL.find { it.id == "job_application_cover_letter" }?.initialLetter
            if (template != null) {
                return template.copy(
                    title = "Job Application Letter",
                    dateString = todayStr,
                    letterLanguage = "en"
                )
            }
        }

        if (lower.contains("complaint") || lower.contains("damaged") || lower.contains("defect") || lower.contains("refund")) {
            val template = TemplateItem.ALL.find { it.id == "complaint_defective_product" }?.initialLetter
            if (template != null) {
                return template.copy(
                    title = "Formal Complaint Letter",
                    dateString = todayStr,
                    letterLanguage = "en"
                )
            }
        }

        val hasTopic = cleanTopic.isNotBlank() && !cleanTopic.equals("ek letter banana hai", ignoreCase = true)
        val subjectText = if (hasTopic) "Application regarding $cleanTopic" else "Formal Request and Application"
        val bodyContent = if (hasTopic) {
            "Specifically, $cleanTopic. Kindly look into this matter with favorable consideration and advise on the next steps."
        } else {
            "Sir/Madam, I am formally submitting this application for an urgent matter. [Please state your specific reason here — e.g., leave of absence, bank inquiry, or official request]. Kindly accord favorable consideration."
        }

        return Letter(
            title = "Formal Application Letter",
            letterType = LetterType.CUSTOM_LETTER,
            tone = LetterTone.FORMAL,
            letterLanguage = "en",
            senderDetails = "[YOUR NAME]\n[YOUR ADDRESS / CONTACT DETAILS]",
            dateString = todayStr,
            recipientDetails = "The Concerned Authority,\n[ORGANIZATION / INSTITUTION NAME],\n[ADDRESS / LOCATION]",
            subject = subjectText,
            salutation = "Dear Sir/Madam,",
            bodyParagraphs = listOf(
                "I am writing this formal application with reference to the subject mentioned above.",
                bodyContent,
                "Thank you for your valuable time and cooperation in this regard."
            ),
            complimentaryClose = "Yours sincerely,",
            signerName = "[YOUR NAME]",
            signerDesignation = "[DESIGNATION / APPLICANT]",
            formatting = LetterFormatOptions(isSubjectBold = true)
        )
    }
}
