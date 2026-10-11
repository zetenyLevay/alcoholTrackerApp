package com.example.alcoholtracker.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.alcoholtracker.R
import com.example.alcoholtracker.ui.testLog
import com.example.alcoholtracker.ui.viewmodel.DetailedLogEvent
import com.example.alcoholtracker.ui.viewmodel.DetailedLogUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DetailedLogScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val events = mutableListOf<DetailedLogEvent>()
    private var backClicks = 0
    private val log = testLog(logId = 42, name = "Heineken")

    private fun show(state: DetailedLogUiState) {
        rule.setContent {
            MaterialTheme {
                DetailedLogScreen(
                    src = R.drawable.beer_icon,
                    onBackClick = { backClicks++ },
                    onEvent = { events += it },
                    state = state
                )
            }
        }
    }

    @Test
    fun showsDrink() {
        show(DetailedLogUiState(drinkLog = log))
        rule.onNodeWithText("Drink details").assertIsDisplayed()
        rule.onNodeWithContentDescription("A glass of beer").assertExists()
    }

    @Test
    fun favorite_sendsEvent() {
        show(DetailedLogUiState(drinkLog = log))
        rule.onNodeWithContentDescription("Add to favorites").performClick()
        assertEquals(listOf<DetailedLogEvent>(DetailedLogEvent.OnFavoriteClick(log)), events)
    }

    @Test
    fun favorite_showsRemoveWhenFavorite() {
        show(DetailedLogUiState(drinkLog = log.copy(isFavorite = true)))
        rule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    @Test
    fun menu_edit_sendsEditWithId() {
        show(DetailedLogUiState(drinkLog = log))
        rule.onNodeWithContentDescription("More options").performClick()
        rule.onNodeWithText("Edit drink").performClick()
        assertEquals(listOf<DetailedLogEvent>(DetailedLogEvent.OnEditClick(42)), events)
    }

    @Test
    fun menu_delete_sendsRemove() {
        show(DetailedLogUiState(drinkLog = log))
        rule.onNodeWithContentDescription("More options").performClick()
        rule.onNodeWithText("Delete drink").performClick()
        assertEquals(listOf<DetailedLogEvent>(DetailedLogEvent.OnRemoveClick(log)), events)
    }

    @Test
    fun back_callsBack() {
        show(DetailedLogUiState(drinkLog = log))
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backClicks)
    }

    @Test
    fun noDrink_rendersNothing() {
        show(DetailedLogUiState(drinkLog = null))
        rule.onNodeWithContentDescription("More options").assertDoesNotExist()
    }
}
