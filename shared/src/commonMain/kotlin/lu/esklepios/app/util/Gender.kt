package lu.esklepios.app.util

/**
 * Canonical gender option for forms and the user profile.
 *
 * `apiValue` is the lowercase value the backend expects (`"male"`, `"female"`,
 * `"other"`). `labelKey` is the Twine string-resource key for the localized
 * display label.
 *
 * RULE A-13: Hardcoding `listOf("Male","Female","Other")` or `["Man","Woman","Other"]`
 * in any screen is forbidden — iterate [Gender.entries] instead.
 */
enum class Gender(val apiValue: String, val labelKey: String) {
    MALE("male", "gender_male"),
    FEMALE("female", "gender_female"),
    OTHER("other", "gender_other");

    companion object {
        /**
         * Lenient parse for legacy / mixed API payloads. Accepts:
         *  - `"male"` / `"man"` / `"MALE"`        → [MALE]
         *  - `"female"` / `"woman"` / `"FEMALE"`  → [FEMALE]
         *  - everything else (including null/empty/"other") → [OTHER]
         */
        fun fromApiString(value: String?): Gender {
            if (value.isNullOrBlank()) return OTHER
            return entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) }
                ?: when (value.lowercase()) {
                    "man" -> MALE
                    "woman" -> FEMALE
                    else -> OTHER
                }
        }
    }
}
