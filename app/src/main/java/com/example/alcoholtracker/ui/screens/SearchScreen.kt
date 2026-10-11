package com.example.alcoholtracker.ui.screens

import com.example.alcoholtracker.SnackbarType
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarAction
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.domain.model.drinkCategoryFromLabel
import com.example.alcoholtracker.ui.components.LogDrinkTopBar
import com.example.alcoholtracker.ui.components.headerColor
import com.example.alcoholtracker.ui.components.logComponents.tabs.FavoritesList
import com.example.alcoholtracker.ui.components.logComponents.tabs.FrequentList
import com.example.alcoholtracker.ui.components.logComponents.tabs.RecentList
import com.example.alcoholtracker.ui.components.logComponents.tabs.SearchComponent
import com.example.alcoholtracker.ui.navigation.SearchTabs
import com.example.alcoholtracker.ui.viewmodel.SearchEffect
import com.example.alcoholtracker.ui.viewmodel.SearchEvent
import com.example.alcoholtracker.ui.viewmodel.SearchEvent.ConsumeEffect
import com.example.alcoholtracker.ui.viewmodel.SearchEvent.OnDrinkClick
import com.example.alcoholtracker.ui.viewmodel.SearchEvent.OnFavoriteToggle
import com.example.alcoholtracker.ui.viewmodel.SearchEvent.OnLogAgainClick
import com.example.alcoholtracker.ui.viewmodel.SearchEvent.OnLogClick
import com.example.alcoholtracker.ui.viewmodel.SearchEvent.OnQueryChange
import com.example.alcoholtracker.ui.viewmodel.SearchUiState
import com.example.alcoholtracker.ui.viewmodel.SearchViewModel
import com.example.alcoholtracker.utils.formatAbv
import com.example.compose.AlcoholTrackerTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onNavigateToDrinkForm: (copyFromLogId: Int) -> Unit,
    onNavigateToDrinkFormWithDrink: (name: String, category: DrinkCategory) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {

    val state = viewModel.searchUiState.collectAsState()

    LaunchedEffect(state.value.effect) {
        when (val effect = state.value.effect) {
            is SearchEffect.ShowError -> {
                viewModel.processEvent(ConsumeEffect)
                SnackbarController.sendEvent(
                    event = SnackBarEvent(message = effect.message, type = SnackbarType.ERROR)
                )
            }

            is SearchEffect.ShowDrinkLogged -> {
                viewModel.processEvent(ConsumeEffect)
                SnackbarController.sendEvent(
                    event = SnackBarEvent(
                        message = "${effect.log.name} logged",
                        type = SnackbarType.SUCCESS,
                        action = SnackbarAction(
                            name = "Undo",
                            action = { viewModel.processEvent(SearchEvent.OnUndoLogAgain(effect.log)) }
                        )
                    )
                )
            }

            is SearchEffect.NavigateToDrinkForm -> {
                viewModel.processEvent(ConsumeEffect)
                onNavigateToDrinkForm(effect.copyFromLogId)
            }

            is SearchEffect.NavigateToDrinkFormWithDrink -> {
                viewModel.processEvent(ConsumeEffect)
                onNavigateToDrinkFormWithDrink(effect.name, effect.category)
            }

            null -> {

            }
        }
    }

    SearchScreen(
        onEvent = viewModel::processEvent,
        onBackClick = onBackClick,
        state = state.value
    )
}

