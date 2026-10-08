package com.example.data.model

enum class LetterFontFamily(val label: String) {
    SERIF("Serif (Classic Formal)"),
    SANS_SERIF("Sans-Serif (Modern Clean)"),
    MONOSPACE("Monospace (Typewriter)")
}

enum class LetterAlignment(val label: String) {
    LEFT("Left Aligned"),
    JUSTIFY("Justified"),
    CENTER("Centered"),
    RIGHT("Right Aligned")
}

enum class LetterMargin(val label: String, val paddingDp: Int) {
    COMPACT("Compact (16mm)", 28),
    STANDARD("Standard (25mm)", 40),
    WIDE("Wide (32mm)", 52)
}

data class LetterFormatOptions(
    val fontFamily: LetterFontFamily = LetterFontFamily.SERIF,
    val fontSizeSp: Float = 13.5f,
    val lineSpacingMultiplier: Float = 1.35f,
    val paragraphSpacingDp: Int = 12,
    val alignment: LetterAlignment = LetterAlignment.LEFT,
    val margin: LetterMargin = LetterMargin.STANDARD,
    val isSubjectBold: Boolean = true,
    val isSubjectUnderlined: Boolean = false,
    val isSenderBold: Boolean = false,
    val isItalicBody: Boolean = false,
    val showPageNumber: Boolean = true,
    val showSignatureLine: Boolean = true,
    val showHeaderDate: Boolean = true,
    val letterheadText: String = "",
    val footerNote: String = ""
)
