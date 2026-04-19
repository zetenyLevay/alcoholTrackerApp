package com.example.alcoholtracker.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface UserEvents {
    data class OnEmailChange(val email: String) : UserEvents
    data class OnPasswordChange(val password: String) : UserEvents
    data object SignIn : UserEvents
    data object SignUp : UserEvents
    data object SignOut : UserEvents
    data object ForgotPassword : UserEvents
    data object AnonymousSignIn : UserEvents
    data object ConsumeEffect : UserEvents
}

sealed interface UserEffect {
    data class ShowError(val message: String) : UserEffect
    data class ShowPasswordResetSent(val email: String) : UserEffect
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
) : ViewModel() {



}
