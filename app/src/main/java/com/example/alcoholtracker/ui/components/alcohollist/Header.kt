package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.compose.AlcoholTrackerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DateHeader(
    date: LocalDate,
    totalAmount: Double,
    totalCost: Double,
    modifier: Modifier = Modifier) {

    val formattedDate = if (date.dayOfMonth == LocalDate.now().dayOfMonth) {
        "Today"
    } else if (date.dayOfMonth == LocalDate.now().minusDays(1).dayOfMonth) {
        "Yesterday"
    } else {
        date.format(DateTimeFormatter.ofPattern("E, MMM d"))
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(formattedDate, modifier = Modifier
                .weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text("Total: ${totalAmount}ml",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)


            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(4.dp)
                    .background(color = MaterialTheme.colorScheme.onSurfaceVariant, shape = CircleShape)
            )

            Text("€${String.format("%.2f", totalCost)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)




        }
    }

}