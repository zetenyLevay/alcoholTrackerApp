package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.repository.UserAndUserDrinkLogRepository
import com.example.alcoholtracker.domain.logic.handlers.BeerHandler
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.domain.usecase.DrinkCreateRequest
import com.example.alcoholtracker.domain.usecase.DrinkHandler
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.checkNewUnit
import com.google.firebase.auth.FirebaseAuth
import com.google.type.DateTime
import com.vamsi.snapnotify.SnapNotify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAmount
import javax.inject.Inject

@HiltViewModel
class UserAndUserDrinkLogViewModel @Inject constructor(
    private val userAndUserDrinkLogRepository: UserAndUserDrinkLogRepository,
    private val drinkHandler: DrinkHandler,
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId
    private val _searchQuery = MutableStateFlow("")
    private val _drinkById = MutableStateFlow<UserDrinkLog?>(null)
    val drinkById = _drinkById.asStateFlow()

    fun getDrinkById(logId: Int?) {
        if (logId == null) return
        viewModelScope.launch {
            val drink = userAndUserDrinkLogRepository.getDrinkById(logId)
            _drinkById.value = drink
        }
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val allDrinkLogs: StateFlow<List<UserDrinkLog>> = userId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList())
        else userAndUserDrinkLogRepository.getDrinkLogsByUserId(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rawRecentFlow = _userId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList())
        else userAndUserDrinkLogRepository.getRecentLogs(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rawFrequentFlow = _userId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList())
        else userAndUserDrinkLogRepository.getFrequentLogs(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rawFavoriteFlow = _userId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList())
        else userAndUserDrinkLogRepository.getFavoriteLogs(id)
    }
    val recentDrinks: StateFlow<List<UserDrinkLog>> =
        combine(_searchQuery, rawRecentFlow) { query, logs ->
            if (query.isBlank()) logs
            else logs.filter { it.name.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val frequentLogs: StateFlow<List<UserDrinkLog>> =
        combine(_searchQuery, rawFrequentFlow) { query, logs ->
            if (query.isBlank()) logs
            else logs.filter { it.name.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteLogs: StateFlow<List<UserDrinkLog>> =
        combine(_searchQuery, rawFavoriteFlow) { query, logs ->
            if (query.isBlank()) logs
            else logs.filter { it.name.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun setUserId(userId: String) {

        _userId.value = userId
        val user = FirebaseAuth.getInstance().currentUser

        viewModelScope.launch {
            val localUser = userAndUserDrinkLogRepository.getUserById(userId)
            if (localUser == null) {
                val newUser = User(
                    userId,
                    user?.displayName ?: "",
                    user?.email ?: ""
                )
                userAndUserDrinkLogRepository.insertUser(newUser)
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val twoDaySummary: StateFlow<List<UserDrinkLog>?> = userId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList())
        else userAndUserDrinkLogRepository.getTwoDayLogsByUser(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun deleteDrink(log: UserDrinkLog) {
        viewModelScope.launch {
            drinkHandler.deleteDrink(log)
        }
    }


    fun logDrink(request: DrinkCreateRequest) {

        viewModelScope.launch {
            try {
                drinkHandler.createDrink(request)
                SnapNotify.showSuccess("Drink logged")
            } catch (e: Exception) {
                SnapNotify.showError("Failed to log drink")
            }

        }
    }

    fun updateDrink(drinkToUpdate: Int, request: DrinkCreateRequest) {
        viewModelScope.launch {
            drinkHandler.editDrink(drinkToUpdate, request)
            getDrinkById(drinkToUpdate)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rawRecipientOptions = _userId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList())
        else userAndUserDrinkLogRepository.getRecipients(id)
    }

    val recipientOptions: StateFlow<List<String>> =
        combine(_searchQuery, rawRecipientOptions) { query, logs ->
            if (query.isBlank()) logs
            else logs.filter { it.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())





    val _formState = MutableStateFlow(DrinkFormState())
    val formState = _formState.asStateFlow()

    fun updateDrinkName(name: String) {
        _formState.value = _formState.value.copy(drinkName = name)
    }

    fun updateSelectedCategory(category: DrinkCategory) {
        _formState.value = _formState.value.copy(selectedCategory = category)
    }

    fun updateSelectedDrink(drink: Drink) {
        _formState.update { currentState ->
            currentState.copy(
                selectedDrink = drink,
                alcoholPercentage = drink.alcoholContent,
            )
        }
    }
    fun updateSelectedDrinkUnit(drinkUnit: DrinkUnit) {
        val previousUnit = _formState.value.selectedDrinkUnit
        val amountChange = checkNewUnit(previousUnit, drinkUnit)

        val finalInputAmount =
            if (amountChange != 0) { amountChange }
            else { _formState.value.inputAmount }

        _formState.update { currentState ->
        currentState.copy(
            selectedDrinkUnit = drinkUnit,
            inputAmount = finalInputAmount.toDouble(),
        )
    }
    }
    fun updateSelectedAmount(amount: Double, unit: DrinkUnit) {
        _formState.value = _formState.value.copy(
            selectedAmount = (amount*unit.amount).toInt(),
            inputAmount = amount,
            selectedDrinkUnit = unit
        )
    }
    fun updateABV(abv: Double) {
        _formState.value = _formState.value.copy(alcoholPercentage = abv)
    }
    fun updatePrice(price: Double) {
        _formState.value = _formState.value.copy(cost = price)
    }
    fun updateRecipient(recipient: String) {
        _formState.value = _formState.value.copy(recipient = recipient)
    }
    fun updateSelectedDate(date: LocalDate) {
        _formState.value = _formState.value.copy(selectedDate = date)
    }
    fun updateSelectedTime(time: LocalTime) {
        _formState.value = _formState.value.copy(selectedTime = time)
    }
    fun updateNotes(notes: String) {
        _formState.value = _formState.value.copy(notes = notes)
    }
    fun updateLocation(location: String) {
        _formState.value = _formState.value.copy(locationName = location)
    }
    fun populateFormForEdit(drink: UserDrinkLog) {
    _formState.value = DrinkFormState(
        drinkName = drink.name,
        selectedCategory = drink.category,
        alcoholPercentage = drink.alcoholPercentage ?: 0.0,
        cost = drink.cost ?: 0.0,
        recipient = drink.recipient ?: "Me",
        selectedDate = drink.date.toLocalDate(),
        selectedTime = drink.date.toLocalTime(),
        notes = drink.notes ?: "",
        locationName = drink.locationName ?: "",
        isEdit = true,
        logId = drink.logId,
        isFavorite = drink.isFavorite,
        longitude = drink.longitude,
        latitude = drink.latitude,
        selectedAmount = drink.amount,
        inputAmount = drink.inputAmount ?: 0.0,
        selectedDrinkUnit = drink.drinkUnit ?: DrinkUnit("milliliters", 1)

    )
}


    init {
        viewModelScope.launch(Dispatchers.IO) {

            val currentUserId = auth.currentUser?.uid ?: ""
            if (currentUserId.isNotEmpty()) {
                _userId.value = currentUserId
                println("ViewModel created/restored. userId: $currentUserId")
            } else {
                println("Warning: No user logged in!")
            }
            auth.addAuthStateListener { firebaseAuth ->
                val newUserId = firebaseAuth.currentUser?.uid ?: ""
                if (newUserId != _userId.value) {
                    _userId.value = newUserId
                    println("Auth state changed. New userId: $newUserId")
                }
            }
        }
    }
}

data class DrinkFormState(
    val drinkName: String = "",
    val selectedCategory: DrinkCategory = DrinkCategory.OTHER,
    val selectedDrink: Drink? = null,
    val selectedDrinkUnit: DrinkUnit = DrinkUnit("milliliters", 1),
    val selectedAmount: Int = 100,
    val inputAmount: Double = 1.0,
    val alcoholPercentage: Double = 0.0,
    val cost: Double = 0.0,
    val recipient: String = "Me",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val notes: String = "",
    val locationName: String = "",
    val isEdit: Boolean = false,
    val logId: Int? = null,
    val isFavorite: Boolean = false,
    val longitude: Double? = null,
    val latitude: Double? = null
)

data class DrinkFormEvents(
    val onDrinkNameChange: (String) -> Unit,
    val onCategoryChange: (DrinkCategory) -> Unit,
    val onDrinkChange: (Drink) -> Unit,
    val onDrinkUnitChange: (DrinkUnit) -> Unit,
    val onAmountChange: (Double) -> Unit,
    val onABVChange: (Double) -> Unit,
    val onPriceChange: (Double) -> Unit,
    val onRecipientChange: (String) -> Unit,
    val onDateChange: (LocalDate) -> Unit,
    val onTimeChange: (LocalTime) -> Unit,
    val onNotesChange: (String) -> Unit,
    val onLocationChange: (String) -> Unit,
    val onSaveDrink: () -> Unit
)

