package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.preferences.ProgressBarPreferences
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeEvent{
    data object OnFABClick : HomeEvent
    data class OnItemClick(val logId: Int) : HomeEvent
    data class OnProgressBarUpdate(val type: ProgressBarType, val target: Double) : HomeEvent
    data class OnItemRemove(val log: UserDrinkLog) : HomeEvent
    data class UndoItemRemove(val log: UserDrinkLog) : HomeEvent
    data object ConsumeEffect : HomeEvent
}

sealed interface HomeEffect{
    data class ShowError(val message: String) : HomeEffect
    data object ShowItemRemoved : HomeEffect
    data object NavigateToDrinkForm: HomeEffect
    data class NavigateToDetailedItem(val logId: Int): HomeEffect
}

data class HomeUiState(
    val drinkCount: Int = 0,
    val progressBarType: ProgressBarType = ProgressBarType.MONEY,
    val currentMoneySpent: Double = 0.0,
    val currentAmountMl: Int = 0,
    val targets: Map<ProgressBarType, Double> = emptyMap(),
    val tonightDrinkLogs: List<UserDrinkLog> = emptyList(),
    val isLoading: Boolean = false,
    val effect: HomeEffect? = null,
)

private data class ProgressTargets(
    val type: ProgressBarType,
    val money: Double,
    val amount: Double,
    val count: Double
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository,
    private val preferences: ProgressBarPreferences
) : ViewModel() {

    private val prefFlow = combine(
        preferences.activeProgressBarType,
        preferences.moneyTarget,
        preferences.amountTarget,
        preferences.countTarget
    ) { type, money, amount, count ->
        ProgressTargets(type, money, amount, count)
    }
    private val _localState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = combine(
        _localState,
        prefFlow,
        drinkLogRepo.getTonightLogs()
    ) { state, prefs, logs ->
        var currentMoneySpent = 0.0
        var currentAmountMl = 0


        for (log in logs) {
            currentMoneySpent += log.cost ?: 0.0
            currentAmountMl += log.amount
        }

        val targetMap = mapOf(
            ProgressBarType.MONEY to prefs.money,
            ProgressBarType.AMOUNT to prefs.amount,
            ProgressBarType.COUNT to prefs.count
        )

        state.copy(
            drinkCount = logs.size,
            currentMoneySpent = currentMoneySpent,
            currentAmountMl = currentAmountMl,
            tonightDrinkLogs = logs,
            progressBarType = prefs.type,
            targets = targetMap,

        )

    }.catch{
        _localState.update {
            it.copy(
                effect = HomeEffect.ShowError("Error loading data"),
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )


    fun processEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnFABClick -> onFABClick()
            is HomeEvent.OnItemClick -> onItemClick(event.logId)
            is HomeEvent.OnProgressBarUpdate -> onProgressBarUpdate(event.type, event.target)
            is HomeEvent.OnItemRemove -> onItemRemove(event.log)
            is HomeEvent.UndoItemRemove -> undoItemRemove(event.log)
            HomeEvent.ConsumeEffect -> consumeEffect()
        }
    }

    private fun undoItemRemove(log: UserDrinkLog) {
        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.insertDrinkLog(log)
            }catch (
                e: Exception
            ) {
                _localState.update {
                    it.copy(
                        effect = HomeEffect.ShowError("Error restoring drink"),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun onFABClick() {
        _localState.update {
            it.copy(effect = HomeEffect.NavigateToDrinkForm)
        }
    }
    private fun onItemClick(logId: Int) {
        _localState.update {
            it.copy(effect = HomeEffect.NavigateToDetailedItem(logId))
        }
    }
    private fun onProgressBarUpdate(
        type: ProgressBarType,
        target: Double
    ) {
        viewModelScope.launch {
            preferences.updateType(type)
            when (type) {
                ProgressBarType.MONEY -> preferences.updateMoneyTarget(target)
                ProgressBarType.COUNT -> preferences.updateCountTarget(target)
                ProgressBarType.AMOUNT -> preferences.updateAmountTarget(target)
            }
        }
    }
    private fun onItemRemove(log: UserDrinkLog) {
        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.deleteDrinkLog(log)
                _localState.update {
                    it.copy(
                        effect = HomeEffect.ShowItemRemoved,
                    )
                }
            }
            catch (e: Exception){
                _localState.update {
                    it.copy(
                        effect = HomeEffect.ShowError("Error deleting drink"),
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun consumeEffect() {
        _localState.update { it.copy(effect = null) }
    }



}