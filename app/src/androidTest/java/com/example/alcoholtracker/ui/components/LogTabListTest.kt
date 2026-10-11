package com.example.alcoholtracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.ui.components.logComponents.tabs.LogTabList
import com.example.alcoholtracker.ui.testLog
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LogTabListTest {

    @get:Rule
    val rule = createComposeRule()

    private val clicked = mutableListOf<DrinkLog>()
    private val loggedAgain = mutableListOf<DrinkLog>()
    private val toggled = mutableListOf<DrinkLog>()

    private fun show(logs: List<DrinkLog>) {
        rule.setContent {
            MaterialTheme {
                LogTabList(
                    logs = logs,
                    emptyText = "No favorite drinks found",
                    onLogClick = { clicked += it },
                    onLogAgainClick = { loggedAgain += it },
                    onFavoriteToggle = { toggled += it },
                )
            }
        }
    }

    @Test
    fun showsEmptyTextWhenEmpty() {
        show(emptyList())
        rule.onNodeWithText("No favorite drinks found").assertIsDisplayed()
    }

    @Test
    fun showsRowsAndNoEmptyText() {
        show(listOf(testLog(1, "Heineken"), testLog(2, "Guinness")))
        rule.onNodeWithText("Heineken").assertIsDisplayed()
        rule.onNodeWithText("Guinness").assertIsDisplayed()
        rule.onNodeWithText("No favorite drinks found").assertDoesNotExist()
    }

    @Test
    fun rowClick_reportsLog() {
        val log = testLog()
        show(listOf(log))
        rule.onNodeWithText("Heineken").performClick()
        assertEquals(listOf(log), clicked)
    }

    @Test
    fun favoriteButton_reflectsStateAndReportsLog() {
        val log = testLog(isFavorite = false)
        show(listOf(log))
        rule.onNodeWithContentDescription("Add to favorites").performClick()
        assertEquals(listOf(log), toggled)
    }

    @Test
    fun favoriteButton_showsRemoveForFavorites() {
        show(listOf(testLog(isFavorite = true)))
        rule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
        rule.onNodeWithContentDescription("Add to favorites").assertDoesNotExist()
    }

    @Test
    fun logAgain_reportsLogAndBrieflyConfirms() {
        val log = testLog()
        show(listOf(log))
        rule.mainClock.autoAdvance = false

        rule.onNodeWithContentDescription("Log again").performClick()
        rule.mainClock.advanceTimeBy(300)
        assertEquals(listOf(log), loggedAgain)
        rule.onNodeWithContentDescription("Logged").assertIsDisplayed()

        rule.mainClock.advanceTimeBy(1_500)
        rule.onNodeWithContentDescription("Log again").assertIsDisplayed()
    }
}
