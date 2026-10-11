package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.SnackbarType
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.data.repository.UserRepository
import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.FakeDrinkLogDao
import com.example.alcoholtracker.testutil.MainDispatcherRule
import com.example.alcoholtracker.testutil.testLog
import com.example.alcoholtracker.testutil.testUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("SavedStateHandle.toRoute() decodes through android.os.Bundle, which is a stub in JVM unit tests (needs Robolectric or a route seam)")
@OptIn(ExperimentalCoroutinesApi::class)
class DetailedLogViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val dao = FakeDrinkLogDao()
    private val repo = DrinkLogRepository(dao, UserRepository(FakeAuthService(testUser)))

    private fun TestScope.createViewModel(logId: Int, appScope: CoroutineScope = this): DetailedLogViewModel {
        val vm = DetailedLogViewModel(repo, appScope, SavedStateHandle(mapOf("logId" to logId)))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.detailedLogUiState.collect {} }
        return vm
    }

    private val DetailedLogViewModel.state get() = detailedLogUiState.value

    @Test
    fun loadsTheRoutedLog() = runTest {
        val id = dao.insertDrinkLog(testLog(name = "Guinness")).toInt()
        val vm = createViewModel(id)
        assertEquals("Guinness", vm.state.drinkLog?.name)
        assertEquals(false, vm.state.isLoading)
        assertNull(vm.state.effect)
    }

    @Test
    fun missingLog_reportsErrorOnlyOnce() = runTest {
        val vm = createViewModel(999)
        advanceUntilIdle()
        assertEquals(DetailedLogEffect.ShowError("No drink found"), vm.state.effect)

        vm.processEvent(DetailedLogEvent.ConsumeEffect)
        advanceUntilIdle()

        assertNull(vm.state.effect)
    }

    @Test
    fun favorite_togglesStoredLog() = runTest {
        val id = dao.insertDrinkLog(testLog(isFavorite = false)).toInt()
        val vm = createViewModel(id)

        vm.processEvent(DetailedLogEvent.OnFavoriteClick(vm.state.drinkLog!!))
        advanceUntilIdle()

        assertEquals(true, dao.getDrinkById(id)?.isFavorite)
        assertEquals(true, vm.state.drinkLog?.isFavorite)
    }

    @Test
    fun favoriteFailure_showsError() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        val vm = createViewModel(id)
        dao.failWrites = true

        vm.processEvent(DetailedLogEvent.OnFavoriteClick(vm.state.drinkLog!!))
        advanceUntilIdle()

        assertEquals(DetailedLogEffect.ShowError("Could not update favorite"), vm.state.effect)
    }

    @Test
    fun edit_navigatesWithId() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        val vm = createViewModel(id)
        vm.processEvent(DetailedLogEvent.OnEditClick(id))
        assertEquals(DetailedLogEffect.NavigateToDrinkForm(id), vm.state.effect)
    }

    @Test
    fun remove_deletesAndKeepsShowingLastLog() = runTest {
        val id = dao.insertDrinkLog(testLog(name = "Guinness")).toInt()
        val vm = createViewModel(id)
        val log = vm.state.drinkLog!!

        vm.processEvent(DetailedLogEvent.OnRemoveClick(log))
        advanceUntilIdle()

        assertEquals(DetailedLogEffect.ShowItemRemoved(log), vm.state.effect)
        assertNull(dao.getDrinkById(id))
        assertEquals("Guinness", vm.state.drinkLog?.name)
    }

    @Test
    fun undoRemove_restoresSameId_evenAfterScreenIsClosed() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        val log = dao.getDrinkById(id)!!
        val store = ViewModelStore()
        val factory = viewModelFactory {
            initializer { DetailedLogViewModel(repo, this@runTest, SavedStateHandle(mapOf("logId" to id))) }
        }
        val vm = ViewModelProvider.create(store, factory)[DetailedLogViewModel::class]

        vm.processEvent(DetailedLogEvent.OnRemoveClick(log))
        advanceUntilIdle()
        store.clear()
        vm.processEvent(DetailedLogEvent.OnUndoRemove(log))
        advanceUntilIdle()

        assertEquals(log, dao.getDrinkById(id))
    }

    @Test
    fun deleteFailure_sendsErrorSnackbar() = runTest {
        val id = dao.insertDrinkLog(testLog()).toInt()
        val vm = createViewModel(id)
        val snackbar = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            val event = SnackbarController.events.first()
            assertEquals(SnackBarEvent("Error deleting drink", type = SnackbarType.ERROR), event)
        }
        dao.failWrites = true

        vm.processEvent(DetailedLogEvent.OnRemoveClick(vm.state.drinkLog!!))
        advanceUntilIdle()

        assertTrue(snackbar.isCompleted)
        assertEquals(id, dao.getDrinkById(id)?.logId)
    }
}
