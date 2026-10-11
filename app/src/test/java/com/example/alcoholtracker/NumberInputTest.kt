package com.example.alcoholtracker

import com.example.alcoholtracker.utils.DrinkFormNumbers
import com.example.alcoholtracker.utils.format
import org.junit.Assert.assertEquals
import org.junit.Test

class NumberInputTest {

    @Test
    fun format_dropsTrailingZeros() {
        assertEquals("500", DrinkFormNumbers.AMOUNT_ML.format(500.0))
        assertEquals("2.5", DrinkFormNumbers.AMOUNT_UNITS.format(2.50))
        assertEquals("4.6", DrinkFormNumbers.ABV.format(4.6))
    }

    @Test
    fun format_roundsToAllowedDecimals() {
        assertEquals("4.7", DrinkFormNumbers.ABV.format(4.65))
        assertEquals("3.33", DrinkFormNumbers.PRICE.format(3.333))
        assertEquals("501", DrinkFormNumbers.AMOUNT_ML.format(500.6))
        assertEquals("0.3", DrinkFormNumbers.ABV.format(0.1 + 0.1 + 0.1))
    }

    @Test
    fun format_clampsToRange() {
        assertEquals("0", DrinkFormNumbers.PRICE.format(-0.1))
        assertEquals("100", DrinkFormNumbers.ABV.format(100.1))
        assertEquals("0", DrinkFormNumbers.ABV.format(0.0))
    }

    @Test
    fun format_neverUsesScientificNotation() {
        assertEquals("1000", DrinkFormNumbers.AMOUNT_ML.format(1000.0))
    }
}
