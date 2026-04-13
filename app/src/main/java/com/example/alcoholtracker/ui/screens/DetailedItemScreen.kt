package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.R
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory.BEER
import com.example.alcoholtracker.domain.model.DrinkCategory.COCKTAIL
import com.example.alcoholtracker.domain.model.DrinkCategory.OTHER
import com.example.alcoholtracker.domain.model.DrinkCategory.SPIRIT
import com.example.alcoholtracker.domain.model.DrinkCategory.WINE
import com.example.alcoholtracker.ui.components.DetailTopBar
import com.example.alcoholtracker.ui.components.detailitemcomponents.ImageCard
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel

@Composable
fun DetailedItemScreen(
    logId: Int,
    onBackClick: () -> Unit,
    onEditClick: (UserDrinkLog) -> Unit,
    viewModel: UserAndUserDrinkLogViewModel = hiltViewModel()
) {


    LaunchedEffect(logId) {
        viewModel.getDrinkById(logId)
    }
    val userDrink by viewModel.drinkById.collectAsState()

    val src = when (userDrink?.category) {
        BEER -> R.drawable.beer_icon
        WINE -> 1
        SPIRIT -> 2
        COCKTAIL -> 3
        OTHER -> 4
        else -> R.drawable.beer_icon
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            DetailTopBar(
                onEditClick = {
                    onEditClick(userDrink!!)
                },
                onDeleteClick = {
                    viewModel.deleteDrink(userDrink!!)
                    onBackClick()
                },
                onBackClick = { onBackClick() },
                onFavoriteClick = {},
                isFavorite = userDrink?.isFavorite?:false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            ImageCard(
                src = src,
                name = userDrink?.name ?: "No Drink",
                description = "A glass of ${userDrink?.category?.name ?: "Nothing"}",
                abv = userDrink?.alcoholPercentage ?: 0.0,
                category = userDrink?.category?.name ?: "No Category"
            )
            Text("Detailed Item Screen")
            Text(userDrink?.name ?: "No Drink")
        }
    }
}