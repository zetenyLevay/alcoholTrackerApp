package com.example.alcoholtracker.domain.logic.handlers

import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.remote.BeerRemoteSource
import com.example.alcoholtracker.domain.logic.DrinkCategoryHandler
import com.example.alcoholtracker.domain.model.DrinkUnit
import javax.inject.Inject

class WineHandler @Inject constructor(
    private val source: BeerRemoteSource
) : DrinkCategoryHandler {
    override suspend fun fetchSuggestions(query: String): List<Drink> {
        return listOf(
            Drink(1, "White Wine", 13.0, "Wine"),
            Drink(2, "Red Wine", 14.0, "Wine")
        )
    }

    override fun getUnitOptions(): List<DrinkUnit> {
        return listOf(
            DrinkUnit("Small glass", 125),
            DrinkUnit("Medium glass", 175),
            DrinkUnit("Large glass", 250),
            DrinkUnit("Bottle", 750),
            DrinkUnit("milliliters", 1)
        )
    }
}