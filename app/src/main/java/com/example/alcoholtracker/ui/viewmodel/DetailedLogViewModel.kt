package com.example.alcoholtracker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.ui.navigation.DetailedLog
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

sealed interface DetailedLogEvent {
    data class OnEditClick(val logId: Int) : DetailedLogEvent
    data class OnRemoveClick(val log: UserDrinkLog) : DetailedLogEvent
    data class OnUndoRemove(val log: UserDrinkLog) : DetailedLogEvent
    data class OnFavoriteClick(val log: UserDrinkLog) : DetailedLogEvent
    data object ConsumeEffect : DetailedLogEvent
}

sealed interface DetailedLogEffect{
    data class ShowError(val message: String) : DetailedLogEffect
    data class ShowItemRemoved(val log: UserDrinkLog) : DetailedLogEffect
    data class NavigateToDrinkForm(val logId: Int): DetailedLogEffect
}


data class DetailedLogUiState(
    val drinkLog: UserDrinkLog? = null,
    val isLoading: Boolean = false,
    val effect: DetailedLogEffect? = null
)

@HiltViewModel
class DetailedLogViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val logId = savedStateHandle.toRoute<DetailedLog>().logId
    private val _localState = MutableStateFlow(DetailedLogUiState(isLoading = true))
    val detailedLogUiState: StateFlow<DetailedLogUiState> = combine(
        _localState,
        drinkLogRepo.getDrinkByIdFlow(logId)
    ){ localState, log ->
        if (log != null) {
            localState.copy(
                drinkLog = log,
                isLoading = false
            )
        } else {

            localState.copy(
                isLoading = false,
                effect = DetailedLogEffect.ShowError("No drink found")
            )
        }

    }.catch {
        _localState.update { it.copy(effect = DetailedLogEffect.ShowError("Error loading drink")) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailedLogUiState(isLoading = true)
    )


    fun processEvent(event: DetailedLogEvent) {
        when (event) {
            is DetailedLogEvent.OnEditClick -> onEditClick(event.logId)
            is DetailedLogEvent.OnRemoveClick -> onRemoveClick(event.log)
            is DetailedLogEvent.OnUndoRemove -> onUndoRemove(event.log)
            is DetailedLogEvent.OnFavoriteClick -> onFavoriteClick(event.log)
            DetailedLogEvent.ConsumeEffect -> consumeEffect()

        }
    }

    private fun onEditClick(logId: Int) {

    }
    private fun onRemoveClick(log: UserDrinkLog) {

    }
    private fun onUndoRemove(log: UserDrinkLog) {

    }
    private fun onFavoriteClick(log: UserDrinkLog) {

        viewModelScope.launch {
            try {
                val updatedLog = log.copy(isFavorite = !log.isFavorite)

                drinkLogRepo.updateDrinkLog(updatedLog)
            } catch (e: Exception) {
                _localState.update { it.copy(effect = DetailedLogEffect.ShowError("Could not update favorite")) }
            }
        }
    }


    private fun consumeEffect() {
        _localState.update { it.copy(effect = null) }
    }


}
