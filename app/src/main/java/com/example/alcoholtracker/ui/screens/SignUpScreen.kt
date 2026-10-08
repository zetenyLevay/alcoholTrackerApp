package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.ui.components.AuthFields
import com.example.alcoholtracker.ui.components.SignUpTopBar
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.UserEffect
import com.example.alcoholtracker.ui.viewmodel.UserEvents
import com.example.alcoholtracker.ui.viewmodel.UserUiState

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onAccountCreated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.effect) {
        if (state.effect == UserEffect.AccountCreated) {
            viewModel.processEvent(UserEvents.ConsumeEffect)
            onAccountCreated()
            SnackbarController.sendEvent(SnackBarEvent(message = "Account created"))
        }
    }

    SignUpScreen(
        state = state,
        onEvent = viewModel::processEvent,
        onBackClick = onBackClick
    )
}

@Composable
fun SignUpScreen(
    state: UserUiState,
    onEvent: (UserEvents) -> Unit,
    onBackClick: () -> Unit,
) {
    val isFormValid = state.emailInput.isNotBlank() && state.passwordInput.length >= 6
    val submit = { if (isFormValid && !state.isLoading) onEvent(UserEvents.SignUp) }

    Scaffold(
        topBar = { SignUpTopBar(onBackClick = onBackClick) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Everything you've logged as a guest moves to your new account.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AuthFields(
                email = state.emailInput,
                onEmailChange = { onEvent(UserEvents.OnEmailChange(it)) },
                password = state.passwordInput,
                onPasswordChange = { onEvent(UserEvents.OnPasswordChange(it)) },
                errorMessage = state.errorMessage,
                enabled = !state.isLoading,
                onDone = submit
            )
            Button(
                onClick = submit,
                enabled = isFormValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create account")
                    }
                }
            }
        }
    }
}
