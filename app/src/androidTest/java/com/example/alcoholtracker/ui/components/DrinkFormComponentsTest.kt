package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.components.logComponents.ABVAndPriceTextFields
import com.example.alcoholtracker.ui.components.logComponents.AmountDropDown
import com.example.alcoholtracker.ui.components.logComponents.DrinkAutoComplete
import com.example.alcoholtracker.ui.components.logComponents.RecipientAutoComplete
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrinkFormComponentsTest {

    @get:Rule
    val rule = createComposeRule()

    private val ml = DrinkUnit("milliliters", 1)
    private val can = DrinkUnit("Can", 500)
    private val smallBottle = DrinkUnit("Small bottle", 300)

    private val abv = TextFieldState("0")
    private val price = TextFieldState("0")
    private val amount = TextFieldState("500")

    private fun showAbvAndPrice() = rule.setContent {
        MaterialTheme { ABVAndPriceTextFields(abv = abv, price = price) }
    }

    private fun showAmount(unit: DrinkUnit, onSelected: (DrinkUnit) -> Unit = {}) = rule.setContent {
        MaterialTheme {
            AmountDropDown(amount = amount, selectedUnit = unit, onSelected = onSelected, options = listOf(can, smallBottle, ml))
        }
    }

    @Test
    fun abv_commaBecomesDot() {
        showAbvAndPrice()
        rule.onAllNodes(hasSetTextAction())[0].performTextReplacement("4,6")
        assertEquals("4.6", abv.text.toString())
    }

    @Test
    fun abv_aboveHundredIsRejected() {
        showAbvAndPrice()
        rule.onAllNodes(hasSetTextAction())[0].performTextReplacement("150")
        assertEquals("0", abv.text.toString())
    }

    @Test
    fun price_typingReplacesLeadingZero() {
        showAbvAndPrice()
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("5")
        assertEquals("5", price.text.toString())
    }

    @Test
    fun price_tooManyDecimalsIsRejected() {
        showAbvAndPrice()
        rule.onAllNodes(hasSetTextAction())[1].performTextReplacement("3.333")
        assertEquals("0", price.text.toString())
    }

    @Test
    fun arrows_stepAndNeverGoNegative() {
        abv.edit { replace(0, length, "4.6") }
        showAbvAndPrice()

        rule.onAllNodesWithContentDescription("Increase")[0].performClick()
        assertEquals("4.7", abv.text.toString())

        rule.onAllNodesWithContentDescription("Decrease")[1].performClick()
        assertEquals("0", price.text.toString())

        rule.onAllNodesWithContentDescription("Increase")[1].performClick()
        rule.onAllNodesWithContentDescription("Increase")[1].performClick()
        rule.onAllNodesWithContentDescription("Increase")[1].performClick()
        assertEquals("0.3", price.text.toString())
    }

    @Test
    fun amountInMilliliters_isWholeNumbers() {
        showAmount(ml)
        rule.onNode(hasSetTextAction() and hasText("500")).performTextReplacement("2.5")
        assertEquals("500", amount.text.toString())
    }

    @Test
    fun amountInContainers_allowsDecimalsWithComma() {
        amount.edit { replace(0, length, "1") }
        showAmount(can)
        rule.onNode(hasSetTextAction() and hasText("1")).performTextReplacement("2,5")
        assertEquals("2.5", amount.text.toString())
    }

    @Test
    fun amountArrows_stepByOne() {
        showAmount(ml)
        rule.onAllNodesWithContentDescription("Increase")[0].performClick()
        assertEquals("501", amount.text.toString())
        rule.onAllNodesWithContentDescription("Decrease")[0].performClick()
        rule.onAllNodesWithContentDescription("Decrease")[0].performClick()
        assertEquals("499", amount.text.toString())
    }

    @Test
    fun amountDecrease_onEmptyFieldDoesNotCrash() {
        amount.edit { replace(0, length, "") }
        showAmount(ml)
        rule.onAllNodesWithContentDescription("Decrease")[0].performClick()
        assertEquals("0", amount.text.toString())
    }

    @Test
    fun unitMenu_showsSizesExceptForMilliliters() {
        val selected = mutableListOf<DrinkUnit>()
        showAmount(ml, onSelected = { selected += it })

        rule.onNodeWithText("milliliters").performClick()
        rule.onNodeWithText("Small bottle").assertIsDisplayed()
        rule.onNodeWithText("300 ml").assertIsDisplayed()
        rule.onNodeWithText("500 ml").assertIsDisplayed()
        rule.onNodeWithText("1 ml").assertDoesNotExist()

        rule.onNodeWithText("Small bottle").performClick()
        assertEquals(listOf(smallBottle), selected)
    }

    @Test
    fun recipientMenu_staysClosedForDefaultValue() {
        rule.setContent {
            MaterialTheme {
                RecipientAutoComplete(recipientOptions = listOf("Me", "Anna"), recipient = TextFieldState("Me"), onRecipientChange = {})
            }
        }
        rule.onNodeWithText("Anna").assertDoesNotExist()
    }

    @Test
    fun drinkMenu_staysClosedForLoadedNameButReportsIt() {
        val typed = mutableListOf<String>()
        rule.setContent {
            MaterialTheme {
                DrinkAutoComplete(
                    drinkName = TextFieldState("Heineken"),
                    options = listOf(Drink(name = "Heineken Silver", alcoholContent = 4.0, category = "Beer")),
                    onTyped = { typed += it },
                    onSelected = {}
                )
            }
        }
        rule.onNodeWithText("Heineken Silver").assertDoesNotExist()
        assertTrue(typed.contains("Heineken"))
    }

}
