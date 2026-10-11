package com.example.alcoholtracker.ui.viewmodel

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.snapshots.Snapshot
import com.example.alcoholtracker.data.local.dao.FilterBounds
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.data.repository.UserRepository
import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.FakeDrinkLogDao
import com.example.alcoholtracker.testutil.MainDispatcherRule
import com.example.alcoholtracker.testutil.PagedQuery
import com.example.alcoholtracker.testutil.testLog
import com.example.alcoholtracker.testutil.testUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
class HistoryViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val dao = FakeDrinkLogDao()
    private val repo = DrinkLogRepository(dao, UserRepository(FakeAuthService(testUser)))

    private fun createViewModel() = HistoryViewModel(repo)

    private fun TestScope.collectPagedLogs(vm: HistoryViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.pagedLogs.collect {} }
    }

    private fun TestScope.applyFilters(change: () -> Unit): PagedQuery {
        change()
        Snapshot.sendApplyNotifications()
        advanceUntilIdle()
        return dao.pagedQueries.last()
    }

    @Test
    fun filterBounds_loadIntoRanges() = runTest {
        dao.filterBounds = FilterBounds(minPrice = 2f, maxPrice = 10f, minAbv = 4f, maxAbv = 40f)
        val filters = createViewModel().filterState

        assertEquals(2f..10f, filters.absolutePriceBounds)
        assertEquals(2f..10f, filters.priceRange)
        assertEquals(4f..40f, filters.absoluteAbvBounds)
        assertEquals(4f..40f, filters.abvRange)
        assertTrue(filters.areBoundsLoaded)
    }

    @Test
    fun filterBounds_singleValueGetsUsableRange() = runTest {
        dao.filterBounds = FilterBounds(minPrice = 5f, maxPrice = 5f, minAbv = 5f, maxAbv = 5f)
        val filters = createViewModel().filterState
        assertEquals(5f..6f, filters.absolutePriceBounds)
        assertEquals(5f..6f, filters.absoluteAbvBounds)
    }

    @Test
    fun filterBounds_defaultWhenNoDrinks() = runTest {
        val filters = createViewModel().filterState
        assertEquals(0f..100f, filters.absolutePriceBounds)
        assertEquals(0f..100f, filters.absoluteAbvBounds)
    }

    @Test
    fun defaultFilters_queryEverything() = runTest {
        val vm = createViewModel()
        collectPagedLogs(vm)
        advanceUntilIdle()

        assertEquals(
            PagedQuery("user", null, null, null, null, null, null, null, null, null, null),
            dao.pagedQueries.last()
        )
    }

    @Test
    fun filters_mapToQuery() = runTest {
        val vm = createViewModel()
        collectPagedLogs(vm)
        advanceUntilIdle()

        val query = applyFilters {
            with(vm.filterState) {
                queryState.setTextAndPlaceCursorAtEnd("hei")
                categoryState.setTextAndPlaceCursorAtEnd("BEER")
                recipientState.setTextAndPlaceCursorAtEnd("Anna")
                priceRange = 10f..50f
                abvRange = 0f..20f
                showOnlyFavorites = true
            }
        }

        assertEquals("hei", query.query)
        assertEquals("BEER", query.category)
        assertEquals("Anna", query.recipient)
        assertEquals(10f, query.minPrice)
        assertEquals(50f, query.maxPrice)
        assertEquals(null, query.minAbv)
        assertEquals(20f, query.maxAbv)
        assertEquals(true, query.isFavorite)
    }

    @Test
    fun blankText_andFullRanges_areNotFilters() = runTest {
        val vm = createViewModel()
        collectPagedLogs(vm)
        advanceUntilIdle()

        val query = applyFilters {
            vm.filterState.queryState.setTextAndPlaceCursorAtEnd("   ")
            vm.filterState.priceRange = 0f..100f
        }

        assertEquals(null, query.query)
        assertEquals(null, query.minPrice)
        assertEquals(null, query.maxPrice)
    }

    @Test
    fun dateRange_coversWholeDays() = runTest {
        val vm = createViewModel()
        collectPagedLogs(vm)
        advanceUntilIdle()
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 3)

        val query = applyFilters {
            vm.filterState.dateRangeState.setSelection(
                start.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                end.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }

        assertEquals(start.atStartOfDay(), query.startDate)
        assertEquals(end.atTime(23, 59, 59), query.endDate)
    }

    @Test
    fun typing_isDebounced() = runTest {
        val vm = createViewModel()
        collectPagedLogs(vm)
        advanceUntilIdle()
        val before = dao.pagedQueries.size

        listOf("h", "he", "hei").forEach {
            vm.filterState.queryState.setTextAndPlaceCursorAtEnd(it)
            Snapshot.sendApplyNotifications()
            advanceTimeBy(100)
        }
        advanceUntilIdle()

        assertEquals(before + 1, dao.pagedQueries.size)
        assertEquals("hei", dao.pagedQueries.last().query)
    }

    @Test
    fun navigationEffects() = runTest {
        val vm = createViewModel()
        vm.processEvent(HistoryEvent.OnEditClick(4))
        assertEquals(HistoryEffect.NavigateToDrinkForm(4), vm.historyUiState.value.effect)
        vm.processEvent(HistoryEvent.OnFABClick)
        assertEquals(HistoryEffect.NavigateToDrinkForm(-1), vm.historyUiState.value.effect)
        vm.processEvent(HistoryEvent.OnItemClick(6))
        assertEquals(HistoryEffect.NavigateToDetailedItem(6), vm.historyUiState.value.effect)
        vm.processEvent(HistoryEvent.ConsumeEffect)
        assertEquals(null, vm.historyUiState.value.effect)
    }

    @Test
    fun removeAndUndo() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        val log = dao.getDrinkById(id)!!
        val vm = createViewModel()

        vm.processEvent(HistoryEvent.OnRemoveItem(log))
        assertEquals(HistoryEffect.ShowItemRemoved(log), vm.historyUiState.value.effect)
        assertEquals(null, dao.getDrinkById(id))

        vm.processEvent(HistoryEvent.OnUndoRemoveItem(log))
        assertEquals(log, dao.getDrinkById(id))
        assertEquals(false, vm.historyUiState.value.isLoading)
    }

    @Test
    fun writeFailures_showErrors() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        val log = dao.getDrinkById(id)!!
        dao.failWrites = true
        val vm = createViewModel()

        vm.processEvent(HistoryEvent.OnRemoveItem(log))
        assertEquals(HistoryEffect.ShowError("Error deleting drink"), vm.historyUiState.value.effect)
        vm.processEvent(HistoryEvent.OnUndoRemoveItem(log))
        assertEquals(HistoryEffect.ShowError("Error restoring drink"), vm.historyUiState.value.effect)
    }
}
