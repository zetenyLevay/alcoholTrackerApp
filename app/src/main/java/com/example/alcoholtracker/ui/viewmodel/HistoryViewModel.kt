package com.example.alcoholtracker.ui.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface HistoryEvent{
    data object OnFABClick : HistoryEvent
    data class OnItemClick(val logId: Int) : HistoryEvent
    data class OnEditClick(val logId: Int) : HistoryEvent
    data class OnRemoveItem(val log: UserDrinkLog) : HistoryEvent
    data class OnUndoRemoveItem(val log: UserDrinkLog) : HistoryEvent
    data object ConsumeEffect : HistoryEvent
}

sealed interface HistoryEffect{
    data class ShowError(val message: String) : HistoryEffect
    data class ShowItemRemoved(val log: UserDrinkLog) : HistoryEffect
    data class NavigateToDrinkForm(val logId: Int): HistoryEffect
    data class NavigateToDetailedItem(val logId: Int): HistoryEffect
}

data class HistoryUiState(
    val drinkLogs: Map<LocalDate,List<UserDrinkLog>> = emptyMap(),
    val query: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val effect: HistoryEffect? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(HistoryUiState())
    val historyUiState = combine(
        _localState,
        drinkLogRepo.getAllLogs(),
                snapshotFlow { _localState.value.query.text }
    ) { state, logs, query ->

        val filteredLogs = if (query.isBlank()){
            logs
        } else {
            logs.filter { it.name.contains(query,ignoreCase = true) || it.category.name.contains(query, ignoreCase = true)}
        }

        state.copy(
            drinkLogs = filteredLogs.groupBy { it.date.toLocalDate() }.toSortedMap(compareByDescending { it }),
            isLoading = false
        )
    }.catch {
        _localState.update {
            it.copy(
                effect = HistoryEffect.ShowError("Error loading data"),
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun processEvent(event: HistoryEvent) {
        when (event) {
            HistoryEvent.ConsumeEffect -> consumeEffect()
            is HistoryEvent.OnEditClick -> onEditClick(event.logId)
            HistoryEvent.OnFABClick -> onFabClick()
            is HistoryEvent.OnItemClick -> onItemClick(event.logId)
            is HistoryEvent.OnRemoveItem -> onRemoveItem(event.log)
            is HistoryEvent.OnUndoRemoveItem -> undoRemoveItem(event.log)
        }
    }

    private fun onEditClick(logId: Int){
        _localState.update {
            it.copy(effect = HistoryEffect.NavigateToDrinkForm(logId))
        }
    }
    private fun onFabClick(){
        _localState.update {
            it.copy(effect = HistoryEffect.NavigateToDrinkForm(-1))
        }
    }
    private fun onItemClick(logId: Int){
        _localState.update {
            it.copy(effect = HistoryEffect.NavigateToDetailedItem(logId))
        }
    }
    private fun onRemoveItem(log: UserDrinkLog) {
        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.deleteDrinkLog(log)
                _localState.update {
                    it.copy(
                        effect = HistoryEffect.ShowItemRemoved(log),
                        isLoading = false
                    )
                }
            }
            catch (e: Exception){
                _localState.update {
                    it.copy(
                        effect = HistoryEffect.ShowError("Error deleting drink"),
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun undoRemoveItem(log: UserDrinkLog) {
        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.insertDrinkLog(log)
                _localState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }catch (
                e: Exception
            ) {
                _localState.update {
                    it.copy(
                        effect = HistoryEffect.ShowError("Error restoring drink"),
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