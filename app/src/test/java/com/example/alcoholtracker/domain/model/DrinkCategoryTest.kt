package com.example.alcoholtracker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DrinkCategoryTest {

    @Test
    fun fromLabel_matchesDisplayNameIgnoringCase() {
        assertEquals(DrinkCategory.BEER, drinkCategoryFromLabel("Beer"))
        assertEquals(DrinkCategory.BEER, drinkCategoryFromLabel("beer"))
        assertEquals(DrinkCategory.WINE, drinkCategoryFromLabel("Wine"))
        assertEquals(DrinkCategory.SPIRIT, drinkCategoryFromLabel("Spirit"))
        assertEquals(DrinkCategory.COCKTAIL, drinkCategoryFromLabel("Cocktail"))
        assertEquals(DrinkCategory.OTHER, drinkCategoryFromLabel("Other"))
    }

    @Test
    fun fromLabel_matchesEnumName() {
        DrinkCategory.entries.forEach { assertEquals(it, drinkCategoryFromLabel(it.name)) }
    }

    @Test
    fun fromLabel_roundTripsDisplayNames() {
        DrinkCategory.entries.forEach { assertEquals(it, drinkCategoryFromLabel(it.nameString)) }
    }

    @Test
    fun fromLabel_unknownFallsBackToOther() {
        assertEquals(DrinkCategory.OTHER, drinkCategoryFromLabel("Cider"))
        assertEquals(DrinkCategory.OTHER, drinkCategoryFromLabel("Mead"))
    }

    @Test
    fun fromLabel_blankFallsBackToOther() {
        assertEquals(DrinkCategory.OTHER, drinkCategoryFromLabel(""))
        assertEquals(DrinkCategory.OTHER, drinkCategoryFromLabel("   "))
    }

    @Test
    fun ids_areUnique() {
        val ids = DrinkCategory.entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun defaultAbv_isWithinRange() {
        DrinkCategory.entries.forEach { assertTrue(it.name, it.defaultABV in 0.0..100.0) }
    }
}
