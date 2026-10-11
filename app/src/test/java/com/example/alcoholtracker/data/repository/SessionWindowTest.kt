package com.example.alcoholtracker.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class SessionWindowTest {

    private val sixAm = LocalDateTime.of(2026, 10, 10, 6, 0)
    private val endOfSession = LocalDateTime.of(2026, 10, 11, 5, 59, 59, 999_999_999)

    @Test
    fun evening_belongsToThatDay() {
        assertEquals(sixAm to endOfSession, sessionWindowFor(LocalDateTime.of(2026, 10, 10, 21, 0)))
    }

    @Test
    fun afterMidnight_belongsToTheEveningBefore() {
        assertEquals(sixAm to endOfSession, sessionWindowFor(LocalDateTime.of(2026, 10, 11, 0, 0)))
        assertEquals(sixAm to endOfSession, sessionWindowFor(LocalDateTime.of(2026, 10, 11, 2, 30)))
        assertEquals(sixAm to endOfSession, sessionWindowFor(LocalDateTime.of(2026, 10, 11, 5, 59, 59)))
    }

    @Test
    fun sixAm_startsANewSession() {
        assertEquals(sixAm to endOfSession, sessionWindowFor(sixAm))
        assertEquals(sixAm.plusDays(1), sessionWindowFor(sixAm.plusDays(1)).first)
    }

    @Test
    fun startIsExactlySixRegardlessOfCurrentSeconds() {
        assertEquals(sixAm, sessionWindowFor(LocalDateTime.of(2026, 10, 10, 14, 37, 42, 123)).first)
    }

    @Test
    fun windowSpansOneDayExclusive() {
        val (start, end) = sessionWindowFor(LocalDateTime.of(2026, 3, 29, 12, 0))
        assertEquals(start.plusDays(1).minusNanos(1), end)
    }

    @Test
    fun crossesMonthAndYearBoundaries() {
        assertEquals(LocalDateTime.of(2026, 12, 31, 6, 0), sessionWindowFor(LocalDateTime.of(2027, 1, 1, 1, 0)).first)
    }
}
