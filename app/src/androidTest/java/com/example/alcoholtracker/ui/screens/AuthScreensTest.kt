package com.example.alcoholtracker.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.alcoholtracker.ui.viewmodel.UserEvents
import com.example.alcoholtracker.ui.viewmodel.UserUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthScreensTest {

    @get:Rule
    val rule = createComposeRule()

    private val events = mutableListOf<UserEvents>()
    private var backClicks = 0

    private val filled = UserUiState(emailInput = "me@example.com", passwordInput = "secret1")

    private fun signIn(state: UserUiState) = rule.setContent {
        MaterialTheme { SignInScreen(state = state, onEvent = { events += it }) }
    }

    private fun guestLogIn(state: UserUiState) = rule.setContent {
        MaterialTheme { GuestLogInScreen(state = state, onEvent = { events += it }, onBackClick = { backClicks++ }) }
    }

    private fun signUp(state: UserUiState) = rule.setContent {
        MaterialTheme { SignUpScreen(state = state, onEvent = { events += it }, onBackClick = { backClicks++ }) }
    }

    private fun logInButton() = rule.onNode(hasText("Log in") and hasClickAction())

    @Test
    fun signIn_disabledUntilEmailAndPassword() {
        signIn(UserUiState(emailInput = "me@example.com"))
        rule.onNodeWithText("Sign in").assertIsNotEnabled()
    }

    @Test
    fun signIn_sendsSignIn() {
        signIn(filled)
        rule.onNodeWithText("Sign in").assertIsEnabled().performClick()
        assertEquals(listOf<UserEvents>(UserEvents.SignIn), events)
    }

    @Test
    fun signIn_canContinueAsGuest() {
        signIn(UserUiState())
        rule.onNodeWithText("Continue as guest").performClick()
        assertEquals(listOf<UserEvents>(UserEvents.AnonymousSignIn), events)
    }

    @Test
    fun signIn_loadingDisablesButtons() {
        signIn(filled.copy(isLoading = true))
        rule.onNodeWithText("Sign in").assertIsNotEnabled()
        rule.onNodeWithText("Continue as guest").assertIsNotEnabled()
    }

    @Test
    fun signIn_showsErrorMessage() {
        signIn(filled.copy(errorMessage = "Wrong email or password"))
        rule.onNodeWithText("Wrong email or password").assertIsDisplayed()
    }

    @Test
    fun signIn_typingSendsChanges() {
        signIn(UserUiState())
        rule.onNodeWithText("Email").performTextInput("a@b.c")
        rule.onNodeWithText("Password").performTextInput("pw")
        assertTrue(events.contains(UserEvents.OnEmailChange("a@b.c")))
        assertTrue(events.contains(UserEvents.OnPasswordChange("pw")))
    }

    @Test
    fun guestLogIn_hasNoGuestOptionAndCanGoBack() {
        guestLogIn(UserUiState())
        rule.onNodeWithText("Continue as guest").assertDoesNotExist()
        rule.onNodeWithText("Everything you've logged as a guest", substring = true).assertIsDisplayed()
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backClicks)
    }

    @Test
    fun guestLogIn_disabledWhenEmpty() {
        guestLogIn(UserUiState())
        logInButton().assertIsNotEnabled()
    }

    @Test
    fun guestLogIn_submits() {
        guestLogIn(filled)
        logInButton().assertIsEnabled().performClick()
        assertEquals(listOf<UserEvents>(UserEvents.SignIn), events)
    }

    @Test
    fun signUp_requiresSixCharacterPassword() {
        signUp(filled.copy(passwordInput = "12345"))
        rule.onNode(hasText("Create account") and hasClickAction()).assertIsNotEnabled()
    }

    @Test
    fun signUp_submits() {
        signUp(filled)
        rule.onNode(hasText("Create account") and hasClickAction()).performClick()
        assertEquals(listOf<UserEvents>(UserEvents.SignUp), events)
    }

    @Test
    fun passwordVisibility_toggles() {
        signIn(filled)
        rule.onNodeWithContentDescription("Show password").performClick()
        rule.onNodeWithContentDescription("Hide password").assertIsDisplayed()
    }
}
