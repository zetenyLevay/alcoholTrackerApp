package com.example.alcoholtracker.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.alcoholtracker.domain.logic.handlers.BeerHandler
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.drinkCategoryFromLabel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BeerRemoteSourceTest {

    private val source = BeerRemoteSource(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    fun blankQuery_returnsAllBeers() {
        val beers = source.getBeers("")
        assertTrue(beers.isNotEmpty())
        beers.forEach { assertEquals(it.name, DrinkCategory.BEER, drinkCategoryFromLabel(it.category)) }
    }

    @Test
    fun query_filtersByNameIgnoringCase() {
        val all = source.getBeers("")
        val target = all.first().name
        val fragment = target.take(4).uppercase()

        val filtered = source.getBeers(fragment)

        assertTrue(filtered.isNotEmpty())
        assertTrue(filtered.all { it.name.contains(fragment, ignoreCase = true) })
        assertTrue(filtered.any { it.name == target })
    }

    @Test
    fun unknownQuery_returnsNothing() {
        assertTrue(source.getBeers("zzzz-no-such-beer").isEmpty())
    }

    @Test
    fun beerHandler_delegatesToSource() = runBlocking {
        assertEquals(source.getBeers("a"), BeerHandler(source).fetchSuggestions("a"))
    }
}
