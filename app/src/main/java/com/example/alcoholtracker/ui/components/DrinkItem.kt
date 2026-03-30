package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.UserDrinkLog

@Composable
fun DrinkItem(
    item: UserDrinkLog,
    listType: AlcoholListType,
    onEditClick: (UserDrinkLog) -> Unit,
    onAddClick: (UserDrinkLog) -> Unit,
) {
    ListItem(

        modifier = Modifier
            .heightIn(72.dp),
        headlineContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(item.name, modifier = Modifier.weight(1f))
            }

        },
        supportingContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "${item.inputAmount?.toInt()} ${item.drinkUnit?.name}",
                    modifier = Modifier.weight(1f)
                )
            }

        },
        trailingContent = {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "${item.alcoholPercentage}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light,

                        )
                    Text(
                        text = "€${item.cost}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Light
                    )
                }


                when (listType) {
                    AlcoholListType.FULL -> IconButton(onClick = {
                        onEditClick(item)
                    }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }

                    AlcoholListType.HOME -> {}
                    AlcoholListType.LOG -> {
                        IconButton(onClick = { onAddClick(item) }) {
                            Icon(Icons.Default.AddCircleOutline, "Add Item")
                        }
                    }
                }
            }


        }
    )
    HorizontalDivider()
}
