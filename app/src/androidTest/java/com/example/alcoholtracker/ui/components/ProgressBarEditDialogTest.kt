package com.example.alcoholtracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarEditDialog
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProgressBarEditDialogTest {

    @get:Rule
    val rule = createComposeRule()

    private val saved = mutableListOf<Pair<ProgressBarType, Double>>()
    private var dismissed = 0

    private fun show(
        type: ProgressBarType = ProgressBarType.MONEY,
        targets: Map<ProgressBarType, Double> = mapOf(
            ProgressBarType.MONEY to 40.0,
            ProgressBarType.COUNT to 5.0,
            ProgressBarType.AMOUNT to 1500.0,
        ),
    ) {
        rule.setContent {
            MaterialTheme {
                ProgressBarEditDialog(
                    currentType = type,
                    currentTargets = targets,
                    onDismiss = { dismissed++ },
                    onEvent = { t, v -> saved += t to v }
                )
            }
        }
    }

    private fun goalField() = rule.onNode(hasSetTextAction())

    @Test
    fun opensWithCurrentGoal() {
        show()
        goalField().assertTextEquals("40")
    }

    @Test
    fun opensOnCurrentType() {
        show(type = ProgressBarType.AMOUNT)
        goalField().assertTextEquals("1500")
    }

    @Test
    fun switchingType_loadsThatGoal() {
        show()
        rule.onNodeWithText("Drinks").performClick()
        goalField().assertTextEquals("5")
    }

    @Test
    fun save_reportsTypeAndValueAndDismisses() {
        show()
        rule.onNodeWithText("Save goal").performClick()
        assertEquals(listOf(ProgressBarType.MONEY to 40.0), saved)
        assertEquals(1, dismissed)
    }

    @Test
    fun editedValue_isSaved() {
        show()
        goalField().performTextReplacement("62.5")
        rule.onNodeWithText("Save goal").performClick()
        assertEquals(listOf(ProgressBarType.MONEY to 62.5), saved)
    }

    @Test
    fun presetChip_setsValue() {
        show()
        rule.onNodeWithText("€20").performClick()
        goalField().assertTextEquals("20")
        rule.onNodeWithText("Save goal").performClick()
        assertEquals(listOf(ProgressBarType.MONEY to 20.0), saved)
    }

    @Test
    fun presets_matchSelectedType() {
        show()
        rule.onNodeWithText("Volume").performClick()
        rule.onNodeWithText("1000 ml").assertExists()
        rule.onNodeWithText("€20").assertDoesNotExist()
    }

    @Test
    fun emptyOrZero_cannotBeSaved() {
        show()
        goalField().performTextClearance()
        rule.onNodeWithText("Save goal").assertIsNotEnabled()
        goalField().performTextReplacement("0")
        rule.onNodeWithText("Save goal").assertIsNotEnabled()
        goalField().performTextReplacement("3")
        rule.onNodeWithText("Save goal").assertIsEnabled()
    }

    @Test
    fun missingGoal_startsEmpty() {
        show(targets = mapOf(ProgressBarType.MONEY to 40.0))
        rule.onNodeWithText("Drinks").performClick()
        goalField().assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
        rule.onNodeWithText("Save goal").assertIsNotEnabled()
    }

    @Test
    fun drinkCount_rejectsDecimals() {
        show(type = ProgressBarType.COUNT)
        goalField().performTextReplacement("2.5")
        goalField().assertTextEquals("5")
    }

    @Test
    fun money_rejectsMoreThanTwoDecimalsAndLetters() {
        show()
        goalField().performTextReplacement("1.234")
        goalField().assertTextEquals("40")
        goalField().performTextReplacement("abc")
        goalField().assertTextEquals("40")
    }

    @Test
    fun cancel_dismissesWithoutSaving() {
        show()
        rule.onNodeWithText("Cancel").performClick()
        assertEquals(1, dismissed)
        assertTrue(saved.isEmpty())
    }
}
