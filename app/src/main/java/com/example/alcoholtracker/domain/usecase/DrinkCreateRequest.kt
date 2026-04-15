package com.example.alcoholtracker.domain.usecase

import androidx.room.ColumnInfo
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import java.time.LocalDateTime

data class DrinkCreateRequest(
    val logId: Int?,
    val name: String,
    val category: DrinkCategory,
    val abv: Double?,
    val volume: Int,
    val cost: Double?,
    val recipient: String?,
    val inputAmount: Double?,
    val isFavorite: Boolean,
    val imgURI: String?,
    val notes: String?,
    val locationName: String?,
    val longitude: Double?,
    val latitude: Double?,
    val drinkUnit: DrinkUnit?,
    val dateTime: LocalDateTime?
)

