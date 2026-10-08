package com.example.alcoholtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alcoholtracker.data.model.Account
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserEvents {
    data class OnEmailChange(val email: String) : UserEvents
    data class OnPasswordChange(val password: String) : UserEvents
    data object SignIn : UserEvents
    data object SignUp : UserEvents
    data object ForgotPassword : UserEvents
    data object AnonymousSignIn : UserEvents
    data object SignOut: UserEvents
    data object ConsumeEffect : UserEvents

}

sealed interface UserEffect {
    data class ShowError(val message: String) : UserEffect
    data object NavigateToHome : UserEffect
    data object AccountCreated : UserEffect
}

data class UserUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val effect: UserEffect? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepo: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState

    val account: StateFlow<Account?> = userRepo.account

    fun processEvent(event: UserEvents) {
        when (event) {
            is UserEvents.OnEmailChange -> onEmailChange(event.email)
            is UserEvents.OnPasswordChange -> onPasswordChange(event.password)
            UserEvents.SignIn -> signIn(_uiState.value.emailInput, _uiState.value.passwordInput)
            UserEvents.SignUp -> signUp(_uiState.value.emailInput, _uiState.value.passwordInput)
            UserEvents.AnonymousSignIn -> signInAnonymously()
            UserEvents.ConsumeEffect -> consumeEffect()
            UserEvents.ForgotPassword -> forgotPassword()
            UserEvents.SignOut -> signOut();
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepo.signIn(email.trim(), password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, effect = UserEffect.NavigateToHome) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.toAuthMessage()) }
                }
        }
    }

    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (account.value?.isGuest == true) {
                userRepo.linkAnonymousAccount(email.trim(), password)
            } else {
                userRepo.createAccount(email.trim(), password)
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, effect = UserEffect.AccountCreated) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.toAuthMessage()) }
                }
        }
    }

    private fun signOut(){
        userRepo.signOut()
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
        _uiState.update { it.copy(emailInput = email, errorMessage = null) }
    }
    private fun onPasswordChange(password: String) {
        _uiState.update { it.copy(passwordInput = password, errorMessage = null) }
    }
    private fun consumeEffect() {
        _uiState.update { it.copy(effect = null) }
    }
    private fun forgotPassword() {

    }

    private fun Throwable.toAuthMessage(): String = when (this) {
        is FirebaseAuthUserCollisionException -> "An account with this email already exists"
        is FirebaseAuthWeakPasswordException -> "Use a password with at least 6 characters"
        is FirebaseAuthInvalidUserException -> "No account found with this email"
        is FirebaseAuthInvalidCredentialsException ->
            if (errorCode == "ERROR_INVALID_EMAIL") "Enter a valid email address"
            else "Wrong email or password"
        is FirebaseNetworkException -> "No connection. Try again when you're online"
        is FirebaseAuthException ->
            if (errorCode == "ERROR_OPERATION_NOT_ALLOWED") "Email sign-in isn't enabled for this app"
            else message ?: "Something went wrong"
        else -> message ?: "Something went wrong"
    }


}
