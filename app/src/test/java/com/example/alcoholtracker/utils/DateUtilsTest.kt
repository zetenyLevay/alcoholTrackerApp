package com.example.alcoholtracker.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.TimeZone

class DateUtilsTest {

    private lateinit var originalZone: TimeZone

    @Before
    fun pinTimeZone() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun restoreTimeZone() {
        TimeZone.setDefault(originalZone)
    }

    @Test
    fun getSuffix_ordinals() {
        val expected = mapOf(
            1 to "st", 2 to "nd", 3 to "rd", 4 to "th", 10 to "th",
            11 to "th", 12 to "th", 13 to "th",
            21 to "st", 22 to "nd", 23 to "rd", 24 to "th", 31 to "st"
        )
        expected.forEach { (day, suffix) -> assertEquals("day $day", suffix, getSuffix(day)) }
    }

    @Test
    fun getFormattedDate_monthDayAndSuffix() {
        assertEquals("October 11th", getFormattedDate(LocalDate.of(2026, 10, 11)))
        assertEquals("January 1st", getFormattedDate(LocalDate.of(2026, 1, 1)))
        assertEquals("March 22nd", getFormattedDate(LocalDate.of(2026, 3, 22)))
    }

    @Test
    fun getLocalDate_usesSystemZone() {
        assertEquals(LocalDate.of(1970, 1, 1), getLocalDate(0L))
        assertEquals(LocalDate.of(2026, 10, 11), getLocalDate(1_791_676_800_000L))

        TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"))
        assertEquals(LocalDate.of(1969, 12, 31), getLocalDate(0L))
    }
}
