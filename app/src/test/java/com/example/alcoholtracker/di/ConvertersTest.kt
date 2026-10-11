package com.example.alcoholtracker.di

import com.example.alcoholtracker.domain.model.DrinkCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun localDateTime_roundTrips() {
        val dateTime = LocalDateTime.of(2026, 10, 11, 23, 59, 58, 123_000_000)
        assertEquals(dateTime, converters.fromTimestamp(converters.dateToTimestamp(dateTime)))
    }

    @Test
    fun localDateTime_storedAsSortableIsoString() {
        assertEquals("2026-01-02T03:04:05", converters.dateToTimestamp(LocalDateTime.of(2026, 1, 2, 3, 4, 5)))
        val earlier = converters.dateToTimestamp(LocalDateTime.of(2026, 1, 2, 9, 0))!!
        val later = converters.dateToTimestamp(LocalDateTime.of(2026, 1, 2, 10, 0))!!
        assert(earlier < later)
    }

    @Test
    fun localDateTime_nullStaysNull() {
        assertNull(converters.fromTimestamp(null))
        assertNull(converters.dateToTimestamp(null))
    }

    @Test
    fun localDate_roundTrips() {
        val date = LocalDate.of(2026, 10, 11)
        assertEquals(date, converters.toLocalDate(converters.fromLocalDate(date)))
        assertNull(converters.toLocalDate(null))
        assertNull(converters.fromLocalDate(null))
    }

    @Test
    fun category_roundTripsByName() {
        DrinkCategory.entries.forEach {
            assertEquals(it.name, converters.fromCategory(it))
            assertEquals(it, converters.toCategory(converters.fromCategory(it)))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun category_unknownNameThrows() {
        converters.toCategory("CIDER")
    }
}
