package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.repository.DrinkRepository
import com.example.alcoholtracker.domain.model.DrinkCategory.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface DrinkEvent{
    data class AddDrink(val drink: Drink) : DrinkEvent
    data class RemoveDrink(val drink: Drink) : DrinkEvent
    data class UpdateDrink(val drink: Drink) : DrinkEvent
    data object ConsumeEffect: DrinkEvent
}

sealed interface DrinkEffect {
    data class ShowError(val message: String) : DrinkEffect

    
}
data class DrinkUiState(
    val isLoading: Boolean = false,
    val effect: DrinkEffect? = null,
)

@HiltViewModel
class DrinkViewModel @Inject constructor(
    private val repository: DrinkRepository,
) : ViewModel() {

    private val _drinkUiState = MutableStateFlow(DrinkUiState())
    val drinkUiState = _drinkUiState.asStateFlow()


    fun processEvent(event: DrinkEvent) {
        when (event) {
            is DrinkEvent.AddDrink -> addDrink(event.drink)
            DrinkEvent.ConsumeEffect -> consumeEffect()
            is DrinkEvent.RemoveDrink -> removeDrink(event.drink)
            is DrinkEvent.UpdateDrink -> updateDrink(event.drink)
        }
    }


    private fun addDrink(drink: Drink){

    }
    private fun removeDrink(drink: Drink){

    }
    private fun updateDrink(drink: Drink){

    }
    private fun consumeEffect(){
        _drinkUiState.update { it.copy(effect = null) }
    }
}






