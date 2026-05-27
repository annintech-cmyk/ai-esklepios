package lu.esklepios.app.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PhoneParserTest {
    @Test
    fun `parse Luxembourg prefix splits dial code from rest`() {
        val (dial, rest) = PhoneParser.parse("+352 621 12 34 56")
        assertNotNull(dial)
        assertEquals("+352", dial.code)
        assertEquals("LU", dial.isoAlpha2)
        assertEquals("621 12 34 56", rest)
    }

    @Test
    fun `parse France prefix`() {
        val (dial, rest) = PhoneParser.parse("+33 1 23 45 67 89")
        assertEquals("+33", dial?.code)
        assertEquals("1 23 45 67 89", rest)
    }

    @Test
    fun `parse Germany prefix`() {
        val (dial, _) = PhoneParser.parse("+49 30 12345678")
        assertEquals("+49", dial?.code)
    }

    @Test
    fun `parse Belgium prefix`() {
        val (dial, _) = PhoneParser.parse("+32 2 123 45 67")
        assertEquals("+32", dial?.code)
    }

    @Test
    fun `parse UK prefix`() {
        val (dial, _) = PhoneParser.parse("+44 20 7946 0958")
        assertEquals("+44", dial?.code)
    }

    @Test
    fun `parse unknown prefix returns null and full input`() {
        val (dial, rest) = PhoneParser.parse("+1 555 0100")
        assertNull(dial)
        assertEquals("+1 555 0100", rest)
    }

    @Test
    fun `parse no prefix returns null and full input`() {
        val (dial, rest) = PhoneParser.parse("621 12 34 56")
        assertNull(dial)
        assertEquals("621 12 34 56", rest)
    }

    @Test
    fun `parse empty string returns null and empty`() {
        val (dial, rest) = PhoneParser.parse("")
        assertNull(dial)
        assertEquals("", rest)
    }
}
