package com.example.alcoholtracker.ui.components.logComponents.tabs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.components.DrinkItem

@Composable
fun FavoritesList(
    drinks: List<UserDrinkLog>
) {

    if (drinks.isEmpty()) {
        Text("No favorite drinks found")
    } else {
        LazyColumn() {
            items(drinks.size) { index ->
                DrinkItem(drinks[index], AlcoholListType.LOG, {}, {})
            }
        }
    }
}