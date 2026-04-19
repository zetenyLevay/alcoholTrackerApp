package com.example.alcoholtracker.data.repository


import com.example.alcoholtracker.data.local.dao.DrinkDao
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.domain.logic.handlers.DrinkHandlerRegistry
import com.example.alcoholtracker.domain.model.DrinkCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrinkRepository @Inject constructor(
    private val drinkDao: DrinkDao,
    private val handlerRegistry: DrinkHandlerRegistry
) {
    suspend fun getAllDrinks(): List<Drink> = drinkDao.getAllDrinks()

    suspend fun insertDrink(drink: Drink){
        drinkDao.insertDrink(drink)
    }



    suspend fun searchApiDrinks(query: String, category: DrinkCategory) : List<Drink>{
        return when (category) {
            DrinkCategory.BEER -> handlerRegistry.beerHandler.fetchSuggestions(query)
            DrinkCategory.WINE -> handlerRegistry.wineHandler.fetchSuggestions(query)
            DrinkCategory.SPIRIT -> handlerRegistry.spiritHandler.fetchSuggestions(query)
            DrinkCategory.COCKTAIL -> handlerRegistry.cocktailHandler.fetchSuggestions(query)
            DrinkCategory.OTHER -> handlerRegistry.otherHandler.fetchSuggestions(query)
        }
    }
}
