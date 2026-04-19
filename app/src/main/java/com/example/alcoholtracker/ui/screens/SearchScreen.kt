package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.LogDrinkTopBar
import com.example.alcoholtracker.ui.components.logComponents.tabs.FavoritesList
import com.example.alcoholtracker.ui.components.logComponents.tabs.FrequentList
import com.example.alcoholtracker.ui.components.logComponents.tabs.RecentList
import com.example.alcoholtracker.ui.components.logComponents.tabs.SearchComponent
import com.example.alcoholtracker.ui.navigation.SearchTabs
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: DrinkLogFormViewModel = hiltViewModel(),

    ) {


    val recentLogs = viewModel.recentDrinks.collectAsState().value
    val frequentLogs = viewModel.frequentLogs.collectAsState().value
    val favoriteLogs = viewModel.favoriteLogs.collectAsState().value

    SearchScreen(
        recentLogs = recentLogs,
        frequentLogs = frequentLogs,
        favoriteLogs = favoriteLogs,
        onBackClick = onBackClick,
        onTyped = { viewModel.updateSearchQuery(it) }
    )

}

@Composable
fun SearchScreen(
    recentLogs: List<UserDrinkLog>,
    frequentLogs: List<UserDrinkLog>,
    favoriteLogs: List<UserDrinkLog>,
    onBackClick: () -> Unit,
    onTyped: (String) -> Unit,


    ){

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { SearchTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        topBar = {
            LogDrinkTopBar(
                onBackClick = { onBackClick() },
                isEdit = false
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),

        ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            SearchComponent(
                onCameraClick = {},
                onTyped = { onTyped(it)}
            )

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth()
            )
            {
                SearchTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(currentTab.title) }

                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (page) {
                        0 -> RecentList(recentLogs)
                        1 -> FrequentList(frequentLogs)
                        2 -> FavoritesList(favoriteLogs)
                    }
                }
            }
        }
    }
}