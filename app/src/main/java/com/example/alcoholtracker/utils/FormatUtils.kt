package com.example.alcoholtracker.utils

import java.math.BigDecimal

fun formatCost(cost: Double): String = "€%.2f".format(cost)

fun formatVolume(milliliters: Number): String = "${trimNumber(milliliters.toDouble())} ml"

fun formatAbv(abv: Double): String = "${trimNumber(abv)}% ABV"

fun formatDrinkCount(count: Int): String = if (count == 1) "1 drink" else "$count drinks"

private fun trimNumber(value: Double): String =
    BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
