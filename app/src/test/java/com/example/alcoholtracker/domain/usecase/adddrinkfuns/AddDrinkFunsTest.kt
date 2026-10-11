package com.example.alcoholtracker.domain.usecase.adddrinkfuns

import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AddDrinkFunsTest {

    private val ml = DrinkUnit("milliliters", 1)
    private val can = DrinkUnit("Can", 500)
    private val pint = DrinkUnit("Pint", 473)
    private val glass = DrinkUnit("Glass", 100)

    @Test
    fun getFinalAmount_multipliesUnitSize() {
        assertEquals(330, getFinalAmount(ml, 330.0))
        assertEquals(1000, getFinalAmount(can, 2.0))
        assertEquals(1250, getFinalAmount(can, 2.5))
    }

    @Test
    fun getFinalAmount_roundsToNearestMilliliter() {
        assertEquals(230, getFinalAmount(glass, 2.3))
        assertEquals(710, getFinalAmount(pint, 1.5))
        assertEquals(156, getFinalAmount(pint, 0.33))
    }

    @Test
    fun getFinalAmount_missingInputIsZero() {
        assertEquals(0, getFinalAmount(null, 2.0))
        assertEquals(0, getFinalAmount(can, null))
    }

    @Test
    fun getLocalDateTime_combinesDateAndTime() {
        assertEquals(
            LocalDateTime.of(2026, 10, 11, 22, 30),
            getLocalDateTime(LocalDate.of(2026, 10, 11), LocalTime.of(22, 30))
        )
    }

    @Test
    fun createNewRequest_copiesEveryField() {
        val log = DrinkLog(
            logId = 7,
            drinkId = 3,
            userId = "user",
            name = "Heineken",
            cost = 4.5,
            alcoholPercentage = 5.0,
            amount = 1000,
            category = DrinkCategory.BEER,
            recipient = "Anna",
            inputAmount = 2.0,
            isFavorite = true,
            imgURI = "uri",
            notes = "notes",
            locationName = "Pub",
            longitude = 19.04,
            latitude = 47.5,
            drinkUnit = can,
            date = LocalDateTime.of(2026, 10, 11, 21, 0)
        )

        val request = createNewRequest(log)

        assertEquals(7, request.logId)
        assertEquals("Heineken", request.name)
        assertEquals(DrinkCategory.BEER, request.category)
        assertEquals(5.0, request.abv!!, 0.0)
        assertEquals(1000, request.volume)
        assertEquals(4.5, request.cost!!, 0.0)
        assertEquals("Anna", request.recipient)
        assertEquals(2.0, request.inputAmount!!, 0.0)
        assertEquals(true, request.isFavorite)
        assertEquals("uri", request.imgURI)
        assertEquals("notes", request.notes)
        assertEquals("Pub", request.locationName)
        assertEquals(19.04, request.longitude!!, 0.0)
        assertEquals(47.5, request.latitude!!, 0.0)
        assertEquals(can, request.drinkUnit)
        assertEquals(LocalDateTime.of(2026, 10, 11, 21, 0), request.dateTime)
    }

    @Test
    fun checkNewUnit_resetsOnlyWhenCrossingMilliliters() {
        assertEquals(1, checkNewUnit(ml, can))
        assertEquals(100, checkNewUnit(can, ml))
        assertEquals(0, checkNewUnit(can, pint))
        assertEquals(0, checkNewUnit(ml, ml))
    }
}
