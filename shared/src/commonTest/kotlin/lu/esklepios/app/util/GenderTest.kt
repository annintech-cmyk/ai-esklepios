package lu.esklepios.app.util

import kotlin.test.Test
import kotlin.test.assertEquals

class GenderTest {
    @Test
    fun `fromApiString lowercase canonical values`() {
        assertEquals(Gender.MALE, Gender.fromApiString("male"))
        assertEquals(Gender.FEMALE, Gender.fromApiString("female"))
        assertEquals(Gender.OTHER, Gender.fromApiString("other"))
    }

    @Test
    fun `fromApiString is case insensitive`() {
        assertEquals(Gender.MALE, Gender.fromApiString("MALE"))
        assertEquals(Gender.FEMALE, Gender.fromApiString("Female"))
    }

    @Test
    fun `fromApiString accepts legacy man and woman labels`() {
        assertEquals(Gender.MALE, Gender.fromApiString("man"))
        assertEquals(Gender.MALE, Gender.fromApiString("Man"))
        assertEquals(Gender.FEMALE, Gender.fromApiString("woman"))
        assertEquals(Gender.FEMALE, Gender.fromApiString("WOMAN"))
    }

    @Test
    fun `fromApiString unknown defaults to OTHER`() {
        assertEquals(Gender.OTHER, Gender.fromApiString("nonbinary"))
        assertEquals(Gender.OTHER, Gender.fromApiString("xyz"))
    }

    @Test
    fun `fromApiString null or blank defaults to OTHER`() {
        assertEquals(Gender.OTHER, Gender.fromApiString(null))
        assertEquals(Gender.OTHER, Gender.fromApiString(""))
        assertEquals(Gender.OTHER, Gender.fromApiString("   "))
    }

    @Test
    fun `enum exposes api value and label key`() {
        assertEquals("male", Gender.MALE.apiValue)
        assertEquals("gender_male", Gender.MALE.labelKey)
        assertEquals("female", Gender.FEMALE.apiValue)
        assertEquals("gender_female", Gender.FEMALE.labelKey)
        assertEquals("other", Gender.OTHER.apiValue)
        assertEquals("gender_other", Gender.OTHER.labelKey)
    }
}
