@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.alcoholtracker.ui.components.analytics

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.analytics.ChartEntry
import com.example.alcoholtracker.data.analytics.PieSlice
import com.example.alcoholtracker.ui.components.analytics.graphs.PieChart

@Composable
fun <T: ChartEntry> ChartCard(
    modifier: Modifier = Modifier,
    title: String,
    onShowDetails: () -> Unit,
    data: List<T>,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope
    content: @Composable (Modifier?, List<T>) -> Unit,
){

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(4.dp)
                    .semantics { heading() }
            )
            content( Modifier
                .clickable(
                    onClick = {
                        onShowDetails
                    }
                ),
                data
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun CardPreview(){

    val data = listOf(
        PieSlice(
            color = Color.Blue,
            label = "Blue",
            value = 2.1,
            isTapped = false,
        ),
        PieSlice(
            color = Color.Red,
            label = "Red",
            value = 4.10,
            isTapped = false,
        ),
        PieSlice(
            color = Color.Green,
            label = "Green",
            value = 1.1,
            isTapped = false,
        )
    )
    val sortedData = data.sortedByDescending { it.value }

    Column {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            ChartCard(
                modifier = Modifier
                    .applyCardStyle(CardStyle.GridSquare)
                    .weight(1f),
                title = "Card 1",
                onShowDetails = {},
                data = sortedData
            ){modifier,data ->
                PieChart(
                    data = data
                )
            }
            ChartCard(
                modifier = Modifier
                    .applyCardStyle(CardStyle.GridSquare)
                    .weight(1f),
                title = "Card 1",
                onShowDetails = {},
                data = sortedData
            ){modifier,data ->
                PieChart(
                    data = data
                )
            }
        }
        ChartCard(
            modifier = Modifier
                .applyCardStyle(CardStyle.Landscape),
            title = "Card 1",
            onShowDetails = {},
            data = sortedData
        ){modifier,data ->
            PieChart(
                data = data
            )
        }
    }
}
