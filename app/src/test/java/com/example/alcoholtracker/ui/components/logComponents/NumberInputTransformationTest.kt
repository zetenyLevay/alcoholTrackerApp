package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.input.KeyboardType
import com.example.alcoholtracker.utils.DrinkFormNumbers
import com.example.alcoholtracker.utils.NumberInputSpec
import org.junit.Assert.assertEquals
import org.junit.Test

class NumberInputTransformationTest {

    private fun type(spec: NumberInputSpec, keys: String, initial: String = ""): String {
        val state = TextFieldState(initial)
        val transformation = NumberInputTransformation(spec)
        keys.forEach { key ->
            state.edit {
                append(key.toString())
                with(transformation) { transformInput() }
            }
        }
        return state.text.toString()
    }

    private fun paste(spec: NumberInputSpec, text: String, initial: String = ""): String {
        val state = TextFieldState(initial)
        val transformation = NumberInputTransformation(spec)
        state.edit {
            replace(0, length, text)
            with(transformation) { transformInput() }
        }
        return state.text.toString()
    }

    @Test
    fun commaBecomesDot() {
        assertEquals("2.7", type(DrinkFormNumbers.AMOUNT_UNITS, "2,7"))
        assertEquals("12.5", paste(DrinkFormNumbers.PRICE, "12,5"))
    }

    @Test
    fun nonDigitsAreRejected() {
        assertEquals("", type(DrinkFormNumbers.PRICE, "abc"))
        assertEquals("5", type(DrinkFormNumbers.PRICE, "5a"))
        assertEquals("5", type(DrinkFormNumbers.PRICE, "-5"))
        assertEquals("5", type(DrinkFormNumbers.PRICE, "5 "))
    }

    @Test
    fun onlyOneDecimalPoint() {
        assertEquals("2.5", type(DrinkFormNumbers.PRICE, "2.5."))
        assertEquals("2.5", type(DrinkFormNumbers.PRICE, "2.5,"))
        assertEquals("7", paste(DrinkFormNumbers.PRICE, "1.2.3", initial = "7"))
        assertEquals("7", paste(DrinkFormNumbers.PRICE, "1,2,3", initial = "7"))
    }

    @Test
    fun decimalsAreCapped() {
        assertEquals("4.6", type(DrinkFormNumbers.ABV, "4.65"))
        assertEquals("3.33", type(DrinkFormNumbers.PRICE, "3.333"))
        assertEquals("2.25", type(DrinkFormNumbers.AMOUNT_UNITS, "2.257"))
    }

    @Test
    fun integerDigitsAreCapped() {
        assertEquals("12345", type(DrinkFormNumbers.AMOUNT_ML, "123456"))
        assertEquals("9999", type(DrinkFormNumbers.PRICE, "99999"))
        assertEquals("999", type(DrinkFormNumbers.AMOUNT_UNITS, "9999"))
    }

    @Test
    fun wholeNumberFieldRejectsDecimalPoint() {
        assertEquals("505", type(DrinkFormNumbers.AMOUNT_ML, "50.5"))
        assertEquals("505", type(DrinkFormNumbers.AMOUNT_ML, "50,5"))
    }

    @Test
    fun leadingZeroIsReplaced() {
        assertEquals("5", type(DrinkFormNumbers.PRICE, "5", initial = "0"))
        assertEquals("0", type(DrinkFormNumbers.PRICE, "0", initial = "0"))
        assertEquals("500", type(DrinkFormNumbers.AMOUNT_ML, "500", initial = "0"))
    }

    @Test
    fun decimalsAfterZeroAreKept() {
        assertEquals("0.5", type(DrinkFormNumbers.PRICE, ".5", initial = "0"))
        assertEquals("0.05", type(DrinkFormNumbers.PRICE, ",05", initial = "0"))
    }

    @Test
    fun leadingDotGetsZero() {
        assertEquals("0.", type(DrinkFormNumbers.PRICE, "."))
        assertEquals("0.5", type(DrinkFormNumbers.PRICE, ",5"))
    }

    @Test
    fun valuesAboveMaxAreRejected() {
        assertEquals("100", type(DrinkFormNumbers.ABV, "100"))
        assertEquals("10", type(DrinkFormNumbers.ABV, "101"))
        assertEquals("100.", type(DrinkFormNumbers.ABV, "100.5"))
        assertEquals("99.9", type(DrinkFormNumbers.ABV, "99.9"))
    }

    @Test
    fun clearingIsAllowed() {
        assertEquals("", paste(DrinkFormNumbers.ABV, "", initial = "4.6"))
    }

    @Test
    fun keyboardMatchesDecimalSupport() {
        assertEquals(KeyboardType.Decimal, NumberInputTransformation(DrinkFormNumbers.ABV).keyboardOptions.keyboardType)
        assertEquals(KeyboardType.Decimal, NumberInputTransformation(DrinkFormNumbers.AMOUNT_UNITS).keyboardOptions.keyboardType)
        assertEquals(KeyboardType.Number, NumberInputTransformation(DrinkFormNumbers.AMOUNT_ML).keyboardOptions.keyboardType)
    }
}
