package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.R
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.components.detailitemcomponents.TagLabel
import com.example.compose.AlcoholTrackerTheme
import java.time.LocalDateTime

@Composable
fun DrinkItem(
    item: DrinkLog,
    listType: AlcoholListType,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(72.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painterResource(R.drawable.beer),
            "Category Icon",
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp)).padding(8.dp),
            tint = MaterialTheme.colorScheme.primary)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(item.name,
                style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagLabel(
                    text = item.category.name,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                )
                Text(
                    "${item.alcoholPercentage} % ABV",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text("${item.amount}ml",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant

                )
            Text("€${String.format("%.2f", item.cost)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview
@Composable
fun Preview(){
    AlcoholTrackerTheme() {
        DrinkItem(
            item = DrinkLog(
                name = "Heineken",
                category = DrinkCategory.BEER,
                cost = 6.2,
                amount = 500,
                alcoholPercentage = 10.2,
                date = LocalDateTime.now(),
                imgURI = "",
                locationName = "",
                notes = "",
                recipient = "",
                isFavorite = false,
                logId = 0,
                drinkId = 0,
                userId = "a",
                inputAmount = 500.0,
                longitude = null,
                latitude = null,
                drinkUnit = DrinkUnit("milliliters", 1),
            ),
            listType = AlcoholListType.FULL,
        )
    }
}
