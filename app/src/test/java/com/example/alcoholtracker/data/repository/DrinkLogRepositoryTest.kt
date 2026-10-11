package com.example.alcoholtracker.data.repository

import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.FakeDrinkLogDao
import com.example.alcoholtracker.testutil.testGuest
import com.example.alcoholtracker.testutil.testLog
import com.example.alcoholtracker.testutil.testUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DrinkLogRepositoryTest {

    private val auth = FakeAuthService(testUser)
    private val dao = FakeDrinkLogDao()
    private val repository = DrinkLogRepository(dao, UserRepository(auth))

    @Test
    fun insert_stampsCurrentUserAndReturnsId() = runTest {
        val id = repository.insertDrinkLog(testLog(userId = ""))
        assertEquals("user", dao.getDrinkById(id.toInt())?.userId)
    }

    @Test(expected = Exception::class)
    fun insert_withoutUserThrows() = runTest {
        auth.signOut()
        repository.insertDrinkLog(testLog())
    }

    @Test
    fun lists_areEmptyWhenSignedOut() = runTest {
        dao.insertDrinkLog(testLog())
        auth.signOut()
        assertTrue(repository.getAllLogs().first().isEmpty())
        assertTrue(repository.getRecentLogs().first().isEmpty())
        assertTrue(repository.getFrequentLogs().first().isEmpty())
        assertTrue(repository.getFavoriteLogs().first().isEmpty())
        assertTrue(repository.getRecipients().first().isEmpty())
        assertTrue(repository.getTonightLogs().first().isEmpty())
        assertNull(repository.getFilterBounds().first())
    }

    @Test
    fun lists_followUserChanges() = runTest {
        dao.insertDrinkLog(testLog(name = "Mine"))
        dao.insertDrinkLog(testLog(name = "Guest drink", userId = "guest"))
        val seen = mutableListOf<List<String>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getRecentLogs().collect { list -> seen += list.map { it.name } }
        }

        auth.setUser(testGuest)
        auth.signOut()
        job.cancel()

        assertEquals(listOf(listOf("Mine"), listOf("Guest drink"), emptyList()), seen)
    }

    @Test
    fun reassignLogs_movesGuestDrinks() = runTest {
        dao.insertDrinkLog(testLog(userId = "guest"))
        repository.reassignLogs("guest", "user")
        assertEquals(1, repository.getAllLogs().first().size)
    }

    @Test
    fun setFavoriteForGroup_matchesPrice() = runTest {
        val paid = dao.insertDrinkLog(testLog(cost = 4.5)).toInt()
        val free = dao.insertDrinkLog(testLog(cost = 0.0)).toInt()

        repository.setFavoriteForGroup(dao.getDrinkById(paid)!!, true)

        assertEquals(true, dao.getDrinkById(paid)?.isFavorite)
        assertEquals(false, dao.getDrinkById(free)?.isFavorite)
    }

    @Test
    fun updateAndDelete_passThrough() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        repository.updateDrinkLog(dao.getDrinkById(id)!!.copy(name = "Guinness"))
        assertEquals("Guinness", repository.getDrinkById(id)?.name)
        repository.deleteDrinkLog(repository.getDrinkById(id)!!)
        assertNull(repository.getDrinkByIdFlow(id).first())
    }

    @Test
    fun dailySummary_nullWhenSignedOut() = runTest {
        auth.signOut()
        val summary = repository.getDailySummary(
            LocalDateTime.MIN, LocalDateTime.MAX, null, null, null, null, null, null, null, null
        )
        assertNull(summary)
    }

    @Test
    fun dailySummary_forCurrentUser() = runTest {
        dao.insertDrinkLog(testLog(cost = 4.5, amount = 500))
        dao.insertDrinkLog(testLog(cost = 3.0, amount = 330))
        val summary = repository.getDailySummary(
            LocalDateTime.of(2026, 10, 10, 0, 0), LocalDateTime.of(2026, 10, 10, 23, 59),
            null, null, null, null, null, null, null, null
        )!!
        assertEquals(7.5, summary.totalCost!!, 0.001)
        assertEquals(830.0, summary.totalAmount!!, 0.001)
    }

    @Test
    fun pagedLogs_queryCurrentUserWithFilters() = runTest {
        repository.getPagedLogs("hei", "BEER", null, null, null, 1f, null, null, null, true).first()
        val query = dao.pagedQueries.single()
        assertEquals("user", query.userId)
        assertEquals("hei", query.query)
        assertEquals("BEER", query.category)
        assertEquals(1f, query.minPrice)
        assertEquals(true, query.isFavorite)
    }
}
