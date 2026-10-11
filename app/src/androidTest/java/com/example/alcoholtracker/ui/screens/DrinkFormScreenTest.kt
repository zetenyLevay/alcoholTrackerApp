package com.example.alcoholtracker.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormOptions
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormTextStates
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DrinkFormScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val events = mutableListOf<DrinkLogFormEvent>()
    private var backClicks = 0

    private val options = DrinkLogFormOptions(
        categoryOptions = DrinkCategory.entries,
        amountOptions = listOf(DrinkUnit("Can", 500), DrinkUnit("milliliters", 1)),
        recipientOptions = listOf("Me", "Anna"),
    )

    private fun show(isEdit: Boolean = false) {
        rule.setContent {
            MaterialTheme {
                DrinkFormScreen(
                    onBackClick = { backClicks++ },
                    onEvent = { events += it },
                    state = DrinkLogFormUiState(isEdit = isEdit, options = options),
                    textState = DrinkLogFormTextStates()
                )
            }
        }
    }

    @Test
    fun newDrink_titleAndSave() {
        show()
        rule.onNodeWithText("Log drink").assertIsDisplayed()
        rule.onNodeWithText("Add drink", useUnmergedTree = true).performClick()
        assertEquals(DrinkLogFormEvent.OnSaveDrinkLog, events.last())
    }

    @Test
    fun editDrink_titleAndSave() {
        show(isEdit = true)
        rule.onNodeWithText("Edit drink").assertIsDisplayed()
        rule.onNodeWithText("Save changes", useUnmergedTree = true).performClick()
        assertEquals(DrinkLogFormEvent.OnSaveDrinkLog, events.last())
    }

    @Test
    fun opening_doesNotOpenAnyMenu() {
        show()
        rule.onNodeWithText("Anna").assertDoesNotExist()
    }

    @Test
    fun back_callsBack() {
        show()
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backClicks)
    }

    @Test
    fun defaults_areClean() {
        val texts = DrinkLogFormTextStates()
        assertEquals("500", texts.inputAmount.text.toString())
        assertEquals("0", texts.alcoholPercentage.text.toString())
        assertEquals("0", texts.cost.text.toString())
        assertEquals("Me", texts.recipient.text.toString())
    }
}
