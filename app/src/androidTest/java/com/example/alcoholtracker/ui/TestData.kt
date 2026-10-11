package com.example.alcoholtracker.ui

import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import java.time.LocalDateTime

fun testLog(
    logId: Int = 1,
    name: String = "Heineken",
    category: DrinkCategory = DrinkCategory.BEER,
    cost: Double? = 4.5,
    abv: Double? = 5.0,
    amount: Int = 500,
    isFavorite: Boolean = false,
) = DrinkLog(
    logId = logId,
    drinkId = null,
    userId = "user",
    name = name,
    cost = cost,
    alcoholPercentage = abv,
    amount = amount,
    category = category,
    recipient = "Me",
    inputAmount = 1.0,
    isFavorite = isFavorite,
    imgURI = null,
    notes = null,
    locationName = null,
    longitude = null,
    latitude = null,
    drinkUnit = DrinkUnit("Can", 500),
    date = LocalDateTime.of(2026, 10, 10, 21, 0)
)
