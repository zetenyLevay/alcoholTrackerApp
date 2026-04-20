package com.example.alcoholtracker.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface UserEvents {
    data class OnEmailChange(val email: String) : UserEvents
    data class OnPasswordChange(val password: String) : UserEvents
    data object SignIn : UserEvents
    data object SignUp : UserEvents
    data object ForgotPassword : UserEvents
    data object AnonymousSignIn : UserEvents
    data object ConsumeEffect : UserEvents
}

sealed interface UserEffect {
    data class ShowError(val message: String) : UserEffect
    data object NavigateToHome : UserEffect
}

data class UserUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val effect: UserEffect? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepo: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState

    fun processEvent(event: UserEvents) {
        when (event) {
            is UserEvents.OnEmailChange -> onEmailChange(event.email)
            is UserEvents.OnPasswordChange -> onPasswordChange(event.password)
            UserEvents.SignIn -> signIn(_uiState.value.emailInput, _uiState.value.passwordInput)
            UserEvents.SignUp -> signUp(_uiState.value.emailInput, _uiState.value.passwordInput)
            UserEvents.AnonymousSignIn -> signInAnonymously()
            UserEvents.ConsumeEffect -> consumeEffect()
            UserEvents.ForgotPassword -> forgotPassword()
        }
    }

    private fun signIn(email: String, password: String) {

    }
    private fun signUp(email: String, password: String) {

    }

    private fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            userRepo.signInAnonymously()
            _uiState.update {
                it.copy(effect = UserEffect.NavigateToHome, isLoading = false)
            }
        }

    }

    private fun onEmailChange(email: String) {
        _uiState.update { it.copy(emailInput = email) }
    }
    private fun onPasswordChange(password: String) {
        _uiState.update { it.copy(passwordInput = password) }
    }
    private fun consumeEffect() {
        _uiState.update { it.copy(effect = null) }
    }
    private fun forgotPassword() {

    }


}
