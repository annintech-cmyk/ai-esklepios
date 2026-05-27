package lu.esklepios.app.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CnsFormatterTest {

    @Test
    fun `mask short string returns unchanged`() {
        assertEquals("", CnsFormatter.mask(""))
        assertEquals("12345", CnsFormatter.mask("12345"))
    }

    @Test
    fun `mask exactly 9 characters returns unchanged`() {
        assertEquals("123456789", CnsFormatter.mask("123456789"))
    }

    @Test
    fun `mask 10 characters truncates to 9 plus dots`() {
        assertEquals("123456789 ••••", CnsFormatter.mask("1234567890"))
    }

    @Test
    fun `mask 13 characters truncates to 9 plus dots`() {
        assertEquals("199203145 ••••", CnsFormatter.mask("1992031456789"))
    }
}
