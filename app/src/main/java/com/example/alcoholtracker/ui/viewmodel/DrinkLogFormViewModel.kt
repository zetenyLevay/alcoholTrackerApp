package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.domain.logic.handlers.DrinkHandlerRegistry
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkCategory.*
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.getLocalDateTime
import com.example.alcoholtracker.ui.navigation.AddDrink
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

sealed interface DrinkLogFormEvent{
    data class OnDrinkLogNameChange(val name: String) : DrinkLogFormEvent
    data class OnCategoryChange(val category: DrinkCategory) : DrinkLogFormEvent
    data class OnDrinkLogChange(val drink: Drink) : DrinkLogFormEvent
    data class OnDrinkLogUnitChange(val drinkUnit: DrinkUnit) : DrinkLogFormEvent
    data class OnAmountChange(val amount: Double) : DrinkLogFormEvent
    data class OnABVChange(val abv: Double) : DrinkLogFormEvent
    data class OnPriceChange(val price: Double) : DrinkLogFormEvent
    data class OnRecipientChange(val recipient: String) : DrinkLogFormEvent
    data class OnDateChange(val date: LocalDate) : DrinkLogFormEvent
    data class OnTimeChange(val time: LocalTime) : DrinkLogFormEvent
    data class OnNotesChange(val notes: String) : DrinkLogFormEvent
    data class OnLocationChange(val location: String) : DrinkLogFormEvent
    data object OnSaveDrinkLog : DrinkLogFormEvent
    data object ConsumeEffect : DrinkLogFormEvent
}

sealed interface DrinkLogFormEffect{
    data class ShowError(val message: String) : DrinkLogFormEffect
    data object SaveDrinkLog : DrinkLogFormEffect
}

data class DrinkLogFormUiState(
    val isEdit: Boolean = false,
    val isLoading: Boolean = false,
    val inputs: DrinkLogFormInput = DrinkLogFormInput(),
    val options: DrinkLogFormOptions = DrinkLogFormOptions(),
    val effect: DrinkLogFormEffect? = null,
)

data class DrinkLogFormInput(
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
    val imgURI: String? = null,
    val userId: String? = null,
)

data class DrinkLogFormOptions(
    val categoryOptions: List<DrinkCategory> = emptyList(),
    val amountOptions: List<DrinkUnit> = emptyList(),
    val drinkOptions: List<Drink> = emptyList(),
    val recipientOptions: List<String> = emptyList(),
)

data class DrinkLogLocalState(
    val isEdit: Boolean = false,
    val isLoading: Boolean = false,
    val effect: DrinkLogFormEffect? = null,
)

