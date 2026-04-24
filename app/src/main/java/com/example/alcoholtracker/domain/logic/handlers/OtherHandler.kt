package com.example.alcoholtracker.domain.logic.handlers

import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.remote.BeerRemoteSource
import com.example.alcoholtracker.domain.logic.DrinkCategoryHandler
import com.example.alcoholtracker.domain.model.DrinkUnit
import javax.inject.Inject

class OtherHandler @Inject constructor(
    private val source: BeerRemoteSource
) : DrinkCategoryHandler {
    override suspend fun fetchSuggestions(query: String): List<Drink> {
        return source.getBeers(query)
    }

    override fun getUnitOptions(): List<DrinkUnit> {
        return listOf(
            DrinkUnit("Can (500 ml)", 500),
            DrinkUnit("Small Bottle (300 ml)", 300),
            DrinkUnit("Bottle (330 ml)", 330),
            DrinkUnit("milliliters", 1)
        )
    }
}