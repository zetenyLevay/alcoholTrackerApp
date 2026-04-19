package com.example.alcoholtracker.domain.usecase

import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import javax.inject.Inject


class DrinkHandler @Inject constructor(
    private val logRepo: DrinkLogRepository,
    private val auth: FirebaseAuth
) {
    suspend fun createDrink(
        request: DrinkCreateRequest
    ) {
        val userId = auth.currentUser?.uid ?: return
        val drink = UserDrinkLog(
            userId = userId,
            drinkId = 0,
            name = request.name,
            category = request.category,
            alcoholPercentage = request.abv,
            amount = request.volume,
            cost = request.cost,
            recipient = request.recipient,
            inputAmount = request.inputAmount,
            drinkUnit = request.drinkUnit,
            isFavorite = false,
            imgURI = null,
            notes = request.notes,
            locationName = request.locationName,
            latitude = null,
            longitude = null,

            date = request.dateTime ?: LocalDateTime.now(),

            )
        logRepo.insertDrinkLog(drink)

    }

    suspend fun editDrink(
        drinkToUpdate: Int,
        request: DrinkCreateRequest

    ) {
        val userId = auth.currentUser?.uid ?: return
        val drink = UserDrinkLog(
            logId = drinkToUpdate,
            userId = userId,
            drinkId = 0,
            name = request.name,
            category = request.category,
            alcoholPercentage = request.abv,
            amount = request.volume,
            cost = request.cost,
            recipient = request.recipient,
            inputAmount = request.inputAmount,
            drinkUnit = request.drinkUnit,
            isFavorite = request.isFavorite,
            imgURI = request.imgURI,
            notes = request.notes,
            locationName = request.locationName,
            latitude = request.latitude,
            longitude = request.longitude,

            date = request.dateTime ?: LocalDateTime.now(),

            )
        logRepo.updateDrinkLog(drink)
    }

    suspend fun deleteDrink(
        drinkToDelete: UserDrinkLog
    ) {
        logRepo.deleteDrinkLog(drinkToDelete)
    }
}

