package com.example.alcoholtracker.ui.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderState
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.DrinkLog
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
import java.util.Locale.getDefault
import javax.inject.Inject

sealed interface HistoryEvent{
    data object OnFABClick : HistoryEvent
    data class OnItemClick(val logId: Int) : HistoryEvent
    data class OnEditClick(val logId: Int) : HistoryEvent
    data class OnRemoveItem(val log: DrinkLog) : HistoryEvent
    data class OnUndoRemoveItem(val log: DrinkLog) : HistoryEvent
    data object ConsumeEffect : HistoryEvent
}

sealed interface HistoryEffect{
    data class ShowError(val message: String) : HistoryEffect
    data class ShowItemRemoved(val log: DrinkLog) : HistoryEffect
    data class NavigateToDrinkForm(val logId: Int): HistoryEffect
    data class NavigateToDetailedItem(val logId: Int): HistoryEffect
}

data class HistoryUiState(
    val drinkLogs: Map<LocalDate,List<DrinkLog>> = emptyMap(),
    val query: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val effect: HistoryEffect? = null
)

data class FilterState(
    val dateRange: DateRangePickerState = DateRangePickerState(locale = getDefault()),
    val category: String? = null,
    val recipient: String? = null,
    val isFavorite: Boolean? = null,
    val priceFrom: Double? = null,
    val priceTo: Double? = null,
    val abvFrom: Double? = null,
    val abvTo: Double? = null

)

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(HistoryUiState())
    private val _filterState = MutableStateFlow(FilterState())
    val queryState: TextFieldState = TextFieldState()
    val dateRangeState: DateRangePickerState = DateRangePickerState(locale = getDefault())
    val priceSliderState: SliderState = SliderState()
    val abvSliderState: SliderState = SliderState()


    val historyUiState = combine(
        _localState,
        _filterState,
        drinkLogRepo.getAllLogs(),
        snapshotFlow { _localState.value.query.text },
        snapshotFlow { _filterState.value.dateRange }
    ) { state, filterState,logs, query, date ->

        val predicates = mutableListOf<(DrinkLog) -> Boolean>()

        if (query.isNotBlank()) {
            predicates.add {
                it.name.contains(query, ignoreCase = true)
            }
        }


        filterState.dateRange.getSelectedStartDate()?.let { from -> predicates.add { it.date.toLocalDate() >= from } }
        filterState.dateRange.getSelectedEndDate()?.let { to -> predicates.add { it.date.toLocalDate() <= to } }
        filterState.priceFrom?.let { from -> predicates.add { it.cost!! >= from } }
        filterState.priceTo?.let { to -> predicates.add { it.cost!! <= to } }
        filterState.category?.let { cat -> predicates.add { it.category.name.equals(cat, ignoreCase = true) } }
        filterState.recipient?.let { rec -> predicates.add { it.recipient.equals(rec, ignoreCase = true) } }
        filterState.isFavorite?.let { fav -> predicates.add { it.isFavorite == fav }}

        val filteredLogs = logs.filter { log ->
            predicates.all { predicate -> predicate(log) }
        }

        HistoryUiState(
            drinkLogs = filteredLogs.groupBy { it.date.toLocalDate() }.toSortedMap(compareByDescending { it }),
            isLoading = false
        )
    }.catch {
        HistoryUiState(
            effect = HistoryEffect.ShowError("Error loading data"),
            isLoading = false
        )
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
    private fun onRemoveItem(log: DrinkLog) {
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
    private fun undoRemoveItem(log: DrinkLog) {
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