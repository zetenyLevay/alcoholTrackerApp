package com.example.alcoholtracker.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.alcoholtracker.data.model.Account
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private var createClicks = 0
    private var logInClicks = 0
    private var logOutClicks = 0

    private fun show(account: Account?) {
        rule.setContent {
            MaterialTheme {
                ProfileScreen(
                    account = account,
                    onCreateAccountClick = { createClicks++ },
                    onLogInClick = { logInClicks++ },
                    onLogout = { logOutClicks++ }
                )
            }
        }
    }

    @Test
    fun guest_canCreateAccountOrLogIn() {
        show(Account(isGuest = true, email = null))
        rule.onNodeWithText("You're using a guest account").assertIsDisplayed()

        rule.onNodeWithText("Create account").performClick()
        rule.onNodeWithText("Log in").performClick()

        assertEquals(1, createClicks)
        assertEquals(1, logInClicks)
        rule.onNodeWithText("Log out").assertDoesNotExist()
    }

    @Test
    fun signedIn_showsEmailAndLogOut() {
        show(Account(isGuest = false, email = "me@example.com"))
        rule.onNodeWithText("me@example.com").assertIsDisplayed()

        rule.onNodeWithText("Log out").performClick()

        assertEquals(1, logOutClicks)
        rule.onNodeWithText("Log in").assertDoesNotExist()
        rule.onNodeWithText("Create account").assertDoesNotExist()
    }

    @Test
    fun noAccount_showsNoActions() {
        show(null)
        rule.onNodeWithText("Log in").assertDoesNotExist()
        rule.onNodeWithText("Log out").assertDoesNotExist()
        rule.onNodeWithText("Create account").assertDoesNotExist()
    }
}
