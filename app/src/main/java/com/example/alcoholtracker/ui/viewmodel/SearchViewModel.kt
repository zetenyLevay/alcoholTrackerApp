package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.data.repository.DrinkRepository
import com.example.alcoholtracker.di.ApplicationScope
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.drinkCategoryFromLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

sealed interface SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent
    data class OnLogClick(val log: DrinkLog) : SearchEvent
    data class OnLogAgainClick(val log: DrinkLog) : SearchEvent
    data class OnUndoLogAgain(val log: DrinkLog) : SearchEvent
    data class OnDrinkClick(val drink: Drink) : SearchEvent
    data class OnFavoriteToggle(val log: DrinkLog) : SearchEvent
    data object ConsumeEffect : SearchEvent
}

sealed interface SearchEffect {
    data class ShowError(val message: String) : SearchEffect
    data class ShowDrinkLogged(val log: DrinkLog) : SearchEffect
    data class NavigateToDrinkForm(val copyFromLogId: Int) : SearchEffect
    data class NavigateToDrinkFormWithDrink(val name: String, val category: DrinkCategory) : SearchEffect
}

data class SearchUiState(
    val query: String = "",
    val recentLogs: List<DrinkLog> = emptyList(),
    val frequentLogs: List<DrinkLog> = emptyList(),
    val favoriteLogs: List<DrinkLog> = emptyList(),
    val searchResults: List<Drink> = emptyList(),
    val isLoading: Boolean = true,
    val isSearching: Boolean = false,
    val effect: SearchEffect? = null,
)


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val drinkLogRepo: DrinkLogRepository,
    private val drinkRepo: DrinkRepository,
    @param:ApplicationScope private val appScope: CoroutineScope,
): ViewModel() {

    private var searchJob: Job? = null
    private val _localState = MutableStateFlow(SearchUiState())

    val searchUiState: StateFlow<SearchUiState> = combine(
        _localState,
        drinkLogRepo.getRecentLogs(),
        drinkLogRepo.getFrequentLogs(),
        drinkLogRepo.getFavoriteLogs()
    ) { state, recent, frequent, favorites ->
        state.copy(
            recentLogs = recent,
            frequentLogs = frequent,
            favoriteLogs = favorites,
            isLoading = false
        )
    }.catch {
        _localState.update {
            it.copy(
                effect = SearchEffect.ShowError("Error loading drinks"),
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(isLoading = true)
    )


    fun processEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> onQueryChange(event.query)
            is SearchEvent.OnLogClick -> onLogClick(event.log)
            is SearchEvent.OnLogAgainClick -> onLogAgainClick(event.log)
            is SearchEvent.OnUndoLogAgain -> onUndoLogAgain(event.log)
            is SearchEvent.OnDrinkClick -> onDrinkClick(event.drink)
            is SearchEvent.OnFavoriteToggle -> onFavoriteToggle(event.log)
            SearchEvent.ConsumeEffect -> consumeEffect()
        }
    }

    private fun onQueryChange(query: String) {
        searchJob?.cancel()
        _localState.update { it.copy(query = query) }

        if (query.isBlank()) {
            _localState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        _localState.update { it.copy(isSearching = true) }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            try {
                val localDrinks = drinkRepo.getAllDrinks()
                val apiDrinks = DrinkCategory.entries.map { category ->
                    async {
                        try {
                            drinkRepo.searchApiDrinks(query, category)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }.awaitAll().flatten()

                val results = (localDrinks + apiDrinks)
                    .filter { it.name.contains(query, ignoreCase = true) }
                    .distinctBy { it.name.lowercase() to it.category.lowercase() }

                _localState.update { it.copy(searchResults = results, isSearching = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _localState.update {
                    it.copy(
                        effect = SearchEffect.ShowError("Error searching drinks"),
                        isSearching = false
                    )
                }
            }
        }
    }

    private fun onLogClick(log: DrinkLog) {
        _localState.update {
            it.copy(effect = SearchEffect.NavigateToDrinkForm(log.logId))
        }
    }

    private fun onLogAgainClick(log: DrinkLog) {
        appScope.launch {
            try {
                val newLog = log.copy(logId = 0, date = LocalDateTime.now())
                val newId = drinkLogRepo.insertDrinkLog(newLog)
                _localState.update {
                    it.copy(effect = SearchEffect.ShowDrinkLogged(newLog.copy(logId = newId.toInt())))
                }
            } catch (e: Exception) {
                _localState.update {
                    it.copy(effect = SearchEffect.ShowError("Error logging drink"))
                }
            }
        }
    }

    // The undo snackbar can outlive this screen, so this can't run in viewModelScope
    private fun onUndoLogAgain(log: DrinkLog) {
        appScope.launch {
            try {
                drinkLogRepo.deleteDrinkLog(log)
            } catch (e: Exception) {
                _localState.update {
                    it.copy(effect = SearchEffect.ShowError("Error undoing drink log"))
                }
            }
        }
    }

    private fun onDrinkClick(drink: Drink) {
        val category = drinkCategoryFromLabel(drink.category)

        _localState.update {
            it.copy(effect = SearchEffect.NavigateToDrinkFormWithDrink(drink.name, category))
        }
    }

    private fun onFavoriteToggle(log: DrinkLog) {
        viewModelScope.launch {
            try {
                drinkLogRepo.setFavoriteForGroup(log, !log.isFavorite)
            } catch (e: Exception) {
                _localState.update {
                    it.copy(effect = SearchEffect.ShowError("Error updating favorite"))
                }
            }
        }
    }

    private fun consumeEffect() {
        _localState.update { it.copy(effect = null) }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}