@Composable
fun SearchScreen(
    onEvent: (SearchEvent) -> Unit,
    onBackClick: () -> Unit,
    state: SearchUiState,
) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { SearchTabs.entries.size })
    val tabListStates = SearchTabs.entries.associateWith { rememberLazyListState() }
    val resultsListState = rememberLazyListState()

    val isShowingResults = state.query.isNotBlank()
    val isScrolled by remember(isShowingResults) {
        derivedStateOf {
            val visibleList = if (isShowingResults) {
                resultsListState
            } else {
                tabListStates.getValue(SearchTabs.entries[pagerState.currentPage])
            }
            visibleList.canScrollBackward
        }
    }
    val headerColor = headerColor(isScrolled)

    Scaffold(
        topBar = {
            LogDrinkTopBar(
                onBackClick = { onBackClick() },
                isEdit = false,
                isScrolled = isScrolled
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            SearchComponent(
                query = state.query,
                onCameraClick = {},
                onQueryChange = { onEvent(OnQueryChange(it)) },
                modifier = Modifier.background(headerColor)
            )

            if (isShowingResults) {
                SearchResults(
                    results = state.searchResults,
                    isSearching = state.isSearching,
                    listState = resultsListState,
                    onDrinkClick = { onEvent(OnDrinkClick(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = headerColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SearchTabs.entries.forEachIndexed { index, currentTab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentTab.ordinal)
                                }
                            },
                            text = { Text(currentTab.title) }
                        )
                    }
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { page ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            val onLogClick: (DrinkLog) -> Unit = { onEvent(OnLogClick(it)) }
                            val onLogAgainClick: (DrinkLog) -> Unit = { onEvent(OnLogAgainClick(it)) }
                            val onFavoriteToggle: (DrinkLog) -> Unit = { onEvent(OnFavoriteToggle(it)) }

                            when (SearchTabs.entries[page]) {
                                SearchTabs.Recent -> RecentList(
                                    drinks = state.recentLogs,
                                    onLogClick = onLogClick,
                                    onLogAgainClick = onLogAgainClick,
                                    onFavoriteToggle = onFavoriteToggle,
                                    listState = tabListStates.getValue(SearchTabs.Recent)
                                )

                                SearchTabs.Frequent -> FrequentList(
                                    drinks = state.frequentLogs,
                                    onLogClick = onLogClick,
                                    onLogAgainClick = onLogAgainClick,
                                    onFavoriteToggle = onFavoriteToggle,
                                    listState = tabListStates.getValue(SearchTabs.Frequent)
                                )

                                SearchTabs.Favorites -> FavoritesList(
                                    drinks = state.favoriteLogs,
                                    onLogClick = onLogClick,
                                    onLogAgainClick = onLogAgainClick,
                                    onFavoriteToggle = onFavoriteToggle,
                                    listState = tabListStates.getValue(SearchTabs.Favorites)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    results: List<Drink>,
    isSearching: Boolean,
    listState: LazyListState,
    onDrinkClick: (Drink) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when {
            isSearching && results.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            results.isEmpty() -> {
                Text(
                    "No drinks found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }

            else -> {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(results, key = { "${it.name}|${it.category}" }) { drink ->
                        SearchResultItem(
                            drink = drink,
                            onClick = { onDrinkClick(drink) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    drink: Drink,
    onClick: () -> Unit,
) {
    val category = drinkCategoryFromLabel(drink.category)

    ListItem(
        headlineContent = { Text(drink.name) },
        supportingContent = { Text("${category.nameString} · ${formatAbv(drink.alcoholContent)}") },
        leadingContent = {
            Icon(
                painterResource(category.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    val sampleLog = DrinkLog(
        logId = 1,
        drinkId = null,
        userId = "preview",
        name = "Heineken",
        cost = 6.2,
        alcoholPercentage = 5.0,
        amount = 500,
        category = DrinkCategory.BEER,
        recipient = null,
        inputAmount = 1.0,
        isFavorite = true,
        imgURI = null,
        notes = null,
        locationName = null,
        longitude = null,
        latitude = null,
        drinkUnit = DrinkUnit("Can (500 ml)", 500),
        date = LocalDateTime.now()
    )

    AlcoholTrackerTheme {
        SearchScreen(
            onEvent = {},
            onBackClick = {},
            state = SearchUiState(
                recentLogs = listOf(sampleLog, sampleLog.copy(logId = 2, name = "Guinness", isFavorite = false)),
                isLoading = false
            )
        )
    }
}
