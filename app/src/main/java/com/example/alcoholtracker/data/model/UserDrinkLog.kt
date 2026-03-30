package com.example.alcoholtracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.alcoholtracker.di.Converters
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import java.time.LocalDateTime

@Entity(tableName = "log")
data class UserDrinkLog(
    @ColumnInfo("logId")
    @PrimaryKey(autoGenerate = true) val logId: Int = 0,

    @ColumnInfo("drinkId")
    val drinkId: Int?,

    @ColumnInfo("userId")
    val userId: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("cost")
    val cost: Double?,

    @ColumnInfo("alcoholPercentage")
    val alcoholPercentage: Double?,

    @ColumnInfo("amount")
    val amount: Int,

    @ColumnInfo("category")
    val category: DrinkCategory,

    @ColumnInfo("recipient")
    val recipient: String?,

    @ColumnInfo("inputAmount")
    val inputAmount: Double?,

    @ColumnInfo("isFavorite")
    val isFavorite: Boolean,

    @ColumnInfo("imgURI")
    val imgURI: String?,

    @ColumnInfo("notes")
    val notes: String?,

    @ColumnInfo("locationName")
    val locationName: String?,

    @ColumnInfo("longitude")
    val longitude: Double?,

    @ColumnInfo("latitude")
    val latitude: Double?,


    @Embedded(prefix = "unit_")
    val drinkUnit: DrinkUnit?,

    @TypeConverters(Converters::class)
    @ColumnInfo("date")
    val date: LocalDateTime

)
