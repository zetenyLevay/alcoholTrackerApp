package com.example.alcoholtracker.domain.logic.handlers

import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.remote.BeerRemoteSource
import com.example.alcoholtracker.domain.logic.DrinkCategoryHandler
import com.example.alcoholtracker.domain.model.DrinkUnit
import javax.inject.Inject

class SpiritHandler @Inject constructor(
    private val source: BeerRemoteSource
) : DrinkCategoryHandler {
    override suspend fun fetchSuggestions(query: String): List<Drink> {
        return listOf(
            Drink(3, "Vodka", 40.0, "Spirit"),
        )
    }

    override fun getUnitOptions(): List<DrinkUnit> {
        return listOf(
            DrinkUnit("Shot (25 ml)", 20),
            DrinkUnit("Double Shot (50 ml)", 50),
            DrinkUnit("Glass (100 ml)", 100),
            DrinkUnit("milliliters", 1)
        )
    }
}