package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.data.repository.UserAndUserDrinkLogRepository
import com.example.alcoholtracker.domain.usecase.DrinkCreateRequest
import com.example.alcoholtracker.domain.usecase.DrinkHandler
import com.google.firebase.auth.FirebaseAuth
import com.vamsi.snapnotify.SnapNotify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAndUserDrinkLogViewModel @Inject constructor(
    private val userAndUserDrinkLogRepository: UserAndUserDrinkLogRepository,
    private val drinkHandler: DrinkHandler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId
    private val _twoDaySummary = MutableStateFlow<List<UserDrinkLog>?>(null)

    private val _searchQuery = MutableStateFlow("")

    private val _drinkById = MutableStateFlow<UserDrinkLog?>(null)
    val drinkById = _drinkById.asStateFlow()


    fun getDrinkById(logId: Int?) {

        if (logId == null) {
            _drinkById.value = null
            return
        } else {
            viewModelScope.launch {
                val drink = userAndUserDrinkLogRepository.getDrinkById(logId)
                _drinkById.value = drink
            }
        }


    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getDrinkLogs(userId: String): StateFlow<List<UserDrinkLog>> {
        return userAndUserDrinkLogRepository.getDrinkLogsByUserId(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }


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


    fun getTwoDaySummary(userId: String): StateFlow<List<UserDrinkLog>> {
        return userAndUserDrinkLogRepository.getTwoDayLogsByUser(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

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
        }
    }

    fun getRecipients(userId: String): StateFlow<List<String>> {
        return userAndUserDrinkLogRepository.getRecipients(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    init {
        viewModelScope.launch {

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
