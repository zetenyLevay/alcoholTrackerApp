package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.compose.AlcoholTrackerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DateHeader(date: LocalDate, drinkLogs: List<UserDrinkLog>) {

    val formattedDate = if (date.dayOfMonth == LocalDate.now().dayOfMonth) {
        "Today"
    } else if (date.dayOfMonth == LocalDate.now().minusDays(1).dayOfMonth) {
        "Yesterday"
    } else {
        date.format(DateTimeFormatter.ofPattern("E, MMM d"))
    }

    val totalAmount = drinkLogs.sumOf { it.amount }
    val totalCost = drinkLogs.sumOf { it.cost ?: 0.0 }



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ){
        Row(

        ){
            Text(formattedDate, modifier = Modifier
                .weight(1f),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text("Total cost: ${totalCost}€",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)

                Text("Total amount: $totalAmount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)

            }
        }
    }
    HorizontalDivider()

}

@Preview(showBackground = true)
@Composable
fun PreviewDateHeader() {
    AlcoholTrackerTheme() {
        DateHeader(LocalDate.now(), emptyList())
    }
}