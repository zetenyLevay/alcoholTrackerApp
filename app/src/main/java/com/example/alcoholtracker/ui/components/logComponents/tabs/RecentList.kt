package com.example.alcoholtracker.ui.components.logComponents.tabs

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.example.alcoholtracker.data.model.DrinkLog

@Composable
fun RecentList(
    drinks: List<DrinkLog>,
    onLogClick: (DrinkLog) -> Unit,
    onLogAgainClick: (DrinkLog) -> Unit,
    onFavoriteToggle: (DrinkLog) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    LogTabList(
        logs = drinks,
        emptyText = "No recent drinks found",
        onLogClick = onLogClick,
        onLogAgainClick = onLogAgainClick,
        onFavoriteToggle = onFavoriteToggle,
        listState = listState
    )
}
