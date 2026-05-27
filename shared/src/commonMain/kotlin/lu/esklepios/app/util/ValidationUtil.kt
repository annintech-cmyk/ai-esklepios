package lu.esklepios.app.util

/**
 * Single source of truth for cross-platform validation rules.
 *
 * RULE A-12: Email format, password strength, password criteria, and any other
 * validation rule that does not depend on platform APIs MUST live here.
 * Re-implementing the same validation in a `@Composable`, SwiftUI `var`, or a
 * ViewModel is forbidden — always call into `ValidationUtil`.
 */
object ValidationUtil {
    /** `true` iff [password] meets the minimum character length shown in the strength UI. */
    fun isPasswordMinLength(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    /**
     * Returns `true` iff [email] is structurally valid:
     * non-blank, contains `@` and `.`, and the `.` comes after the `@` (allowing
     * for trailing TLDs but not catching every RFC-5322 quirk — sufficient for UI).
     */
    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() &&
            email.contains("@") &&
            email.contains(".") &&
            email.indexOf("@") < email.lastIndexOf(".")

    /**
     * `true` iff [confirm] is non-blank and exactly equals [first].
     * Used for "confirm email" form fields.
     */
    fun emailsMatch(
        first: String,
        confirm: String,
    ): Boolean = confirm.isNotBlank() && confirm == first

    /** `true` iff [confirmPassword] is non-blank and exactly equals [newPassword]. */
    fun passwordsMatch(
        newPassword: String,
        confirmPassword: String,
    ): Boolean = confirmPassword.isNotBlank() && confirmPassword == newPassword

    /**
     * Returns a [PasswordCriteriaResult] with named boolean fields — easier to
     * consume across the Kotlin/Swift boundary than a raw `Set<PasswordCriterion>`.
     */
    fun passwordCriteriaResult(password: String): PasswordCriteriaResult {
        val met = passwordCriteria(password)
        return PasswordCriteriaResult(
            minLength = PasswordCriterion.MIN_LENGTH in met,
            mixedCase = PasswordCriterion.MIXED_CASE in met,
            numAndSymbol = PasswordCriterion.NUM_AND_SYMBOL in met,
        )
    }

    /**
     * Maps a password to a discrete [PasswordStrength] band.
     *
     * Bands (matches the existing UX exactly):
     *  - empty               → NONE (0%)
     *  - all 3 criteria met  → STRONG (90%)
     *  - length ≥ 10 with at least one of MIXED_CASE or NUM_AND_SYMBOL → GOOD (65%)
     *  - length ≥ 8          → FAIR (40%)
     *  - otherwise           → WEAK (15%)
     */
    fun passwordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.NONE
        val criteria = passwordCriteria(password)
        return when {
            criteria.size == PasswordCriterion.entries.size -> PasswordStrength.STRONG
            password.length >= 10 && (
                PasswordCriterion.MIXED_CASE in criteria ||
                    PasswordCriterion.NUM_AND_SYMBOL in criteria
            ) -> PasswordStrength.GOOD
            password.length >= 8 -> PasswordStrength.FAIR
            else -> PasswordStrength.WEAK
        }
    }

    /**
     * Returns which structural [PasswordCriterion]s [password] satisfies.
     * Callers iterate the result to render the per-criterion checklist UI.
     */
    fun passwordCriteria(password: String): Set<PasswordCriterion> =
        buildSet {
            if (password.length >= MIN_PASSWORD_LENGTH) add(PasswordCriterion.MIN_LENGTH)
            if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) {
                add(PasswordCriterion.MIXED_CASE)
            }
            if (password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() }) {
                add(PasswordCriterion.NUM_AND_SYMBOL)
            }
        }

    private const val MIN_PASSWORD_LENGTH = 12
}
