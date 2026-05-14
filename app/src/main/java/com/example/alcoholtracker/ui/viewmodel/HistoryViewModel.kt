@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)

package com.example.alcoholtracker.ui.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderState
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.alcoholtracker.data.local.dao.DailySummary
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import java.util.Locale.getDefault
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
    val isLoading: Boolean = false,
    val effect: HistoryEffect? = null
)
sealed class HistoryUiModel {
    data class Header(
        val date: LocalDate,
        val summary: DailySummary
    ) : HistoryUiModel()
    data class DrinkItem(val log: DrinkLog) : HistoryUiModel()
}

class HistoryFilterStates {
    val queryState = TextFieldState()
    val categoryState = TextFieldState()
    val recipientState = TextFieldState()
    val dateRangeState = DateRangePickerState(locale = Locale.getDefault())
    val priceSliderState = RangeSliderState()
    val abvSliderState = RangeSliderState()
    var showOnlyFavorites by mutableStateOf(false)
    var absolutePriceBounds by mutableStateOf(0f..100f)
    var absoluteAbvBounds by mutableStateOf(0f..100f)
    var priceRange by mutableStateOf(0f..100f)
    var abvRange by mutableStateOf(0f..100f)
    var areBoundsLoaded by mutableStateOf(false)
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository
) : ViewModel() {

    private val _historyUiState = MutableStateFlow(HistoryUiState())
    val historyUiState = _historyUiState.asStateFlow()
    val filterState = HistoryFilterStates()

    init {

        viewModelScope.launch {
                drinkLogRepo.getFilterBounds().collectLatest { dbBounds ->

                    val minP = dbBounds?.minPrice ?: 0f
                    val maxP = dbBounds?.maxPrice ?: 100f
                    val minA = dbBounds?.minAbv ?: 0f
                    val maxA = dbBounds?.maxAbv ?: 100f

                    val safeMaxPrice = if (minP >= maxP) minP + 1f else maxP
                    val safeMaxAbv = if (minA >= maxA) minA + 1f else maxA

                    val newPriceBounds = minP..safeMaxPrice
                    val newAbvBounds = minA..safeMaxAbv

                    filterState.absolutePriceBounds = newPriceBounds
                    filterState.absoluteAbvBounds = newAbvBounds

                    if (!filterState.areBoundsLoaded) {
                        filterState.priceRange = newPriceBounds
                        filterState.abvRange = newAbvBounds
                        filterState.areBoundsLoaded = true
                    }
                }
            }
        }


    val pagedLogs: Flow<PagingData<HistoryUiModel>> = combine(
        listOf(
            snapshotFlow { filterState.queryState.text.toString() },
            snapshotFlow { filterState.categoryState.text.toString() },
            snapshotFlow { filterState.recipientState.text.toString() },
            snapshotFlow {
                Pair(
                    filterState.dateRangeState.getSelectedStartDate(),
                    filterState.dateRangeState.getSelectedEndDate()
                )
            },
            snapshotFlow { filterState.priceRange },
            snapshotFlow { filterState.abvRange },
            snapshotFlow { filterState.showOnlyFavorites }
        )
    ) { values ->


        val currentPriceRange = values[4] as ClosedFloatingPointRange<Float>
        val currentAbvRange = values[5] as ClosedFloatingPointRange<Float>
        val dates = values[3] as Pair<LocalDate?, LocalDate?>
        val showFavoritesOnly = values[6] as Boolean


        val minPrice = if (currentPriceRange.start > filterState.absolutePriceBounds.start) currentPriceRange.start else null
        val maxPrice = if (currentPriceRange.endInclusive < filterState.absolutePriceBounds.endInclusive) currentPriceRange.endInclusive else null
        val minAbv = if (currentAbvRange.start > filterState.absoluteAbvBounds.start) currentAbvRange.start else null
        val maxAbv = if (currentAbvRange.endInclusive < filterState.absoluteAbvBounds.endInclusive) currentAbvRange.endInclusive else null

        FilterPayload(
            query = (values[0] as String).takeIf { it.isNotBlank() },
            category = (values[1] as String).takeIf { it.isNotBlank() },
            recipient = (values[2] as String).takeIf { it.isNotBlank() },
            startDate = dates.first?.atStartOfDay(),
            endDate = dates.second?.atTime(23, 59, 59),
            minPrice = minPrice,
            maxPrice = maxPrice,
            minAbv = minAbv,
            maxAbv = maxAbv,
            isFavorite = if (showFavoritesOnly) true else null
        )
    }
        .debounce(300.milliseconds)
        .flatMapLatest { payload ->
            drinkLogRepo.getPagedLogs(

                query = payload.query,
                category = payload.category,
                recipient = payload.recipient,
                startDate = payload.startDate,
                endDate = payload.endDate,
                minPrice = payload.minPrice,
                maxPrice = payload.maxPrice,
                minAbv = payload.minAbv,
                maxAbv = payload.maxAbv,
                isFavorite = payload.isFavorite
            ).map { pagingData ->
                pagingData.map { HistoryUiModel.DrinkItem(it) }
                    .insertSeparators { before, after ->
                        if (after == null) return@insertSeparators null
                        val beforeDate = before?.log?.date?.toLocalDate()
                        val afterDate = after.log.date.toLocalDate()

                        if (before == null || beforeDate != afterDate) {

                            val startOfDay = afterDate.atStartOfDay()
                            val endOfDay = afterDate.atTime(23, 59, 59)

                            val dailySummary = drinkLogRepo.getDailySummary(
                                startDate = startOfDay,
                                endDate = endOfDay,
                                query = payload.query,
                                category = payload.category,
                                recipient = payload.recipient,
                                minPrice = payload.minPrice,
                                maxPrice = payload.maxPrice,
                                minAbv = payload.minAbv,
                                maxAbv = payload.maxAbv,
                                isFavorite = payload.isFavorite
                            )

                            return@insertSeparators HistoryUiModel.Header(
                                date = afterDate,
                                summary = dailySummary ?: DailySummary(0.0, 0.0)
                            )
                        } else {
                            return@insertSeparators null
                        }
                    }
            }
        }
        .cachedIn(viewModelScope)


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
        _historyUiState.update {
            it.copy(effect = HistoryEffect.NavigateToDrinkForm(logId))
        }
    }
    private fun onFabClick(){
        _historyUiState.update {
            it.copy(effect = HistoryEffect.NavigateToDrinkForm(-1))
        }
    }
    private fun onItemClick(logId: Int){
        _historyUiState.update {
            it.copy(effect = HistoryEffect.NavigateToDetailedItem(logId))
        }
    }
    private fun onRemoveItem(log: DrinkLog) {
        viewModelScope.launch {
            _historyUiState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.deleteDrinkLog(log)
                _historyUiState.update {
                    it.copy(
                        effect = HistoryEffect.ShowItemRemoved(log),
                        isLoading = false
                    )
                }
            }
            catch (e: Exception){
                _historyUiState.update {
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
            _historyUiState.update { it.copy(isLoading = true) }
            try {
                drinkLogRepo.insertDrinkLog(log)
                _historyUiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }catch (
                e: Exception
            ) {
                _historyUiState.update {
                    it.copy(
                        effect = HistoryEffect.ShowError("Error restoring drink"),
                        isLoading = false
                    )
                }
            }
        }
    }
    private fun consumeEffect() {
        _historyUiState.update { it.copy(effect = null) }
    }

private data class FilterPayload(
    val query: String?,
    val category: String?,
    val recipient: String?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val minPrice: Float?,
    val maxPrice: Float?,
    val minAbv: Float?,
    val maxAbv: Float?,
    val isFavorite: Boolean?
)
}