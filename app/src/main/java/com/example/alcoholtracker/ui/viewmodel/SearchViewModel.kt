package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import dagger.hilt.android.lifecycle.HiltViewModel
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

): ViewModel() {

}