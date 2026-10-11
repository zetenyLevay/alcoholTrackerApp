package com.example.alcoholtracker.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import com.example.alcoholtracker.ui.testLog
import com.example.alcoholtracker.ui.viewmodel.HomeEvent
import com.example.alcoholtracker.ui.viewmodel.HomeUiState
import com.example.alcoholtracker.utils.formatCost
import com.example.alcoholtracker.utils.formatDrinkCount
import com.example.alcoholtracker.utils.formatVolume
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val events = mutableListOf<HomeEvent>()

    private val targets = mapOf(
        ProgressBarType.MONEY to 40.0,
        ProgressBarType.COUNT to 5.0,
        ProgressBarType.AMOUNT to 1500.0,
    )

    private val tonight = HomeUiState(
        drinkCount = 2,
        progressBarType = ProgressBarType.MONEY,
        currentMoneySpent = 12.5,
        currentAmountMl = 1000,
        targets = targets,
        tonightDrinkLogs = listOf(testLog(1, "Heineken"), testLog(2, "Guinness")),
    )

    private fun show(state: HomeUiState) {
        rule.setContent { MaterialTheme { HomeScreen(onEvent = { events += it }, state = state) } }
    }

    @Test
    fun moneyGoal_showsSpentOfTarget() {
        show(tonight)
        rule.onNodeWithText("${formatCost(12.5)} / ${formatCost(40.0)}").assertIsDisplayed()
        rule.onNodeWithText("${formatDrinkCount(2)} · ${formatVolume(1000)}").assertIsDisplayed()
    }

    @Test
    fun countGoal_showsDrinksOfTarget() {
        show(tonight.copy(progressBarType = ProgressBarType.COUNT))
        rule.onNodeWithText("2 / 5 drinks").assertIsDisplayed()
    }

    @Test
    fun amountGoal_showsVolumeOfTarget() {
        show(tonight.copy(progressBarType = ProgressBarType.AMOUNT))
        rule.onNodeWithText("${formatVolume(1000)} / ${formatVolume(1500)}").assertIsDisplayed()
    }

    @Test
    fun listsTonightsDrinks() {
        show(tonight)
        rule.onNodeWithText("Heineken").assertIsDisplayed()
        rule.onNodeWithText("Guinness").assertIsDisplayed()
    }

    @Test
    fun emptyNight_showsEmptyText() {
        show(tonight.copy(drinkCount = 0, tonightDrinkLogs = emptyList()))
        rule.onNodeWithText("No drinks logged tonight").assertIsDisplayed()
    }

    @Test
    fun itemClick_opensDetails() {
        show(tonight)
        rule.onNodeWithText("Guinness").performClick()
        assertEquals(listOf<HomeEvent>(HomeEvent.OnItemClick(2)), events)
    }

    @Test
    fun fab_sendsEvent() {
        show(tonight)
        rule.onNodeWithContentDescription("Log drink").performClick()
        assertEquals(listOf<HomeEvent>(HomeEvent.OnFABClick), events)
    }

    @Test
    fun editGoal_opensDialogAndSavesUpdate() {
        show(tonight)
        rule.onNodeWithContentDescription("Edit goal").performClick()
        rule.onNodeWithText("Tonight's goal").assertIsDisplayed()
        rule.onNodeWithText("Save goal").performClick()

        assertTrue(events.contains(HomeEvent.OnProgressBarUpdate(ProgressBarType.MONEY, 40.0)))
        rule.onNodeWithText("Tonight's goal").assertDoesNotExist()
    }
}
