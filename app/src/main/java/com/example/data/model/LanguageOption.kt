package com.example.data.model

data class LanguageOption(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val speechLocale: String,
    val isIndianLanguage: Boolean = false
) {
    val fullLabel: String
        get() = "$displayName ($nativeName)"

    companion object {
        val ALL: List<LanguageOption> = listOf(
            // English
            LanguageOption("en", "English", "English", "en-IN"),
            // Indian Languages
            LanguageOption("hi", "Hindi", "हिन्दी", "hi-IN", true),
            LanguageOption("bn", "Bengali", "বাংলা", "bn-IN", true),
            LanguageOption("te", "Telugu", "తెలుగు", "te-IN", true),
            LanguageOption("mr", "Marathi", "मराठी", "mr-IN", true),
            LanguageOption("ta", "Tamil", "தமிழ்", "ta-IN", true),
            LanguageOption("gu", "Gujarati", "ગુજરાતી", "gu-IN", true),
            LanguageOption("kn", "Kannada", "ಕನ್ನಡ", "kn-IN", true),
            LanguageOption("ml", "Malayalam", "മലയാളം", "ml-IN", true),
            LanguageOption("or", "Odia", "ଓଡ଼ିଆ", "or-IN", true),
            LanguageOption("pa", "Punjabi", "ਪੰਜਾਬੀ", "pa-IN", true),
            LanguageOption("as", "Assamese", "অসমীয়া", "as-IN", true),
            LanguageOption("ur", "Urdu", "اردو", "ur-IN", true),
            LanguageOption("sa", "Sanskrit", "संस्कृतम्", "sa-IN", true),
            // World Languages
            LanguageOption("es", "Spanish", "Español", "es-ES"),
            LanguageOption("fr", "French", "Français", "fr-FR"),
            LanguageOption("de", "German", "Deutsch", "de-DE"),
            LanguageOption("ar", "Arabic", "العربية", "ar-SA"),
            LanguageOption("ja", "Japanese", "日本語", "ja-JP"),
            LanguageOption("ru", "Russian", "Русский", "ru-RU"),
            LanguageOption("pt", "Portuguese", "Português", "pt-BR"),
            LanguageOption("zh", "Chinese", "中文", "zh-CN")
        )

        val DEFAULT_ENGLISH = ALL[0]
        val DEFAULT_HINDI = ALL[1]

        fun findByCode(code: String): LanguageOption {
            return ALL.find { it.code.equals(code, ignoreCase = true) } ?: DEFAULT_ENGLISH
        }
    }
}
