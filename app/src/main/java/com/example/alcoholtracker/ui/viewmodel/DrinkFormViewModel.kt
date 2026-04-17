package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.repository.UserAndUserDrinkLogRepository
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

sealed interface DrinkFormEvent{
    data class OnDrinkNameChange(val name: String) : DrinkFormEvent
    data class OnCategoryChange(val category: DrinkCategory) : DrinkFormEvent
    data class OnDrinkChange(val drink: Drink) : DrinkFormEvent
    data class OnDrinkUnitChange(val drinkUnit: DrinkUnit) : DrinkFormEvent
    data class OnAmountChange(val amount: Double) : DrinkFormEvent
    data class OnABVChange(val abv: Double) : DrinkFormEvent
    data class OnPriceChange(val price: Double) : DrinkFormEvent
    data class OnRecipientChange(val recipient: String) : DrinkFormEvent
    data class OnDateChange(val date: LocalDate) : DrinkFormEvent
    data class OnTimeChange(val time: LocalTime) : DrinkFormEvent
    data class OnNotesChange(val notes: String) : DrinkFormEvent
    data class OnLocationChange(val location: String) : DrinkFormEvent
    data class GetDrinkOptions(val category: DrinkCategory) : DrinkFormEvent
    data class GetAmountOptions(val category: DrinkCategory) : DrinkFormEvent
    data object GetRecipientOptions: DrinkFormEvent
    data object OnSaveDrinkLog : DrinkFormEvent
    data object ConsumeEffect : DrinkFormEvent
}

sealed interface DrinkFormEffect{
    data class ShowError(val message: String) : DrinkFormEffect
    data object SaveDrink : DrinkFormEffect
}

data class DrinkFormUiState(
    val isEdit: Boolean = false,
    val isLoading: Boolean = false,
    val inputs: DrinkFormInput = DrinkFormInput(),
    val options: DrinkFormOptions = DrinkFormOptions(),
    val effect: DrinkFormEffect? = null,
)

data class DrinkFormInput(
    val logId: Int? = null,
    val drinkName: String = "",
    val selectedCategory: DrinkCategory = DrinkCategory.OTHER,
    val selectedDrink: Drink? = null,
    val selectedDrinkUnit: DrinkUnit = DrinkUnit("milliliters", 1),
    val selectedAmount: Int = 100,
    val inputAmount: Double = 100.0,
    val alcoholPercentage: Double = 0.0,
    val cost: Double = 0.0,
    val recipient: String = "Me",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val notes: String = "",
    val locationName: String = "",
    val isFavorite: Boolean = false,
    val longitude: Double? = null,
    val latitude: Double? = null,
)

data class DrinkFormOptions(
    val categoryOptions: List<DrinkCategory> = emptyList(),
    val amountOptions: List<DrinkUnit> = emptyList(),
    val drinkOptions: List<Drink> = emptyList(),
    val recipientOptions: List<String> = emptyList(),
)



@HiltViewModel
class DrinkFormViewModel @Inject constructor(
    private val userAndUserDrinkLogRepository: UserAndUserDrinkLogRepository,
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {

        }

    }
    private val _formUiState = MutableStateFlow(DrinkFormUiState(inputs = DrinkFormInput()))
    val formUiState = _formUiState.asStateFlow()



    fun processEvent(event: DrinkFormEvent){
        when (event){
            is DrinkFormEvent.OnABVChange -> onABVChange(event.abv)
            is DrinkFormEvent.OnAmountChange -> onAmountChange(event.amount)
            is DrinkFormEvent.OnCategoryChange -> onCategoryChange(event.category)
            is DrinkFormEvent.OnDateChange -> onDateChange(event.date)
            is DrinkFormEvent.OnDrinkChange -> onDrinkChange(event.drink)
            is DrinkFormEvent.OnDrinkNameChange -> onDrinkNameChange(event.name)
            is DrinkFormEvent.OnDrinkUnitChange -> onDrinkUnitChange(event.drinkUnit)
            is DrinkFormEvent.OnLocationChange -> onLocationChange(event.location)
            is DrinkFormEvent.OnNotesChange -> onNotesChange(event.notes)
            is DrinkFormEvent.OnPriceChange -> onPriceChange(event.price)
            is DrinkFormEvent.OnRecipientChange -> onRecipientChange(event.recipient)
            is DrinkFormEvent.OnTimeChange -> onTimeChange(event.time)
            is DrinkFormEvent.GetDrinkOptions -> getDrinkOptions(event.category)
            is DrinkFormEvent.GetAmountOptions -> getAmountOptions(event.category)
            DrinkFormEvent.OnSaveDrinkLog -> saveDrinkLog()
            DrinkFormEvent.ConsumeEffect -> consumeEffect()
            DrinkFormEvent.GetRecipientOptions -> getRecipientOptions()
        }

    }

    private fun getRecipientOptions() {

    }
    private fun saveDrinkLog() {

    }
    private fun onTimeChange(time: LocalTime) {

    }
    private fun onDateChange(date: LocalDate) {

    }
    private fun onNotesChange(notes: String) {

    }
    private fun onLocationChange(location: String) {

    }
    private fun onRecipientChange(recipient: String) {

    }
    private fun onPriceChange(price: Double) {

    }
    private fun onABVChange(abv: Double) {

    }
    private fun onAmountChange(amount: Double) {

    }
    private fun onDrinkUnitChange(drinkUnit: DrinkUnit) {

    }

    private fun onDrinkChange(drink: Drink) {

    }
    private fun onCategoryChange(category: DrinkCategory) {

    }
    private fun onDrinkNameChange(name: String) {

    }
    private fun getAmountOptions(category: DrinkCategory) {

    }
    private fun getDrinkOptions(category: DrinkCategory) {

    }

    private fun consumeEffect() {

    }
}

