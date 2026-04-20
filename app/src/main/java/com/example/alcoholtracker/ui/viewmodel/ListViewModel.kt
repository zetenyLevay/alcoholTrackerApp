package com.example.alcoholtracker.ui.viewmodel

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

sealed interface ListEvent{
    data object OnFABClick : ListEvent
    data class OnItemClick(val logId: Int) : ListEvent
    data class OnEditClick(val logId: Int) : ListEvent
    data class OnRemoveItem(val log: UserDrinkLog) : ListEvent
    data class UndoRemoveItem(val log: UserDrinkLog) : ListEvent
    data object ConsumeEffect : ListEvent
}

sealed interface ListEffect{
    data class ShowError(val message: String) : ListEffect
    data object ShowItemRemoved : ListEffect
    data class NavigateToDrinkForm(val logId: Int): ListEffect
    data class NavigateToDetailedItem(val logId: Int): ListEffect
}

data class ListUiState(
    val drinkLogs: Map<LocalDate,List<UserDrinkLog>> = emptyMap(),
    val isLoading: Boolean = false,
    val effect: ListEffect? = null
)

@HiltViewModel
class ListViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(ListUiState())
    val listUiState = combine(
        _localState,
        drinkLogRepo.getAllLogs()
    ) { state, logs ->
        state.copy(
            drinkLogs = logs.groupBy { it.date.toLocalDate() },
        )
    }.catch {
        _localState.update {
            it.copy(
                effect = ListEffect.ShowError("Error loading data"),
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListUiState(isLoading = true)
    )

    fun processEvent(event: ListEvent) {
        when (event) {
            ListEvent.ConsumeEffect -> consumeEffect()
            is ListEvent.OnEditClick -> onEditClick(event.logId)
            ListEvent.OnFABClick -> onFabClick()
            is ListEvent.OnItemClick -> onItemClick(event.logId)
            is ListEvent.OnRemoveItem -> onRemoveItem(event.log)
            is ListEvent.UndoRemoveItem -> undoRemoveItem(event.log)
        }
    }

    private fun onEditClick(logId: Int){
        _localState.update {
            it.copy(effect = ListEffect.NavigateToDrinkForm(logId))
        }
    }
    private fun onFabClick(){
        _localState.update {
            it.copy(effect = ListEffect.NavigateToDrinkForm(-1))
        }
    }
    private fun onItemClick(logId: Int){
        _localState.update {
            it.copy(effect = ListEffect.NavigateToDetailedItem(logId))
        }
    }
    private fun onRemoveItem(log: UserDrinkLog) {
        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.deleteDrinkLog(log)
                _localState.update {
                    it.copy(
                        effect = ListEffect.ShowItemRemoved,
                        isLoading = false
                    )
                }
            }
            catch (e: Exception){
                _localState.update {
                    it.copy(
                        effect = ListEffect.ShowError("Error deleting drink"),
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
                        effect = ListEffect.ShowError("Error restoring drink"),
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