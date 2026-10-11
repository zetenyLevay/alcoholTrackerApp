package com.example.alcoholtracker.ui.components.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.analytics.PieSlice
import com.example.alcoholtracker.ui.components.analytics.graphs.PieChart


@Composable
fun OverviewCharts(scrollState: ScrollState = rememberScrollState()) {



    val test = listOf(
        PieSlice(
            label = "x",
            value = 2.1,
            color = Color.Red
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            ChartCard(
                modifier = Modifier
                    .weight(1f)
                    .applyCardStyle(CardStyle.GridSquare),
                title = "Who drank it?",
                onShowDetails = {},
                data = test
            ) { modifier, slices ->
                PieChart(
                    data = slices,
                    isTappable = false
                )
            }
            ChartCard(
                modifier = Modifier
                    .weight(1f)
                    .applyCardStyle(CardStyle.GridSquare),
                title = "Whats up?",
                onShowDetails = {},
                data = test
            ) { modifier, slices ->
                PieChart(
                    data = slices,
                    isTappable = false
                )
            }
        }
        ChartCard(
            modifier = Modifier
                .applyCardStyle(CardStyle.Landscape),
            title = "I am landscape?",
            onShowDetails = {},
            data = test
        ) { modifier, slices ->
            PieChart(
                data = slices
            )
        }
        ChartCard(
            modifier = Modifier

                .applyCardStyle(CardStyle.Large),
            title = "I am large?",
            onShowDetails = {},
            data = test
        ) { modifier, slices ->
            PieChart(
                data = slices
            )
        }
        ChartCard(
            modifier = Modifier
                .applyCardStyle(CardStyle.Compact),
            title = "I am compact?",
            onShowDetails = {},
            data = test
        ) { modifier, slices ->
            PieChart(
                data = slices
            )
        }

        val data = listOf(
            PieSlice(
                color = Color.Red,
                label = "Red",
                value = 2.0,
                isTapped = false
            ),
            PieSlice(
                color = Color.Blue,
                label = "Blue",
                value = 1.0,
                isTapped = false
            ),
            PieSlice(
                color = Color.Green,
                label = "Green",
                value = 3.0,
                isTapped = false
            ),
            PieSlice(
                color = Color.Yellow,
                label = "Yellow",
                value = 2.5,
                isTapped = false
            )
        )
        val sortedData = data.sortedByDescending { it.value }

        PieChart(modifier = Modifier.fillMaxSize(),
            data = sortedData,
            outerRingPercent = 35)
    }
}
