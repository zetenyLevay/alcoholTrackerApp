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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    data class GetAmountOptions(val category: DrinkCategory) : DrinkLogFormEvent
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

@HiltViewModel
class DrinkLogFormViewModel @Inject constructor(
    private val logRepo: DrinkLogRepository,
    private val handlerRegistry: DrinkHandlerRegistry,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var recipientOptionsMaster = listOf<String>()
    private var searchJob: Job? = null
    private val _formUiState = MutableStateFlow(DrinkLogFormUiState(inputs = DrinkLogFormInput()))
    val formUiState = _formUiState.asStateFlow()

    init {

        try {
             logRepo.getRecipients().onEach {
                recipientOptionsMaster = it
                filterRecipients(_formUiState.value.inputs.recipient)
            }.launchIn(viewModelScope)
        }catch (e: Exception){
            _formUiState.update {
                it.copy(
                    effect = DrinkLogFormEffect.ShowError("Error fetching recipients"),
                )
            }
        }

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
            is DrinkLogFormEvent.GetAmountOptions -> getAmountOptions(event.category)
            DrinkLogFormEvent.OnSaveDrinkLog -> saveDrinkLog()
            DrinkLogFormEvent.ConsumeEffect -> consumeEffect()
        }
    }

    private fun loadLogForEdit(logId: Int){
        viewModelScope.launch {
            _formUiState.update { it.copy(isLoading = true) }
            try {
                val logToEdit =logRepo.getDrinkById(logId)

                if (logToEdit != null){
                    _formUiState.update {
                        it.copy(
                            isEdit = true,
                            isLoading = false,
                            inputs = it.inputs.copy(
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
                        )
                    }
                    getAmountOptions(logToEdit.category)
                    filterRecipients(logToEdit.recipient ?: "")
                }
            } catch (e: Exception) {
                _formUiState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.ShowError("Error loading drink log"),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveDrinkLog() {

        val inputs = _formUiState.value.inputs

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
            _formUiState.update { it.copy(isLoading = true) }
            try {

                if (_formUiState.value.isEdit){
                    logRepo.updateDrinkLog(newLog)
                }
                else{
                    logRepo.insertDrinkLog(newLog)
                }
                _formUiState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.SaveDrinkLog,
                        isLoading = false
                    )}

            }catch (
                e: Exception
            ) {
                _formUiState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.ShowError("Error saving drink log"),
                        isLoading = false
                    )
                }
            }
        }

    }
    private fun onTimeChange(time: LocalTime) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    selectedTime = time
                ),
            )
        }
    }
    private fun onDateChange(date: LocalDate) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    selectedDate = date
                ),
            )
        }
    }
    private fun onNotesChange(notes: String) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    notes = notes
                ),
            )
        }
    }
    private fun onLocationChange(location: String) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    locationName = location
                ),
            )
        }
    }
    private fun onRecipientChange(recipient: String) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    recipient = recipient
                ),
            )
        }
        filterRecipients(recipient)
    }
    private fun onPriceChange(price: Double) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    cost = price
                ),
            )
        }
    }
    private fun onABVChange(abv: Double) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    alcoholPercentage = abv
                ),
            )
        }
    }
    private fun onAmountChange(amount: Double) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    inputAmount = amount
                ),
                isLoading = false
            )
        }
    }
    private fun onDrinkUnitChange(drinkUnit: DrinkUnit) {
        _formUiState.update {
                it.copy(
                    inputs = it.inputs.copy(
                        selectedDrinkUnit = drinkUnit
                    ),
                )
            }
        }

    private fun onDrinkChange(drink: Drink) {

        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    selectedDrink = drink
                )
            )
        }
    }
    private fun onCategoryChange(category: DrinkCategory) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    selectedCategory = category
                ),
            )
        }
        getAmountOptions(category)
        viewModelScope.launch {
            getDrinkOptions(_formUiState.value.inputs.drinkName,category)
        }


    }
    private fun onDrinkNameChange(name: String) {
        _formUiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    drinkName = name
                ),
            )
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)

            if (name.isBlank()){
                _formUiState.update { it.copy(options = it.options.copy(drinkOptions = emptyList())) }
                return@launch
            }
            getDrinkOptions(name,_formUiState.value.inputs.selectedCategory)
        }
    }
    private fun getAmountOptions(category: DrinkCategory) {

            _formUiState.update { it.copy(isLoading = true) }
            try {
                val options = when (category) {
                    BEER -> handlerRegistry.beerHandler.getUnitOptions()
                    WINE -> handlerRegistry.wineHandler.getUnitOptions()
                    SPIRIT -> handlerRegistry.spiritHandler.getUnitOptions()
                    COCKTAIL -> handlerRegistry.cocktailHandler.getUnitOptions()
                    OTHER -> handlerRegistry.otherHandler.getUnitOptions()
                }

                _formUiState.update {
                    it.copy(
                        options = it.options.copy(
                            amountOptions = options
                        ),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                 _formUiState.update {
                    it.copy(
                        effect = DrinkLogFormEffect.ShowError("Error fetching drink units"),
                        isLoading = false
                    )
                }
            }
    }
    private suspend fun getDrinkOptions(query: String,category: DrinkCategory) {

            _formUiState.update { it.copy(isLoading = true) }
            try {


                val options = when (category) {
                    BEER -> handlerRegistry.beerHandler.fetchSuggestions(query)
                    WINE -> handlerRegistry.wineHandler.fetchSuggestions(query)
                    SPIRIT -> handlerRegistry.spiritHandler.fetchSuggestions(query)
                    COCKTAIL -> handlerRegistry.cocktailHandler.fetchSuggestions(query)
                    OTHER -> handlerRegistry.otherHandler.fetchSuggestions(query)
                }

                _formUiState.update {
                    it.copy(
                        options = it.options.copy(
                            drinkOptions = options
                        ),
                        isLoading = false
                    )
                }
            }
            catch (e: Exception) {
                 _formUiState.update {
                it.copy(
                    effect = DrinkLogFormEffect.ShowError("Error fetching drinks"),
                    isLoading = false
                )
            }
            }
    }

    private fun filterRecipients(query: String) {
        val filteredList = if (query.isBlank()) {
            recipientOptionsMaster
        } else {
            recipientOptionsMaster.filter {
                it.contains(query, ignoreCase = true)
            }
        }

        _formUiState.update { it.copy(options = it.options.copy(recipientOptions = filteredList)) }
    }

    private fun consumeEffect() {
        _formUiState.update { it.copy(effect = null) }
    }
}

