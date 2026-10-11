package com.example.alcoholtracker.testutil

import com.example.alcoholtracker.data.auth.AuthUser
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import java.time.LocalDateTime

val testUser = AuthUser(uid = "user", isAnonymous = false, email = "me@example.com")
val testGuest = AuthUser(uid = "guest", isAnonymous = true, email = null)

fun testLog(
    logId: Int = 0,
    name: String = "Heineken",
    userId: String = testUser.uid,
    category: DrinkCategory = DrinkCategory.BEER,
    cost: Double? = 4.5,
    abv: Double? = 5.0,
    amount: Int = 500,
    isFavorite: Boolean = false,
    recipient: String? = "Me",
    date: LocalDateTime = LocalDateTime.of(2026, 10, 10, 21, 0),
    unit: DrinkUnit? = DrinkUnit("Can", 500),
    inputAmount: Double? = 1.0,
) = DrinkLog(
    logId = logId,
    drinkId = null,
    userId = userId,
    name = name,
    cost = cost,
    alcoholPercentage = abv,
    amount = amount,
    category = category,
    recipient = recipient,
    inputAmount = inputAmount,
    isFavorite = isFavorite,
    imgURI = null,
    notes = null,
    locationName = null,
    longitude = null,
    latitude = null,
    drinkUnit = unit,
    date = date
)
