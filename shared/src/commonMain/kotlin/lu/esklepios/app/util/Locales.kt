package lu.esklepios.app.util

/**
 * A language eSklepios supports.
 *
 * `code` is the ISO-639-1 two-letter code used everywhere internally (API
 * payloads, Twine resource buckets, user.language). `englishName` is the
 * canonical English noun for the language — useful for fallback UI before
 * a locale-specific label is available. `flagEmoji` is the platform-rendered
 * flag; both platforms access it via this model (same pattern as `DialCode`).
 */
data class SupportedLanguage(val code: String, val englishName: String, val flagEmoji: String)

/**
 * Authoritative list of supported app languages. Both platforms read this
 * list and combine `flagEmoji` with a Twine-localized name for display.
 *
 * RULE A-13: Inline `listOf("fr" to "French", ...)` in a screen is forbidden.
 */
val supportedLanguages: List<SupportedLanguage> =
    listOf(
        SupportedLanguage(code = "fr", englishName = "French", flagEmoji = "🇫🇷"),
        SupportedLanguage(code = "en", englishName = "English", flagEmoji = "🇬🇧"),
        SupportedLanguage(code = "de", englishName = "German", flagEmoji = "🇩🇪"),
        SupportedLanguage(code = "lb", englishName = "Luxembourgish", flagEmoji = "🇱🇺"),
    )
