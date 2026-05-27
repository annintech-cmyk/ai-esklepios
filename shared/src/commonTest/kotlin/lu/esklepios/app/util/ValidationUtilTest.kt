package lu.esklepios.app.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationUtilTest {
    // ── isValidEmail ─────────────────────────────────────────────────────────

    @Test
    fun `isValidEmail accepts simple address`() {
        assertTrue(ValidationUtil.isValidEmail("user@example.com"))
    }

    @Test
    fun `isValidEmail accepts subdomain`() {
        assertTrue(ValidationUtil.isValidEmail("user@mail.example.co.uk"))
    }

    @Test
    fun `isValidEmail rejects empty string`() {
        assertFalse(ValidationUtil.isValidEmail(""))
    }

    @Test
    fun `isValidEmail rejects whitespace only`() {
        assertFalse(ValidationUtil.isValidEmail("   "))
    }

    @Test
    fun `isValidEmail rejects missing at sign`() {
        assertFalse(ValidationUtil.isValidEmail("user.example.com"))
    }

    @Test
    fun `isValidEmail rejects missing dot`() {
        assertFalse(ValidationUtil.isValidEmail("user@example"))
    }

    @Test
    fun `isValidEmail rejects dot before at sign`() {
        assertFalse(ValidationUtil.isValidEmail("user.name@invalid"))
        // "user.name@invalid": indexOf("@") = 9, lastIndexOf(".") = 4 → invalid
    }

    // ── emailsMatch ──────────────────────────────────────────────────────────

    @Test
    fun `emailsMatch returns true when strings equal and non-blank`() {
        assertTrue(ValidationUtil.emailsMatch("a@b.co", "a@b.co"))
    }

    @Test
    fun `emailsMatch returns false when confirm is blank`() {
        assertFalse(ValidationUtil.emailsMatch("a@b.co", ""))
    }

    @Test
    fun `emailsMatch returns false when strings differ`() {
        assertFalse(ValidationUtil.emailsMatch("a@b.co", "x@b.co"))
    }

    // ── passwordCriteria ─────────────────────────────────────────────────────

    @Test
    fun `passwordCriteria empty returns empty set`() {
        assertEquals(emptySet(), ValidationUtil.passwordCriteria(""))
    }

    @Test
    fun `passwordCriteria detects min length only when 12 plus characters`() {
        assertFalse(PasswordCriterion.MIN_LENGTH in ValidationUtil.passwordCriteria("abcdefghijk"))
        assertTrue(PasswordCriterion.MIN_LENGTH in ValidationUtil.passwordCriteria("abcdefghijkl"))
    }

    @Test
    fun `passwordCriteria detects mixed case`() {
        assertTrue(PasswordCriterion.MIXED_CASE in ValidationUtil.passwordCriteria("aB"))
        assertFalse(PasswordCriterion.MIXED_CASE in ValidationUtil.passwordCriteria("abcdef"))
        assertFalse(PasswordCriterion.MIXED_CASE in ValidationUtil.passwordCriteria("ABCDEF"))
    }

    @Test
    fun `passwordCriteria detects number plus symbol`() {
        assertTrue(PasswordCriterion.NUM_AND_SYMBOL in ValidationUtil.passwordCriteria("a1!"))
        assertFalse(PasswordCriterion.NUM_AND_SYMBOL in ValidationUtil.passwordCriteria("a1")) // digit but no symbol
        assertFalse(PasswordCriterion.NUM_AND_SYMBOL in ValidationUtil.passwordCriteria("a!")) // symbol but no digit
    }

    @Test
    fun `passwordCriteria all three criteria simultaneously`() {
        val criteria = ValidationUtil.passwordCriteria("LongEnough1!")
        assertTrue(PasswordCriterion.MIN_LENGTH in criteria)
        assertTrue(PasswordCriterion.MIXED_CASE in criteria)
        assertTrue(PasswordCriterion.NUM_AND_SYMBOL in criteria)
    }

    // ── passwordStrength ─────────────────────────────────────────────────────

    @Test
    fun `passwordStrength empty is NONE`() {
        assertEquals(PasswordStrength.NONE, ValidationUtil.passwordStrength(""))
    }

    @Test
    fun `passwordStrength all criteria is STRONG`() {
        assertEquals(PasswordStrength.STRONG, ValidationUtil.passwordStrength("LongEnough1!"))
    }

    @Test
    fun `passwordStrength length 10 with one criterion is GOOD`() {
        // 10 chars, has digit + symbol → NUM_AND_SYMBOL matched
        assertEquals(PasswordStrength.GOOD, ValidationUtil.passwordStrength("abcdefgh1!"))
    }

    @Test
    fun `passwordStrength length 8 to 9 is FAIR`() {
        assertEquals(PasswordStrength.FAIR, ValidationUtil.passwordStrength("abcdefgh"))
        assertEquals(PasswordStrength.FAIR, ValidationUtil.passwordStrength("abcdefghi"))
    }

    @Test
    fun `passwordStrength short is WEAK`() {
        assertEquals(PasswordStrength.WEAK, ValidationUtil.passwordStrength("a"))
        assertEquals(PasswordStrength.WEAK, ValidationUtil.passwordStrength("abc"))
    }

    @Test
    fun `passwordStrength percent matches enum`() {
        assertEquals(0f, PasswordStrength.NONE.percent)
        assertEquals(0.15f, PasswordStrength.WEAK.percent)
        assertEquals(0.4f, PasswordStrength.FAIR.percent)
        assertEquals(0.65f, PasswordStrength.GOOD.percent)
        assertEquals(0.9f, PasswordStrength.STRONG.percent)
    }
}
