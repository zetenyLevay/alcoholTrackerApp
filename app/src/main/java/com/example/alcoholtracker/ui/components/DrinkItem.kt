package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.components.detailitemcomponents.TagLabel
import com.example.alcoholtracker.utils.formatAbv
import com.example.alcoholtracker.utils.formatCost
import com.example.alcoholtracker.utils.formatVolume
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
            .height(72.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painterResource(item.category.icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(8.dp)).padding(8.dp),
            tint = MaterialTheme.colorScheme.primary)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagLabel(
                    text = item.category.nameString,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                Text(
                    formatAbv(item.alcoholPercentage ?: 0.0),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(formatVolume(item.amount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            Text(formatCost(item.cost ?: 0.0),
                style = MaterialTheme.typography.bodyMedium,
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
