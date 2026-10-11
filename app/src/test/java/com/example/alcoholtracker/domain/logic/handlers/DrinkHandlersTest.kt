package com.example.alcoholtracker.domain.logic.handlers

import com.example.alcoholtracker.domain.logic.DrinkCategoryHandler
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.drinkCategoryFromLabel
import com.example.alcoholtracker.utils.isMilliliters
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrinkHandlersTest {

    private val registry = testHandlerRegistry()

    private val handlers: Map<String, DrinkCategoryHandler> = mapOf(
        "beer" to registry.beerHandler,
        "wine" to registry.wineHandler,
        "cocktail" to registry.cocktailHandler,
        "spirit" to registry.spiritHandler,
        "other" to registry.otherHandler,
    )

    private fun DrinkCategoryHandler.size(name: String) = getUnitOptions().first { it.name == name }.amount

    @Test
    fun everyHandler_endsWithMilliliters() {
        handlers.forEach { (category, handler) ->
            val last = handler.getUnitOptions().last()
            assertTrue(category, last.isMilliliters)
            assertEquals(category, 1, last.amount)
        }
    }

    @Test
    fun everyHandler_hasExactlyOneMillilitersOption() {
        handlers.forEach { (category, handler) ->
            assertEquals(category, 1, handler.getUnitOptions().count { it.isMilliliters })
        }
    }

    @Test
    fun containerUnits_havePositiveSizes() {
        handlers.forEach { (category, handler) ->
            handler.getUnitOptions().filterNot { it.isMilliliters }.forEach {
                assertTrue("$category ${it.name}", it.amount > 1)
            }
        }
    }

    @Test
    fun unitNames_areShortAndDoNotRepeatTheSize() {
        handlers.forEach { (category, handler) ->
            handler.getUnitOptions().forEach {
                assertFalse("$category ${it.name}", it.name.contains("("))
                assertFalse("$category ${it.name}", it.name.contains("ml "))
                assertTrue("$category ${it.name}", it.name.length <= 17)
            }
        }
    }

    @Test
    fun unitNames_areUniquePerHandler() {
        handlers.forEach { (category, handler) ->
            val names = handler.getUnitOptions().map { it.name }
            assertEquals(category, names.size, names.toSet().size)
        }
    }

    @Test
    fun knownSizes() {
        assertEquals(500, registry.beerHandler.size("Can"))
        assertEquals(473, registry.beerHandler.size("Pint"))
        assertEquals(330, registry.beerHandler.size("Bottle"))
        assertEquals(300, registry.beerHandler.size("Small bottle"))
        assertEquals(750, registry.wineHandler.size("Bottle"))
        assertEquals(125, registry.wineHandler.size("Small glass"))
        assertEquals(25, registry.spiritHandler.size("Shot"))
        assertEquals(50, registry.spiritHandler.size("Double shot"))
        assertEquals(150, registry.cocktailHandler.size("Standard cocktail"))
    }

    @Test
    fun staticSuggestions_belongToTheirCategory() = runBlocking {
        val expected = mapOf(
            registry.wineHandler to DrinkCategory.WINE,
            registry.spiritHandler to DrinkCategory.SPIRIT,
            registry.cocktailHandler to DrinkCategory.COCKTAIL,
        )
        expected.forEach { (handler, category) ->
            val suggestions = handler.fetchSuggestions("")
            assertTrue(suggestions.isNotEmpty())
            suggestions.forEach { assertEquals(it.name, category, drinkCategoryFromLabel(it.category)) }
        }
    }
}
