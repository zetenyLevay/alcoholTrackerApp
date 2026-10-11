package com.example.alcoholtracker.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.ui.testLog
import com.example.alcoholtracker.ui.viewmodel.SearchEvent
import com.example.alcoholtracker.ui.viewmodel.SearchUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val events = mutableListOf<SearchEvent>()
    private var backClicks = 0

    private val heineken = testLog(1, "Heineken")
    private val guinness = testLog(2, "Guinness", isFavorite = true)
    private val stout = testLog(3, "Stout")

    private val lists = SearchUiState(
        recentLogs = listOf(heineken),
        frequentLogs = listOf(stout),
        favoriteLogs = listOf(guinness),
        isLoading = false
    )

    private fun show(state: SearchUiState) {
        rule.setContent {
            MaterialTheme {
                SearchScreen(onEvent = { events += it }, onBackClick = { backClicks++ }, state = state)
            }
        }
    }

    @Test
    fun blankQuery_showsTabsWithRecentFirst() {
        show(lists)
        rule.onNodeWithText("Recent").assertIsDisplayed()
        rule.onNodeWithText("Frequent").assertIsDisplayed()
        rule.onNodeWithText("Favorites").assertIsDisplayed()
        rule.onNodeWithText("Heineken").assertIsDisplayed()
    }

    @Test
    fun tabs_switchLists() {
        show(lists)
        rule.onNodeWithText("Favorites").performClick()
        rule.onNodeWithText("Guinness").assertIsDisplayed()
        rule.onNodeWithText("Frequent").performClick()
        rule.onNodeWithText("Stout").assertIsDisplayed()
    }

    @Test
    fun loading_hidesLists() {
        show(lists.copy(isLoading = true))
        rule.onNodeWithText("Heineken").assertDoesNotExist()
    }

    @Test
    fun typing_sendsQueryChange() {
        show(lists)
        rule.onNode(hasSetTextAction()).performTextInput("hei")
        assertTrue(events.contains(SearchEvent.OnQueryChange("hei")))
    }

    @Test
    fun query_showsResultsInsteadOfTabs() {
        val drink = Drink(name = "Heineken Silver", alcoholContent = 4.0, category = "Beer")
        show(lists.copy(query = "hei", searchResults = listOf(drink)))

        rule.onNodeWithText("Heineken Silver").assertIsDisplayed()
        rule.onNodeWithText("Beer · 4% ABV").assertIsDisplayed()
        rule.onNodeWithText("Frequent").assertDoesNotExist()

        rule.onNodeWithText("Heineken Silver").performClick()
        assertEquals(SearchEvent.OnDrinkClick(drink), events.last())
    }

    @Test
    fun query_withoutResults_showsEmptyMessage() {
        show(lists.copy(query = "zzz", searchResults = emptyList(), isSearching = false))
        rule.onNodeWithText("No drinks found").assertIsDisplayed()
    }

    @Test
    fun query_whileSearching_doesNotClaimNoResults() {
        show(lists.copy(query = "zzz", searchResults = emptyList(), isSearching = true))
        rule.onNodeWithText("No drinks found").assertDoesNotExist()
    }

    @Test
    fun rowActions_sendEvents() {
        show(lists)
        rule.onNodeWithText("Heineken").performClick()
        rule.onNodeWithContentDescription("Add to favorites").performClick()
        rule.onNodeWithContentDescription("Log again").performClick()

        assertEquals(
            listOf(
                SearchEvent.OnLogClick(heineken),
                SearchEvent.OnFavoriteToggle(heineken),
                SearchEvent.OnLogAgainClick(heineken),
            ),
            events
        )
    }

    @Test
    fun back_callsBack() {
        show(lists)
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backClicks)
    }
}
