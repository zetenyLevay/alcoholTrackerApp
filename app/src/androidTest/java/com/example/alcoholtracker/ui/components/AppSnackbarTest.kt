package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.alcoholtracker.SnackbarType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AppSnackbarTest {

    @get:Rule
    val rule = createComposeRule()

    private class FakeSnackbarData(override val visuals: SnackbarVisuals) : SnackbarData {
        var actions = 0
        var dismissals = 0
        override fun performAction() { actions++ }
        override fun dismiss() { dismissals++ }
    }

    @Test
    fun showsMessageAndAction_forEveryType() {
        rule.setContent {
            MaterialTheme {
                Column {
                    SnackbarType.entries.forEach { type ->
                        AppSnackbar(FakeSnackbarData(AppSnackbarVisuals("Message $type", "Undo", type)))
                    }
                }
            }
        }
        SnackbarType.entries.forEach { rule.onNodeWithText("Message $it").assertIsDisplayed() }
        rule.onAllNodesWithText("Undo").assertCountEquals(SnackbarType.entries.size)
    }

    @Test
    fun actionClick_performsAction() {
        val data = FakeSnackbarData(AppSnackbarVisuals("Heineken logged", "Undo", SnackbarType.SUCCESS))
        rule.setContent { MaterialTheme { AppSnackbar(data) } }
        rule.onNodeWithText("Undo").performClick()
        assertEquals(1, data.actions)
    }

    @Test
    fun noActionLabel_showsNoButton() {
        val data = FakeSnackbarData(AppSnackbarVisuals("Drink saved", null, SnackbarType.SUCCESS))
        rule.setContent { MaterialTheme { AppSnackbar(data) } }
        rule.onNodeWithText("Drink saved").assertIsDisplayed()
        rule.onNodeWithText("Undo").assertDoesNotExist()
    }

    @Test
    fun plainVisuals_fallBackToInfoStyle() {
        val visuals = object : SnackbarVisuals {
            override val message = "Plain"
            override val actionLabel: String? = null
            override val withDismissAction = false
            override val duration = SnackbarDuration.Short
        }
        rule.setContent { MaterialTheme { AppSnackbar(FakeSnackbarData(visuals)) } }
        rule.onNodeWithText("Plain").assertIsDisplayed()
    }

    @Test
    fun visuals_areIndefiniteWithoutDismissButton() {
        val visuals = AppSnackbarVisuals("m", null, SnackbarType.INFO)
        assertEquals(SnackbarDuration.Indefinite, visuals.duration)
        assertEquals(false, visuals.withDismissAction)
    }
}
