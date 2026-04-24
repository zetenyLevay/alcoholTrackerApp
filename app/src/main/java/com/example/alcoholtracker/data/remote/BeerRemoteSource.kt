package com.example.alcoholtracker.data.remote

import android.content.Context
import com.example.alcoholtracker.data.model.Drink
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BeerRemoteSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun getBeers(query: String): List<Drink> {

        val beerList = mutableListOf(Beer("Mock", 0.0))
        val drinkList: MutableList<Drink> = mutableListOf()

        val jsonString = context.assets.open("beerSearchTest.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val result = gson.fromJson(jsonString, BeerSearchResult::class.java)

        for (beerItem in result.beers.items) {
            beerList.add(beerItem.beer)
            val newDrink = Drink(
                name = beerItem.beer.name,
                alcoholContent = beerItem.beer.abv,
                category = "Beer",
            )
            drinkList.add(newDrink)
        }
        if (query.isBlank()) return drinkList

        return drinkList.filter { drink ->
            drink.name.contains(query, ignoreCase = true)
        }
    }
}
