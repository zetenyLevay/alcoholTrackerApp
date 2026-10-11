package com.example.alcoholtracker.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.alcoholtracker.data.local.database.AlcoholTrackerDatabase
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class DrinkLogDaoTest {

    private lateinit var db: AlcoholTrackerDatabase
    private lateinit var dao: DrinkLogDao

    private val user = "user"
    private val otherUser = "other"
    private val night = LocalDateTime.of(2026, 10, 10, 20, 0)

    private fun log(
        name: String = "Heineken",
        userId: String = user,
        category: DrinkCategory = DrinkCategory.BEER,
        abv: Double? = 5.0,
        amount: Int = 500,
        cost: Double? = 4.5,
        isFavorite: Boolean = false,
        recipient: String? = "Me",
        notes: String? = null,
        date: LocalDateTime = night,
        logId: Int = 0,
    ) = DrinkLog(
        logId = logId,
        drinkId = null,
        userId = userId,
        name = name,
        cost = cost,
        alcoholPercentage = abv,
        amount = amount,
        category = category,
        recipient = recipient,
        inputAmount = 1.0,
        isFavorite = isFavorite,
        imgURI = null,
        notes = notes,
        locationName = null,
        longitude = null,
        latitude = null,
        drinkUnit = DrinkUnit("Can", 500),
        date = date
    )

    private suspend fun insert(log: DrinkLog): Int = dao.insertDrinkLog(log).toInt()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AlcoholTrackerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.drinkLogDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_returnsNewRowId() = runBlocking {
        val first = insert(log())
        val second = insert(log())
        assertTrue(first > 0)
        assertEquals(first + 1, second)
        assertEquals("Heineken", dao.getDrinkById(first)?.name)
    }

    @Test
    fun reinsertingDeletedLog_restoresSameId() = runBlocking {
        val id = insert(log())
        val saved = dao.getDrinkById(id)!!
        dao.deleteDrinkLog(saved)
        assertNull(dao.getDrinkById(id))

        dao.insertDrinkLog(saved)
        assertEquals(saved, dao.getDrinkById(id))
    }

    @Test
    fun update_changesStoredLog() = runBlocking {
        val id = insert(log())
        dao.updateDrinkLog(dao.getDrinkById(id)!!.copy(name = "Guinness", isFavorite = true))
        val updated = dao.getDrinkById(id)!!
        assertEquals("Guinness", updated.name)
        assertTrue(updated.isFavorite)
    }

    @Test
    fun getDrinkByIdFlow_emitsNullAfterDelete() = runBlocking {
        val id = insert(log())
        assertNotNull(dao.getDrinkByIdFlow(id).first())
        dao.deleteDrinkLog(dao.getDrinkById(id)!!)
        assertNull(dao.getDrinkByIdFlow(id).first())
    }

    @Test
    fun getDrinkLogsByUserId_onlyReturnsThatUser() = runBlocking {
        insert(log(name = "Mine"))
        insert(log(name = "Theirs", userId = otherUser))
        assertEquals(listOf("Mine"), dao.getDrinkLogsByUserId(user).first().map { it.name })
    }

    @Test
    fun recent_groupsSameDrinkIgnoringNameCase() = runBlocking {
        insert(log(name = "Heineken", date = night))
        val latest = insert(log(name = "heineken", date = night.plusHours(1)))
        insert(log(name = "Guinness", date = night.plusMinutes(30)))

        val recent = dao.getRecentLogs(user).first()

        assertEquals(2, recent.size)
        assertEquals(latest, recent[0].logId)
        assertEquals("Guinness", recent[1].name)
    }

    @Test
    fun recent_separatesDifferentAmountsAndAbv() = runBlocking {
        insert(log(amount = 500))
        insert(log(amount = 330))
        insert(log(abv = 0.0))
        assertEquals(3, dao.getRecentLogs(user).first().size)
    }

    @Test
    fun recent_isLimitedTo20AndExcludesOtherUsers() = runBlocking {
        repeat(25) { insert(log(name = "Drink $it", date = night.plusMinutes(it.toLong()))) }
        insert(log(name = "Not mine", userId = otherUser, date = night.plusDays(1)))

        val recent = dao.getRecentLogs(user).first()

        assertEquals(20, recent.size)
        assertEquals("Drink 24", recent.first().name)
        assertTrue(recent.none { it.userId == otherUser })
    }

    @Test
    fun frequent_ordersByCountThenRecency() = runBlocking {
        repeat(3) { insert(log(name = "Often", date = night.minusDays(it.toLong()))) }
        insert(log(name = "Once old", date = night.minusDays(5)))
        insert(log(name = "Once new", date = night.plusHours(2)))

        val frequent = dao.getFrequentLogs(user).first().map { it.name }

        assertEquals(listOf("Often", "Once new", "Once old"), frequent)
    }

    @Test
    fun favorites_keepDifferentPricesSeparate() = runBlocking {
        insert(log(cost = 0.0, isFavorite = true))
        insert(log(cost = 4.5, isFavorite = true))
        insert(log(cost = 4.5, isFavorite = true, date = night.plusHours(1)))
        insert(log(name = "Guinness", isFavorite = false))

        val favorites = dao.getFavoritesLogs(user).first()

        assertEquals(2, favorites.size)
        assertEquals(setOf(0.0, 4.5), favorites.map { it.cost }.toSet())
    }

    @Test
    fun setFavoriteForGroup_onlyTouchesMatchingGroup() = runBlocking {
        val paid = insert(log(cost = 4.5))
        val paidAgain = insert(log(name = "HEINEKEN", cost = 4.5))
        val free = insert(log(cost = 0.0))
        val otherAmount = insert(log(amount = 330, cost = 4.5))
        val otherUsers = insert(log(userId = otherUser, cost = 4.5))

        dao.setFavoriteForGroup(user, "heineken", DrinkCategory.BEER, 5.0, 500, 4.5, true)

        assertTrue(dao.getDrinkById(paid)!!.isFavorite)
        assertTrue(dao.getDrinkById(paidAgain)!!.isFavorite)
        assertEquals(false, dao.getDrinkById(free)!!.isFavorite)
        assertEquals(false, dao.getDrinkById(otherAmount)!!.isFavorite)
        assertEquals(false, dao.getDrinkById(otherUsers)!!.isFavorite)
    }

    @Test
    fun setFavoriteForGroup_matchesMissingPriceAndAbv() = runBlocking {
        val noPrice = insert(log(cost = null, abv = null))
        val zeroPrice = insert(log(cost = 0.0, abv = null))

        dao.setFavoriteForGroup(user, "Heineken", DrinkCategory.BEER, null, 500, null, true)

        assertTrue(dao.getDrinkById(noPrice)!!.isFavorite)
        assertEquals(false, dao.getDrinkById(zeroPrice)!!.isFavorite)
    }

    @Test
    fun setFavoriteForGroup_canUnfavorite() = runBlocking {
        val id = insert(log(isFavorite = true))
        dao.setFavoriteForGroup(user, "Heineken", DrinkCategory.BEER, 5.0, 500, 4.5, false)
        assertEquals(false, dao.getDrinkById(id)!!.isFavorite)
        assertTrue(dao.getFavoritesLogs(user).first().isEmpty())
    }

    @Test
    fun reassignLogs_movesOnlyGuestLogs() = runBlocking {
        insert(log(name = "Guest beer", userId = "guest"))
        insert(log(name = "Account beer", userId = "account"))
        insert(log(name = "Someone else", userId = otherUser))

        dao.reassignLogs("guest", "account")

        assertEquals(setOf("Guest beer", "Account beer"), dao.getDrinkLogsByUserId("account").first().map { it.name }.toSet())
        assertTrue(dao.getDrinkLogsByUserId("guest").first().isEmpty())
        assertEquals(1, dao.getDrinkLogsByUserId(otherUser).first().size)
    }

    @Test
    fun tonightLogs_includeWindowBoundsOnly() = runBlocking {
        val start = LocalDateTime.of(2026, 10, 10, 6, 0)
        val end = start.plusDays(1).minusNanos(1)
        insert(log(name = "Before", date = start.minusMinutes(1)))
        insert(log(name = "Start", date = start))
        insert(log(name = "Late", date = LocalDateTime.of(2026, 10, 11, 5, 59)))
        insert(log(name = "Next session", date = start.plusDays(1)))
        insert(log(name = "Other user", userId = otherUser, date = start.plusHours(14)))

        val names = dao.getTonightLogs(user, start, end).first().map { it.name }.toSet()

        assertEquals(setOf("Start", "Late"), names)
    }

    @Test
    fun filterBounds_coverOnlyThatUser() = runBlocking {
        insert(log(cost = 2.0, abv = 4.0))
        insert(log(cost = 9.5, abv = 40.0))
        insert(log(userId = otherUser, cost = 100.0, abv = 90.0))

        val bounds = dao.getFilterBounds(user).first()!!

        assertEquals(2.0f, bounds.minPrice!!, 0.001f)
        assertEquals(9.5f, bounds.maxPrice!!, 0.001f)
        assertEquals(4.0f, bounds.minAbv!!, 0.001f)
        assertEquals(40.0f, bounds.maxAbv!!, 0.001f)
    }

    @Test
    fun recipients_areDistinct() = runBlocking {
        insert(log(recipient = "Me"))
        insert(log(recipient = "Anna"))
        insert(log(recipient = "Me"))
        insert(log(recipient = "Bob", userId = otherUser))

        assertEquals(setOf("Me", "Anna"), dao.getRecipients(user).first().toSet())
    }

    @Test
    fun recipients_skipLogsWithoutRecipient() = runBlocking {
        insert(log(recipient = "Me"))
        insert(log(recipient = null))

        assertEquals(listOf("Me"), dao.getRecipients(user).first())
    }

    @Test
    fun dailySummary_sumsMatchingLogsInRange() = runBlocking {
        val from = night.minusHours(1)
        val to = night.plusHours(5)
        insert(log(cost = 4.5, amount = 500, date = night))
        insert(log(name = "Wine", category = DrinkCategory.WINE, cost = 7.0, amount = 175, date = night.plusHours(1)))
        insert(log(cost = 100.0, amount = 1000, date = night.minusDays(1)))
        insert(log(userId = otherUser, cost = 50.0, date = night))

        val all = dao.getDailySummary(user, from, to, null, null, null, null, null, null, null, null)
        assertEquals(11.5, all.totalCost!!, 0.001)
        assertEquals(675.0, all.totalAmount!!, 0.001)

        val wineOnly = dao.getDailySummary(user, from, to, null, "WINE", null, null, null, null, null, null)
        assertEquals(7.0, wineOnly.totalCost!!, 0.001)
    }

    private suspend fun paged(
        query: String? = null,
        category: String? = null,
        recipient: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        minPrice: Float? = null,
        maxPrice: Float? = null,
        minAbv: Float? = null,
        maxAbv: Float? = null,
        isFavorite: Boolean? = null,
    ): List<String> {
        val source = dao.getPagedLogs(user, query, category, recipient, startDate, endDate, minPrice, maxPrice, minAbv, maxAbv, isFavorite)
        val result = source.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 100, placeholdersEnabled = false))
        return (result as PagingSource.LoadResult.Page).data.map { it.name }
    }

    @Test
    fun pagedLogs_newestFirstAndOnlyThatUser() = runBlocking {
        insert(log(name = "Old", date = night.minusDays(1)))
        insert(log(name = "New", date = night))
        insert(log(name = "Theirs", userId = otherUser))

        assertEquals(listOf("New", "Old"), paged())
    }

    @Test
    fun pagedLogs_filters() = runBlocking {
        insert(log(name = "Heineken", cost = 4.5, abv = 5.0, recipient = "Me", date = night))
        insert(log(name = "Red", category = DrinkCategory.WINE, cost = 8.0, abv = 13.0, recipient = "Anna", isFavorite = true, notes = "with dinner", date = night.minusDays(2)))
        insert(log(name = "Vodka", category = DrinkCategory.SPIRIT, cost = 3.0, abv = 40.0, recipient = "Me", date = night.minusDays(4)))

        assertEquals(listOf("Heineken"), paged(query = "hein"))
        assertEquals(listOf("Red"), paged(query = "dinner"))
        assertEquals(listOf("Red"), paged(category = "WINE"))
        assertEquals(listOf("Red"), paged(recipient = "Anna"))
        assertEquals(listOf("Red"), paged(isFavorite = true))
        assertEquals(listOf("Heineken", "Red"), paged(minPrice = 4f, maxPrice = 8f))
        assertEquals(listOf("Red", "Vodka"), paged(minAbv = 10f))
        assertEquals(listOf("Heineken", "Red"), paged(startDate = night.minusDays(3)))
        assertEquals(listOf("Red", "Vodka"), paged(endDate = night.minusDays(1)))
    }
}