@HiltViewModel
class DrinkLogFormViewModel @Inject constructor(
    private val logRepo: DrinkLogRepository,
    private val handlerRegistry: DrinkHandlerRegistry,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var searchJob: Job? = null
    private val _localState = MutableStateFlow(DrinkLogLocalState())
    private val _inputs = MutableStateFlow(DrinkLogFormInput())
    private val _drinkOptions = MutableStateFlow<List<Drink>>(emptyList())
    private val _filteredRecipients = combine(
        logRepo.getRecipients(),
        _inputs.map { it.recipient }
    ){ recipients, query ->
        if (query.isBlank()){
            recipients
        } else {
            recipients.filter {
                it.contains(query, ignoreCase = true)
            }
        }
    }.catch { emit(emptyList()) }
    private val _amountOptions = _inputs.map { it.selectedCategory }.map{
        when(it) {
            BEER -> handlerRegistry.beerHandler.getUnitOptions()
            WINE -> handlerRegistry.wineHandler.getUnitOptions()
            SPIRIT -> handlerRegistry.spiritHandler.getUnitOptions()
            COCKTAIL -> handlerRegistry.cocktailHandler.getUnitOptions()
            OTHER -> handlerRegistry.otherHandler.getUnitOptions()
        }

    }
    val formUiState: StateFlow<DrinkLogFormUiState> = combine(
        _inputs,
        _localState,
        _drinkOptions,
        _filteredRecipients,
        _amountOptions
    ) { inputs, localState, drinks, recipients, amounts ->
        DrinkLogFormUiState(
            isEdit = localState.isEdit,
            isLoading = localState.isLoading,
            inputs = inputs,
            effect = localState.effect,
            options = DrinkLogFormOptions(
                categoryOptions = DrinkCategory.entries,
                amountOptions = amounts,
                drinkOptions = drinks,
                recipientOptions = recipients
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DrinkLogFormUiState(isLoading = true)
    )

    init {

        val route = savedStateHandle.toRoute<AddDrink>()

        if (route.logId != -1) {
            loadLogForEdit(route.logId)
        }


    }


    fun processEvent(event: DrinkLogFormEvent){
        when (event){
            is DrinkLogFormEvent.OnABVChange -> onABVChange(event.abv)
            is DrinkLogFormEvent.OnAmountChange -> onAmountChange(event.amount)
            is DrinkLogFormEvent.OnCategoryChange -> onCategoryChange(event.category)
            is DrinkLogFormEvent.OnDateChange -> onDateChange(event.date)
            is DrinkLogFormEvent.OnDrinkLogChange -> onDrinkChange(event.drink)
            is DrinkLogFormEvent.OnDrinkLogNameChange -> onDrinkNameChange(event.name)
            is DrinkLogFormEvent.OnDrinkLogUnitChange -> onDrinkUnitChange(event.drinkUnit)
            is DrinkLogFormEvent.OnLocationChange -> onLocationChange(event.location)
            is DrinkLogFormEvent.OnNotesChange -> onNotesChange(event.notes)
            is DrinkLogFormEvent.OnPriceChange -> onPriceChange(event.price)
            is DrinkLogFormEvent.OnRecipientChange -> onRecipientChange(event.recipient)
            is DrinkLogFormEvent.OnTimeChange -> onTimeChange(event.time)
            DrinkLogFormEvent.OnSaveDrinkLog -> saveDrinkLog()
            DrinkLogFormEvent.ConsumeEffect -> consumeEffect()
        }
    }

    private fun loadLogForEdit(logId: Int){
        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {
                val logToEdit =logRepo.getDrinkById(logId)

                if (logToEdit != null){
                   _inputs.update {
                        it.copy(
                            logId = logId,
                            drinkName = logToEdit.name,
                            selectedCategory = logToEdit.category,
                            selectedDrink = null,
                            selectedDrinkUnit = logToEdit.drinkUnit ?: DrinkUnit("milliliters", 1),
                            selectedAmount = logToEdit.amount,
                            inputAmount = logToEdit.inputAmount ?: 100.0,
                            alcoholPercentage = logToEdit.alcoholPercentage ?: 0.0,
                            cost = logToEdit.cost ?: 0.0,
                            recipient = logToEdit.recipient ?: "Me",
                            selectedDate = logToEdit.date.toLocalDate() ?: LocalDate.now(),
                            selectedTime = logToEdit.date.toLocalTime() ?: LocalTime.now(),
                            notes = logToEdit.notes ?: "",
                            locationName = logToEdit.locationName ?: "",
                            isFavorite = logToEdit.isFavorite,
                            longitude = logToEdit.longitude,
                            latitude = logToEdit.latitude,
                            imgURI = logToEdit.imgURI,
                            userId = logToEdit.userId,
                        )
                    }
                    _localState.update {
                        it.copy(
                            isEdit = true,
                            isLoading = false
                        )
                    }

                }
            } catch (e: Exception) {
                _localState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.ShowError("Error loading drink log"),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveDrinkLog() {

        val inputs = _inputs.value

        val newLog = UserDrinkLog(
            drinkId = inputs.selectedDrink?.drinkId,
            userId = inputs.userId?: "",
            logId = inputs.logId ?: 0,
            name = inputs.drinkName,
            cost = inputs.cost,
            alcoholPercentage = inputs.alcoholPercentage,
            amount = inputs.selectedAmount,
            category = inputs.selectedCategory,
            recipient = inputs.recipient,
            inputAmount = inputs.inputAmount,
            isFavorite = inputs.isFavorite,
            imgURI = inputs.imgURI,
            notes = inputs.notes,
            locationName = inputs.locationName,
            longitude = inputs.longitude,
            latitude = inputs.latitude,
            drinkUnit = inputs.selectedDrinkUnit,
            date = getLocalDateTime(inputs.selectedDate, inputs.selectedTime)
        )

        viewModelScope.launch {
            _localState.update { it.copy(isLoading = true) }
            try {

                if (_localState.value.isEdit){
                    logRepo.updateDrinkLog(newLog)
                }
                else{
                    logRepo.insertDrinkLog(newLog)
                }
                _localState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.SaveDrinkLog,
                        isLoading = false
                    )}

            }catch (
                e: Exception
            ) {
                _localState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.ShowError("Error saving drink log"),
                        isLoading = false
                    )
                }
            }
        }

    }
    private fun onTimeChange(time: LocalTime) {
        _inputs.update {
            it.copy(
                selectedTime = time
            )
        }
    }
    private fun onDateChange(date: LocalDate) {
        _inputs.update {
            it.copy(
                selectedDate = date
            )
        }
    }
    private fun onNotesChange(notes: String) {
        _inputs.update {
            it.copy(
                notes = notes
            )
        }
    }
    private fun onLocationChange(location: String) {
        _inputs.update {
            it.copy(
                locationName = location
            )
        }
    }
    private fun onRecipientChange(recipient: String) {
        _inputs.update {
            it.copy(
                recipient = recipient
            )
        }
    }
    private fun onPriceChange(price: Double) {
        _inputs.update {
            it.copy(
                cost = price
            )
        }
    }
    private fun onABVChange(abv: Double) {
       _inputs.update {
            it.copy(
                alcoholPercentage = abv
            )
        }
    }
    private fun onAmountChange(amount: Double) {
        _inputs.update {
            it.copy(
                inputAmount = amount
            )
        }
    }
    private fun onDrinkUnitChange(drinkUnit: DrinkUnit) {
        _inputs.update {
            it.copy(
                selectedDrinkUnit = drinkUnit
            )
        }
    }

    private fun onDrinkChange(drink: Drink) {
        _inputs.update {
            it.copy(
                selectedDrink = drink
            )
        }
    }
    private fun onCategoryChange(category: DrinkCategory) {
        _inputs.update {
            it.copy(
                selectedCategory = category
            )
        }
        onDrinkNameChange(_inputs.value.drinkName)
    }
    private fun onDrinkNameChange(name: String) {
        _inputs.update {
            it.copy(
                drinkName = name
            )
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)

            if (name.isBlank()){
                _drinkOptions.value = emptyList()
                return@launch
            }
            _localState.update { it.copy(isLoading = true) }
            try {
                val category = _inputs.value.selectedCategory
                val options = when (category) {
                    BEER -> handlerRegistry.beerHandler.fetchSuggestions(name)
                    WINE -> handlerRegistry.wineHandler.fetchSuggestions(name)
                    SPIRIT -> handlerRegistry.spiritHandler.fetchSuggestions(name)
                    COCKTAIL -> handlerRegistry.cocktailHandler.fetchSuggestions(name)
                    OTHER -> handlerRegistry.otherHandler.fetchSuggestions(name)
                }
                _drinkOptions.value = options
                _localState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _localState.update { it.copy(effect = DrinkLogFormEffect.ShowError("Error fetching drinks"), isLoading = false) }
            }
        }
    }
    private fun consumeEffect() {
        _localState.update { it.copy(effect = null) }
    }
}

