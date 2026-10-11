package com.example.alcoholtracker.domain.logic.handlers

import android.content.ContextWrapper
import com.example.alcoholtracker.data.remote.BeerRemoteSource

// BeerRemoteSource reads Android assets; only constructed here, never called on the JVM
fun unusedBeerSource() = BeerRemoteSource(ContextWrapper(null))

fun testHandlerRegistry(source: BeerRemoteSource = unusedBeerSource()) = DrinkHandlerRegistry(
    beerHandler = BeerHandler(source),
    wineHandler = WineHandler(source),
    cocktailHandler = CocktailHandler(source),
    spiritHandler = SpiritHandler(source),
    otherHandler = OtherHandler(source)
)
