package com.example.alcoholtracker.ui.components.detailitemcomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.sharp.Layers
import androidx.compose.material.icons.sharp.MonetizationOn
import androidx.compose.material.icons.sharp.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AlcoholTrackerTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CardGrid(
    cost: Double,
    amount: Int,
    dateTime: LocalDateTime,
) {

    val date = dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))


    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f).height(88.dp)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.inversePrimary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = Icons.Sharp.Payments,
                            contentDescription = "Money",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                    ) {
                        Text("COST",
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        Text(text = "€${cost}",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                            maxLines = 1
                        )
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f).height(88.dp)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.inversePrimary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = Icons.Filled.LocalDrink,
                            contentDescription = "MONEY",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 16.dp),

                    ) {
                        Text("AMOUNT",
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        Text(text = "${amount}ml",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.height(88.dp).fillMaxWidth()


        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ){
                Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.inversePrimary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "TIME",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                Column(
                        modifier = Modifier.padding(start = 16.dp),
                    ) {
                        Text("TIMESTAMP",
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                            )
                         Row(
                             horizontalArrangement = Arrangement.spacedBy(12.dp)
                         ) {
                            Text(text = time,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                             Text(
                                text = "\u2022",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
            }
        }
    }
}

@Preview
@Composable
fun SmallDetailCardPreview() {
    AlcoholTrackerTheme {
        CardGrid(
            cost = 5.00,
            amount = 500,
            dateTime = java.time.LocalDateTime.now()
        )
    }

}

