package com.example.alcoholtracker.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.R
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory.BEER
import com.example.alcoholtracker.domain.model.DrinkCategory.COCKTAIL
import com.example.alcoholtracker.domain.model.DrinkCategory.OTHER
import com.example.alcoholtracker.domain.model.DrinkCategory.SPIRIT
import com.example.alcoholtracker.domain.model.DrinkCategory.WINE
import com.example.alcoholtracker.domain.usecase.DrinkCreateRequest
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.createNewRequest
import com.example.alcoholtracker.ui.components.DetailTopBar
import com.example.alcoholtracker.ui.components.detailitemcomponents.CardGrid
import com.example.alcoholtracker.ui.components.detailitemcomponents.DetailRow
import com.example.alcoholtracker.ui.components.detailitemcomponents.ImageCard
import com.example.alcoholtracker.ui.components.detailitemcomponents.LocationCard
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
            .fillMaxSize()
            ,
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
                onFavoriteClick =
                    {val request = createNewRequest(userDrink!!.copy(isFavorite = !userDrink!!.isFavorite))
                    viewModel.updateDrink(userDrink!!.logId, request) },
                isFavorite = userDrink?.isFavorite ?: false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ImageCard(
                src = userDrink?.imgURI?.toInt() ?: src,
                name = userDrink?.name ?: "No Drink",
                description = "A glass of ${userDrink?.category?.name ?: "Nothing"}",
                abv = userDrink?.alcoholPercentage ?: 0.0,
                category = userDrink?.category?.name ?: "No Category"
            )
            CardGrid(
                cost = userDrink?.cost ?: 0.0,
                amount = userDrink?.amount ?: 0,
                dateTime = userDrink?.date ?: java.time.LocalDateTime.now()
            )
            DetailRow()
            LocationCard(
                location = userDrink?.locationName ?: "No Location",
                notes = userDrink?.notes ?: "No Notes",
                recipient = userDrink?.recipient ?: "No Recipient"
            )

        }
    }
}




