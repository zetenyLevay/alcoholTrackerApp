package com.example.alcoholtracker.utils

import com.example.alcoholtracker.domain.model.DrinkUnit
import java.math.BigDecimal
import java.math.RoundingMode

data class NumberInputSpec(
    val maxIntegerDigits: Int,
    val maxDecimals: Int,
    val maxValue: Double? = null,
)

object DrinkFormNumbers {
    val ABV = NumberInputSpec(maxIntegerDigits = 3, maxDecimals = 1, maxValue = 100.0)
    val PRICE = NumberInputSpec(maxIntegerDigits = 4, maxDecimals = 2)
    val AMOUNT_ML = NumberInputSpec(maxIntegerDigits = 5, maxDecimals = 0)
    val AMOUNT_UNITS = NumberInputSpec(maxIntegerDigits = 3, maxDecimals = 2)

    fun amountFor(unit: DrinkUnit?): NumberInputSpec =
        if (unit == null || unit.isMilliliters) AMOUNT_ML else AMOUNT_UNITS
}

val DrinkUnit.isMilliliters: Boolean
    get() = name == "milliliters"

fun NumberInputSpec.format(value: Double): String {
    val clamped = value.coerceIn(0.0, maxValue ?: Double.MAX_VALUE)
    if (clamped == 0.0) return "0"
    return BigDecimal.valueOf(clamped)
        .setScale(maxDecimals, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}
