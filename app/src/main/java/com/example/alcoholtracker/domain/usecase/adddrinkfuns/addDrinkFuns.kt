package com.example.alcoholtracker.domain.usecase.adddrinkfuns

import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.domain.usecase.DrinkCreateRequest
import com.example.alcoholtracker.ui.viewmodel.DrinkFormUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun getFinalAmount(unit: DrinkUnit?, amount: Double?): Int {

    return if (unit != null && amount != null) {
        (unit.amount * amount).toInt()
    } else 0
}

fun getLocalDateTime(date: LocalDate, time: LocalTime): LocalDateTime {
    return LocalDateTime.of(date, time)
}

fun createNewRequest(drink: DrinkFormUiState): DrinkCreateRequest{
    val request = DrinkCreateRequest(
        name = drink.drinkName,
        category = drink.selectedCategory,
        abv = drink.alcoholPercentage,
        volume = getFinalAmount(drink.selectedDrinkUnit, drink.inputAmount),
        cost = drink.cost,
        recipient = drink.recipient,
        inputAmount = drink.inputAmount,
        drinkUnit = drink.selectedDrinkUnit,
        dateTime = getLocalDateTime(drink.selectedDate, drink.selectedTime),
        logId = drink.logId,
        isFavorite = drink.isFavorite,
        imgURI = null,
        notes = drink.notes,
        locationName = drink.locationName,
        longitude = drink.longitude,
        latitude = drink.latitude
    )
    return request
}

fun createNewRequest(drink: UserDrinkLog): DrinkCreateRequest {
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

fun checkNewUnit(previousUnit: DrinkUnit, newUnit: DrinkUnit): Int{
    val isCurrentUnitMilliliters = newUnit.name == "milliliters"
                    val wasPreviousUnitMilliliters = previousUnit.name == "milliliters"

    return when {
        !isCurrentUnitMilliliters && wasPreviousUnitMilliliters -> {
            1
        }

        !wasPreviousUnitMilliliters && isCurrentUnitMilliliters -> {
            100
        }

        else -> {
            0
        }
    }

}
