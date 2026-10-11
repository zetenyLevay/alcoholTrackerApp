package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.data.repository.DrinkRepository
import com.example.alcoholtracker.data.repository.UserRepository
import com.example.alcoholtracker.domain.logic.handlers.testHandlerRegistry
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.FakeDrinkDao
import com.example.alcoholtracker.testutil.FakeDrinkLogDao
import com.example.alcoholtracker.testutil.MainDispatcherRule
import com.example.alcoholtracker.testutil.testLog
import com.example.alcoholtracker.testutil.testUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthService(testUser)
    private val logDao = FakeDrinkLogDao()
    private val drinkDao = FakeDrinkDao()
    private val logRepo = DrinkLogRepository(logDao, UserRepository(auth))
    private val drinkRepo = DrinkRepository(drinkDao, testHandlerRegistry())

    private fun TestScope.createViewModel(appScope: CoroutineScope = this): SearchViewModel {
        val vm = SearchViewModel(logRepo, drinkRepo, appScope)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.searchUiState.collect {} }
        return vm
    }

    private val SearchViewModel.state get() = searchUiState.value

    @Test
    fun loadsListsForCurrentUser() = runTest {
        logDao.insertDrinkLog(testLog(name = "Heineken", isFavorite = true))
        val vm = createViewModel()

        assertFalse(vm.state.isLoading)
        assertEquals(listOf("Heineken"), vm.state.recentLogs.map { it.name })
        assertEquals(listOf("Heineken"), vm.state.frequentLogs.map { it.name })
        assertEquals(listOf("Heineken"), vm.state.favoriteLogs.map { it.name })
    }

    @Test
    fun query_waitsForDebounceBeforeSearching() = runTest {
        val vm = createViewModel()

        vm.processEvent(SearchEvent.OnQueryChange("red"))
        assertEquals("red", vm.state.query)
        assertTrue(vm.state.isSearching)

        advanceTimeBy(299)
        assertTrue(vm.state.searchResults.isEmpty())

        advanceUntilIdle()
        assertFalse(vm.state.isSearching)
        assertEquals(listOf("Red Wine"), vm.state.searchResults.map { it.name })
    }

    @Test
    fun query_onlyLatestSearchRuns() = runTest {
        val vm = createViewModel()

        vm.processEvent(SearchEvent.OnQueryChange("red"))
        advanceTimeBy(100)
        vm.processEvent(SearchEvent.OnQueryChange("vod"))
        advanceUntilIdle()

        assertEquals(listOf("Vodka"), vm.state.searchResults.map { it.name })
    }

    @Test
    fun query_combinesSavedDrinksAndRemovesDuplicates() = runTest {
        drinkDao.drinks += Drink(name = "red wine", alcoholContent = 12.0, category = "wine")
        drinkDao.drinks += Drink(name = "Redbreast", alcoholContent = 40.0, category = "Spirit")
        drinkDao.drinks += Drink(name = "Guinness", alcoholContent = 4.2, category = "Beer")
        val vm = createViewModel()

        vm.processEvent(SearchEvent.OnQueryChange("RED"))
        advanceUntilIdle()

        val names = vm.state.searchResults.map { it.name.lowercase() }
        assertEquals(listOf("red wine", "redbreast"), names.sorted())
    }

    @Test
    fun blankQuery_clearsResultsAndCancelsSearch() = runTest {
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnQueryChange("red"))
        advanceTimeBy(100)

        vm.processEvent(SearchEvent.OnQueryChange(" "))
        advanceUntilIdle()

        assertTrue(vm.state.searchResults.isEmpty())
        assertFalse(vm.state.isSearching)
    }

    @Test
    fun searchFailure_showsError() = runTest {
        drinkDao.failReads = true
        val vm = createViewModel()

        vm.processEvent(SearchEvent.OnQueryChange("red"))
        advanceUntilIdle()

        assertEquals(SearchEffect.ShowError("Error searching drinks"), vm.state.effect)
        assertFalse(vm.state.isSearching)
    }

    @Test
    fun logClick_navigatesToFormWithLogId() = runTest {
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnLogClick(testLog(logId = 7)))
        assertEquals(SearchEffect.NavigateToDrinkForm(7), vm.state.effect)
    }

    @Test
    fun drinkClick_navigatesWithMappedCategory() = runTest {
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnDrinkClick(Drink(name = "Red Wine", alcoholContent = 13.0, category = "Wine")))
        assertEquals(SearchEffect.NavigateToDrinkFormWithDrink("Red Wine", DrinkCategory.WINE), vm.state.effect)

        vm.processEvent(SearchEvent.OnDrinkClick(Drink(name = "Mystery", alcoholContent = 0.0, category = "")))
        assertEquals(SearchEffect.NavigateToDrinkFormWithDrink("Mystery", DrinkCategory.OTHER), vm.state.effect)
    }

    @Test
    fun logAgain_savesCopyAndReportsIt() = runTest {
        val original = testLog(name = "Heineken")
        val originalId = logDao.insertDrinkLog(original).toInt()
        val vm = createViewModel()

        vm.processEvent(SearchEvent.OnLogAgainClick(logDao.getDrinkById(originalId)!!))
        advanceUntilIdle()

        val effect = vm.state.effect as SearchEffect.ShowDrinkLogged
        assertNotEquals(originalId, effect.log.logId)
        assertNotEquals(original.date, effect.log.date)
        assertEquals("Heineken", effect.log.name)
        assertEquals(2, logDao.logs.value.size)
        assertEquals(effect.log.logId, logDao.getDrinkById(effect.log.logId)?.logId)
    }

    @Test
    fun undo_removesOnlyTheNewLog() = runTest {
        val originalId = logDao.insertDrinkLog(testLog()).toInt()
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnLogAgainClick(logDao.getDrinkById(originalId)!!))
        advanceUntilIdle()
        val logged = (vm.state.effect as SearchEffect.ShowDrinkLogged).log

        vm.processEvent(SearchEvent.OnUndoLogAgain(logged))
        advanceUntilIdle()

        assertEquals(listOf(originalId), logDao.logs.value.map { it.logId })
    }

    @Test
    fun undo_stillWorksAfterScreenIsClosed() = runTest {
        val loggedId = logDao.insertDrinkLog(testLog()).toInt()
        val store = ViewModelStore()
        val factory = viewModelFactory { initializer { SearchViewModel(logRepo, drinkRepo, this@runTest) } }
        val vm = ViewModelProvider.create(store, factory)[SearchViewModel::class]

        store.clear()
        vm.processEvent(SearchEvent.OnUndoLogAgain(logDao.getDrinkById(loggedId)!!))
        advanceUntilIdle()

        assertTrue(logDao.logs.value.isEmpty())
    }

    @Test
    fun logAgainFailure_showsError() = runTest {
        logDao.failWrites = true
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnLogAgainClick(testLog()))
        advanceUntilIdle()
        assertEquals(SearchEffect.ShowError("Error logging drink"), vm.state.effect)
    }

    @Test
    fun favoriteToggle_updatesFavorites() = runTest {
        val id = logDao.insertDrinkLog(testLog(isFavorite = false)).toInt()
        val vm = createViewModel()

        vm.processEvent(SearchEvent.OnFavoriteToggle(logDao.getDrinkById(id)!!))
        advanceUntilIdle()

        assertEquals(listOf(id), vm.state.favoriteLogs.map { it.logId })

        vm.processEvent(SearchEvent.OnFavoriteToggle(logDao.getDrinkById(id)!!))
        advanceUntilIdle()
        assertTrue(vm.state.favoriteLogs.isEmpty())
    }

    @Test
    fun favoriteFailure_showsError() = runTest {
        val id = logDao.insertDrinkLog(testLog()).toInt()
        logDao.failWrites = true
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnFavoriteToggle(logDao.getDrinkById(id)!!))
        advanceUntilIdle()
        assertEquals(SearchEffect.ShowError("Error updating favorite"), vm.state.effect)
    }

    @Test
    fun consumeEffect_clearsIt() = runTest {
        val vm = createViewModel()
        vm.processEvent(SearchEvent.OnLogClick(testLog(logId = 1)))
        vm.processEvent(SearchEvent.ConsumeEffect)
        assertNull(vm.state.effect)
    }
}
