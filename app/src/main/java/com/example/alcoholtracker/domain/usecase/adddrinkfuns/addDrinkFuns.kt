package com.example.alcoholtracker.domain.usecase.adddrinkfuns

import android.util.Log
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.domain.usecase.DrinkCreateRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun getFinalAmount(unit: DrinkUnit?, amount: Double?): Int {

    Log.d("DrinkUnit", "Unit: $unit, Amount: $amount")

    return if (unit != null && amount != null) {
        (unit.amount * amount).toInt()
    } else 0
}

fun getLocalDateTime(date: LocalDate, time: LocalTime): LocalDateTime {
    return LocalDateTime.of(date, time)
}

fun createNewRequest(drink: UserDrinkLog): DrinkCreateRequest{
    val request = DrinkCreateRequest(
        name = drink.name,
        category = drink.category,
        abv = drink.alcoholPercentage,
        volume = drink.amount,
        cost = drink.cost,
        recipient = drink.recipient,
        inputAmount = drink.inputAmount,
        drinkUnit = drink.drinkUnit,
        dateTime = drink.date,
        logId = drink.logId,
        isFavorite = drink.isFavorite,
        imgURI = drink.imgURI,
        notes = drink.notes,
        locationName = drink.locationName,
        longitude = drink.longitude,
        latitude = drink.latitude
    )
    return request
}

