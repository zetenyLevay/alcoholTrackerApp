package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alcoholtracker.R
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.SnackbarType
import com.example.alcoholtracker.ui.components.ArcBackground
import com.example.alcoholtracker.ui.components.AuthFields
import com.example.alcoholtracker.ui.components.LogInTopBar
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.UserEffect
import com.example.alcoholtracker.ui.viewmodel.UserEvents
import com.example.alcoholtracker.ui.viewmodel.UserUiState

/**
 * Signed-out start screen, or — when [onBackClick] is given — a guest logging in to an
 * existing account from Profile.
 */
@Composable
fun SignInScreen(
    onBackClick: (() -> Unit)? = null,
    onSignedIn: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isGuestLogIn = onBackClick != null

    LaunchedEffect(state.effect) {
        if (state.effect == UserEffect.NavigateToHome) {
            viewModel.processEvent(UserEvents.ConsumeEffect)
            if (isGuestLogIn) {
                onSignedIn()
                SnackbarController.sendEvent(
                    SnackBarEvent(message = "Logged in", type = SnackbarType.SUCCESS)
                )
            }
        }
    }

    if (onBackClick != null) {
        GuestLogInScreen(
            state = state,
            onEvent = viewModel::processEvent,
            onBackClick = onBackClick
        )
    } else {
        SignInScreen(
            state = state,
            onEvent = viewModel::processEvent
        )
    }
}

@Composable
fun SignInScreen(
    state: UserUiState,
    onEvent: (UserEvents) -> Unit,
) {
    val isFormValid = state.emailInput.isNotBlank() && state.passwordInput.isNotEmpty()
    val signIn = { if (isFormValid && !state.isLoading) onEvent(UserEvents.SignIn) }

    Scaffold(
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        ArcBackground()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),

        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Track your drinks, spending and nights out.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            AuthFields(
                email = state.emailInput,
                onEmailChange = { onEvent(UserEvents.OnEmailChange(it)) },
                password = state.passwordInput,
                onPasswordChange = { onEvent(UserEvents.OnPasswordChange(it)) },
                errorMessage = state.errorMessage,
                enabled = !state.isLoading,
                onDone = signIn
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = signIn,
                enabled = isFormValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Sign in")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { onEvent(UserEvents.AnonymousSignIn) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Continue as guest")
            }
        }
    }
}

@Composable
fun GuestLogInScreen(
    state: UserUiState,
    onEvent: (UserEvents) -> Unit,
    onBackClick: () -> Unit,
) {
    val isFormValid = state.emailInput.isNotBlank() && state.passwordInput.isNotEmpty()
    val signIn = { if (isFormValid && !state.isLoading) onEvent(UserEvents.SignIn) }

    Scaffold(
        topBar = { LogInTopBar(onBackClick = onBackClick) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Log in to an existing account. Everything you've logged as a guest moves to that account.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthFields(
                email = state.emailInput,
                onEmailChange = { onEvent(UserEvents.OnEmailChange(it)) },
                password = state.passwordInput,
                onPasswordChange = { onEvent(UserEvents.OnPasswordChange(it)) },
                errorMessage = state.errorMessage,
                enabled = !state.isLoading,
                onDone = signIn
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = signIn,
                enabled = isFormValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Log in")
            }
        }
    }
}
