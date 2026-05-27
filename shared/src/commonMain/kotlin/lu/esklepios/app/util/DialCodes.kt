package lu.esklepios.app.util

/**
 * An international dial-code entry: telephone prefix + flag emoji + ISO alpha-2
 * country code. The `code` is what the user picks; the alpha-2 is exposed for
 * future locale lookups or country-specific validation.
 */
data class DialCode(
    val code: String,
    val flagEmoji: String,
    val isoAlpha2: String,
)

/**
 * Authoritative list of supported phone dial codes. Both screens (the country
 * dropdown in `EditProfileScreen`/`EditProfileView`) and the parser
 * ([PhoneParser]) read from this single list.
 *
 * RULE A-13: Hardcoding `listOf("+352" to ..., "+33" to ...)` in a screen is forbidden.
 */
val supportedDialCodes: List<DialCode> = listOf(
    DialCode(code = "+352", flagEmoji = "🇱🇺", isoAlpha2 = "LU"),
    DialCode(code = "+33",  flagEmoji = "🇫🇷", isoAlpha2 = "FR"),
    DialCode(code = "+49",  flagEmoji = "🇩🇪", isoAlpha2 = "DE"),
    DialCode(code = "+32",  flagEmoji = "🇧🇪", isoAlpha2 = "BE"),
    DialCode(code = "+44",  flagEmoji = "🇬🇧", isoAlpha2 = "GB"),
)

/**
 * Splits a raw phone string into (recognized dial-code, remaining digits).
 *
 * - `"+352 621 12 34 56"` → (DialCode("+352", "🇱🇺", "LU"), "621 12 34 56")
 * - `"+1 555 1234"`       → (null, "+1 555 1234")          // not in our list
 * - `"621 12 34 56"`      → (null, "621 12 34 56")
 */
object PhoneParser {
    fun parse(raw: String): Pair<DialCode?, String> {
        val dial = supportedDialCodes.firstOrNull { raw.startsWith(it.code) }
        val rest = if (dial != null) raw.removePrefix(dial.code).trim() else raw
        return dial to rest
    }
}
