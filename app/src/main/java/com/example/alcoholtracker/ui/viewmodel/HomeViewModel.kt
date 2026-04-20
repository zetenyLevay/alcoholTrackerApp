package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.preferences.ProgressBarPreferences
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.launchIn
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
    val currentDrinkCount: Int = 0,
    val currentMoneySpent: Double = 0.0,
    val currentAmountMl: Int = 0,
    val targets: Map<ProgressBarType, Double> = emptyMap(),
    val tonightDrinkLogs: List<UserDrinkLog> = emptyList(),
    val isLoading: Boolean = false,
    val effect: HomeEffect? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository,
    private val preferences: ProgressBarPreferences
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        getInitialData()
    }

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

    fun getInitialData(){

            _homeUiState.update { it.copy(isLoading = true) }

                combine(
                    drinkLogRepo.getTonightLogs(),
                    preferences.activeProgressBarType,
                    preferences.moneyTarget,
                    preferences.amountTarget,
                    preferences.countTarget
                ) { logs, progressBarType, moneyTarget, amountTarget, countTarget ->
                    var currentMoneySpent = 0.0
                    var currentAmountMl = 0
                    var currentDrinkCount = 0

                    for (log in logs) {
                        currentMoneySpent += log.cost ?: 0.0
                        currentAmountMl += log.amount
                        currentDrinkCount++
                    }

                    val targetMap = mapOf(
                        ProgressBarType.MONEY to moneyTarget,
                        ProgressBarType.AMOUNT to amountTarget,
                        ProgressBarType.COUNT to countTarget
                    )

                    _homeUiState.update {
                        it.copy(
                            drinkCount = currentDrinkCount,
                            currentMoneySpent = currentMoneySpent,
                            currentAmountMl = currentAmountMl,
                            tonightDrinkLogs = logs,
                            progressBarType = progressBarType,
                            targets = targetMap,
                            isLoading = false
                        )
                    }

                }.catch {
                    _homeUiState.update {
                        it.copy(
                            effect = HomeEffect.ShowError("Error loading data"),
                            isLoading = false
                        )
                    }

                }.launchIn(viewModelScope)
    }

    private fun undoItemRemove(log: UserDrinkLog) {
        viewModelScope.launch {
            _homeUiState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.insertDrinkLog(log)
                _homeUiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }catch (
                e: Exception
            ) {
                _homeUiState.update {
                    it.copy(
                        effect = HomeEffect.ShowError("Error restoring drink"),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun onFABClick() {
        _homeUiState.update {
            it.copy(effect = HomeEffect.NavigateToDrinkForm)
        }
    }
    private fun onItemClick(logId: Int) {
        _homeUiState.update {
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
            _homeUiState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.deleteDrinkLog(log)
                _homeUiState.update {
                    it.copy(
                        effect = HomeEffect.ShowItemRemoved,
                        isLoading = false
                    )
                }
            }
            catch (e: Exception){
                _homeUiState.update {
                    it.copy(
                        effect = HomeEffect.ShowError("Error deleting drink"),
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun consumeEffect() {
        _homeUiState.update { it.copy(effect = null) }
    }



}