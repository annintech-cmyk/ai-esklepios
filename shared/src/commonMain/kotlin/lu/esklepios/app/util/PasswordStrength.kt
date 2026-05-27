package lu.esklepios.app.util

enum class PasswordStrength(val percent: Float, val labelKey: String) {
    NONE(0f, ""),
    WEAK(0.15f, "change_password_strength_weak"),
    FAIR(0.4f, "change_password_strength_fair"),
    GOOD(0.65f, "change_password_strength_good"),
    STRONG(0.9f, "change_password_strength_strong"),
}

enum class PasswordCriterion {
    MIN_LENGTH,
    MIXED_CASE,
    NUM_AND_SYMBOL,
}

data class PasswordCriteriaResult(
    val minLength: Boolean,
    val mixedCase: Boolean,
    val numAndSymbol: Boolean,
)
