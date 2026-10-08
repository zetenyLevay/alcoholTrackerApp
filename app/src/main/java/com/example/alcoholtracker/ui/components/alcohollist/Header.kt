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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.utils.formatCost
import com.example.alcoholtracker.utils.formatVolume
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DateHeader(
    date: LocalDate,
    totalAmount: Double,
    totalCost: Double,
    modifier: Modifier = Modifier) {

    val today = LocalDate.now()
    val formattedDate = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("E, MMM d"))
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(formattedDate, modifier = Modifier
                .weight(1f)
                .semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text("Total: ${formatVolume(totalAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)


            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(4.dp)
                    .background(color = MaterialTheme.colorScheme.onSurfaceVariant, shape = CircleShape)
            )

            Text(formatCost(totalCost),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

}
