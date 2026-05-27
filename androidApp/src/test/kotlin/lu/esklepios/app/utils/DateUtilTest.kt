package lu.esklepios.app.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DateUtilTest {

    @Test
    fun `extractSlotTime returns formatted time from valid slotId`() {
        assertEquals("08:30", DateUtil.extractSlotTime("slot_d1_20260526_0830"))
    }

    @Test
    fun `extractSlotTime returns original string for slotId with too few parts`() {
        assertEquals("invalid", DateUtil.extractSlotTime("invalid"))
    }

    @Test
    fun `extractSlotTime returns original string when hhmm has wrong length`() {
        assertEquals("slot_d1_20260526_830", DateUtil.extractSlotTime("slot_d1_20260526_830"))
    }

    @Test
    fun `compactToIso converts 8-digit compact date to ISO format`() {
        assertEquals("2026-05-26", DateUtil.compactToIso("20260526"))
    }

    @Test
    fun `formatIsoDate formats with PATTERN_DISPLAY_FULL`() {
        assertEquals("Tue, May 26, 2026", DateUtil.formatIsoDate("2026-05-26", DateUtil.PATTERN_DISPLAY_FULL))
    }

    @Test
    fun `formatIsoDate formats with PATTERN_DISPLAY_LONG`() {
        assertEquals("Tuesday, May 26", DateUtil.formatIsoDate("2026-05-26", DateUtil.PATTERN_DISPLAY_LONG))
    }

    @Test
    fun `formatIsoDate returns original string for invalid date`() {
        assertEquals("not-a-date", DateUtil.formatIsoDate("not-a-date", DateUtil.PATTERN_DISPLAY_FULL))
    }

    @Test
    fun `today returns non-null LocalDate`() {
        assertNotNull(DateUtil.today())
    }
}
