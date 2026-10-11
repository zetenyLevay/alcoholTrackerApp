package com.example.alcoholtracker.ui.viewmodel

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.alcoholtracker.data.preferences.ProgressBarPreferences
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.data.repository.UserRepository
import com.example.alcoholtracker.data.repository.sessionWindowFor
import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.FakeDrinkLogDao
import com.example.alcoholtracker.testutil.MainDispatcherRule
import com.example.alcoholtracker.testutil.testLog
import com.example.alcoholtracker.testutil.testUser
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @get:Rule
    val folder = TemporaryFolder()

    private val dao = FakeDrinkLogDao()
    private val repo = DrinkLogRepository(dao, UserRepository(FakeAuthService(testUser)))
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val preferences by lazy {
        ProgressBarPreferences(PreferenceDataStoreFactory.create(scope = ioScope) {
            folder.newFile("prefs.preferences_pb").also { it.delete() }
        })
    }

    private val tonight = sessionWindowFor(LocalDateTime.now()).first.plusHours(1)

    @After
    fun tearDown() {
        ioScope.cancel()
    }

    private fun TestScope.createViewModel(): HomeViewModel {
        val vm = HomeViewModel(repo, preferences)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.homeUiState.collect {} }
        return vm
    }

    // Preferences come from a real DataStore file, so wait in real time rather than runTest's virtual time
    private suspend fun HomeViewModel.awaitState(predicate: (HomeUiState) -> Boolean): HomeUiState =
        withContext(Dispatchers.Default) { withTimeout(5_000) { homeUiState.first(predicate) } }

    @Test
    fun totalsOnlyCountTonight() = runTest {
        dao.insertDrinkLog(testLog(name = "A", cost = 4.5, amount = 500, date = tonight))
        dao.insertDrinkLog(testLog(name = "B", cost = null, amount = 330, date = tonight.plusMinutes(30)))
        dao.insertDrinkLog(testLog(name = "Yesterday", cost = 100.0, amount = 1000, date = tonight.minusDays(1)))
        val vm = createViewModel()

        val state = vm.awaitState { it.targets.isNotEmpty() }

        assertEquals(2, state.drinkCount)
        assertEquals(4.5, state.currentMoneySpent, 0.001)
        assertEquals(830, state.currentAmountMl)
        assertEquals(setOf("A", "B"), state.tonightDrinkLogs.map { it.name }.toSet())
    }

    @Test
    fun goalsComeFromPreferences() = runTest {
        val vm = createViewModel()
        val state = vm.awaitState { it.targets.isNotEmpty() }
        assertEquals(ProgressBarType.MONEY, state.progressBarType)
        assertEquals(
            mapOf(ProgressBarType.MONEY to 50.0, ProgressBarType.AMOUNT to 1500.0, ProgressBarType.COUNT to 5.0),
            state.targets
        )
    }

    @Test
    fun progressBarUpdate_savesTypeAndTarget() = runTest {
        val vm = createViewModel()
        vm.processEvent(HomeEvent.OnProgressBarUpdate(ProgressBarType.COUNT, 3.0))

        val state = vm.awaitState { it.progressBarType == ProgressBarType.COUNT && it.targets[ProgressBarType.COUNT] == 3.0 }

        assertEquals(50.0, state.targets[ProgressBarType.MONEY]!!, 0.0)
        assertEquals(3.0, preferences.countTarget.first(), 0.0)
    }

    @Test
    fun navigationEffects() = runTest {
        val vm = createViewModel()
        vm.processEvent(HomeEvent.OnFABClick)
        assertEquals(HomeEffect.NavigateToDrinkForm, vm.awaitState { it.effect != null }.effect)
        vm.processEvent(HomeEvent.OnItemClick(9))
        assertEquals(HomeEffect.NavigateToDetailedItem(9), vm.awaitState { it.effect is HomeEffect.NavigateToDetailedItem }.effect)
        vm.processEvent(HomeEvent.ConsumeEffect)
        assertNull(vm.awaitState { it.effect == null }.effect)
    }

    @Test
    fun removeAndUndo() = runTest {
        val id = dao.insertDrinkLog(testLog(date = tonight)).toInt()
        val log = dao.getDrinkById(id)!!
        val vm = createViewModel()

        vm.processEvent(HomeEvent.OnItemRemove(log))
        assertEquals(HomeEffect.ShowItemRemoved(log), vm.awaitState { it.effect != null }.effect)
        assertEquals(0, vm.awaitState { it.drinkCount == 0 }.drinkCount)

        vm.processEvent(HomeEvent.OnUndoItemRemove(log))
        assertEquals(log, dao.getDrinkById(id))
    }

    @Test
    fun removeFailure_showsError() = runTest {
        val id = dao.insertDrinkLog(testLog(date = tonight)).toInt()
        dao.failWrites = true
        val vm = createViewModel()
        vm.processEvent(HomeEvent.OnItemRemove(dao.getDrinkById(id)!!))
        assertEquals(HomeEffect.ShowError("Error deleting drink"), vm.awaitState { it.effect != null }.effect)
    }
}
