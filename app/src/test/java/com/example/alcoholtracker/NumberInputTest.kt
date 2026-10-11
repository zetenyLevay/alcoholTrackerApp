package com.example.alcoholtracker

import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.utils.DrinkFormNumbers
import com.example.alcoholtracker.utils.format
import com.example.alcoholtracker.utils.isMilliliters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
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

    @Test
    fun format_keepsAllowedPrecision() {
        assertEquals("12.05", DrinkFormNumbers.PRICE.format(12.05))
        assertEquals("2.25", DrinkFormNumbers.AMOUNT_UNITS.format(2.25))
        assertEquals("99999", DrinkFormNumbers.AMOUNT_ML.format(99999.0))
    }

    @Test
    fun format_specWithoutMaxValueIsOnlyClampedAtZero() {
        assertEquals("9999.99", DrinkFormNumbers.PRICE.format(9999.99))
    }

    @Test
    fun isMilliliters_onlyForTheMillilitersUnit() {
        assertTrue(DrinkUnit("milliliters", 1).isMilliliters)
        assertFalse(DrinkUnit("Can", 500).isMilliliters)
        assertFalse(DrinkUnit("Milliliters", 1).isMilliliters)
    }

    @Test
    fun amountFor_picksSpecByUnit() {
        assertSame(DrinkFormNumbers.AMOUNT_ML, DrinkFormNumbers.amountFor(null))
        assertSame(DrinkFormNumbers.AMOUNT_ML, DrinkFormNumbers.amountFor(DrinkUnit("milliliters", 1)))
        assertSame(DrinkFormNumbers.AMOUNT_UNITS, DrinkFormNumbers.amountFor(DrinkUnit("Pint", 473)))
    }

    @Test
    fun specs_matchFieldRules() {
        assertEquals(0, DrinkFormNumbers.AMOUNT_ML.maxDecimals)
        assertEquals(1, DrinkFormNumbers.ABV.maxDecimals)
        assertEquals(100.0, DrinkFormNumbers.ABV.maxValue!!, 0.0)
        assertEquals(2, DrinkFormNumbers.PRICE.maxDecimals)
        assertEquals(2, DrinkFormNumbers.AMOUNT_UNITS.maxDecimals)
    }
}
