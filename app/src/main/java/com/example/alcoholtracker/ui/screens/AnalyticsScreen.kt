package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.ui.components.AnalyticsTopBar
import com.example.alcoholtracker.ui.components.analytics.FinanceCharts
import com.example.alcoholtracker.ui.components.analytics.HealthCharts
import com.example.alcoholtracker.ui.components.analytics.OverviewCharts
import com.example.alcoholtracker.ui.navigation.AnalyticsTabs
import com.example.alcoholtracker.ui.viewmodel.DrinkFormViewModel
import kotlinx.coroutines.launch

@Composable
fun AnalyticsScreen(
    // onCardClick: (String, List<ChartEntry>, @Composable (Modifier?, List<ChartEntry>) -> Unit) -> Unit,
    userDrinkFormViewModel: DrinkFormViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { AnalyticsTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        topBar = { AnalyticsTopBar() },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())

        ) {

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnalyticsTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(currentTab.title) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex.value == index)
                                    currentTab.selectedIcon else currentTab.unSelectedIcon,
                                contentDescription = "Tab Icon"
                            )
                        }
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
                        0 -> OverviewCharts()
                        1 -> FinanceCharts()
                        2 -> HealthCharts()
                    }
                }
            }

//            val data = listOf(
//                PieSlice(
//                    color = Color.Blue,
//                    label = "Blue",
//                    value = 2.1,
//                    isTapped = false,
//                ),
//                PieSlice(
//                    color = Color.Red,
//                    label = "Red",
//                    value = 4.10,
//                    isTapped = false,
//                ),
//                PieSlice(
//                    color = Color.Green,
//                    label = "Green",
//                    value = 1.1,
//                    isTapped = false,
//                )
//            )
//            val sortedData = data.sortedByDescending { it.value }
//
//
//
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                contentPadding = PaddingValues(8.dp)
//            ) {
//
//            }
        }
    }
}
