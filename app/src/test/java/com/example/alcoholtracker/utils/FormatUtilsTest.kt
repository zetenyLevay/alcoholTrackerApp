package com.example.alcoholtracker.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class FormatUtilsTest {

    private lateinit var originalLocale: Locale

    @Before
    fun pinLocale() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun formatCost_alwaysShowsTwoDecimals() {
        assertEquals("€6.20", formatCost(6.2))
        assertEquals("€0.00", formatCost(0.0))
        assertEquals("€12.35", formatCost(12.345))
        assertEquals("€1234.50", formatCost(1234.5))
    }

    @Test
    fun formatVolume_trimsTrailingZeros() {
        assertEquals("500 ml", formatVolume(500))
        assertEquals("500 ml", formatVolume(500.0))
        assertEquals("12.5 ml", formatVolume(12.5))
        assertEquals("1000 ml", formatVolume(1000))
    }

    @Test
    fun formatAbv_trimsTrailingZeros() {
        assertEquals("5% ABV", formatAbv(5.0))
        assertEquals("4.6% ABV", formatAbv(4.6))
        assertEquals("0% ABV", formatAbv(0.0))
    }

    @Test
    fun formatDrinkCount_pluralizes() {
        assertEquals("0 drinks", formatDrinkCount(0))
        assertEquals("1 drink", formatDrinkCount(1))
        assertEquals("2 drinks", formatDrinkCount(2))
    }
}
