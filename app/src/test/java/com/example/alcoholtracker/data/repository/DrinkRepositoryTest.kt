package com.example.alcoholtracker.data.repository

import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.domain.logic.handlers.testHandlerRegistry
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.testutil.FakeDrinkDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DrinkRepositoryTest {

    private val dao = FakeDrinkDao()
    private val registry = testHandlerRegistry()
    private val repository = DrinkRepository(dao, registry)

    @Test
    fun insertedDrinks_areReturned() = runBlocking {
        val drink = Drink(name = "House Red", alcoholContent = 13.0, category = "Wine")
        repository.insertDrink(drink)
        assertEquals(listOf(drink), repository.getAllDrinks())
    }

    @Test
    fun searchApiDrinks_routesToCategoryHandler() = runBlocking {
        assertEquals(registry.wineHandler.fetchSuggestions("x"), repository.searchApiDrinks("x", DrinkCategory.WINE))
        assertEquals(registry.spiritHandler.fetchSuggestions("x"), repository.searchApiDrinks("x", DrinkCategory.SPIRIT))
        assertEquals(registry.cocktailHandler.fetchSuggestions("x"), repository.searchApiDrinks("x", DrinkCategory.COCKTAIL))
    }
}
