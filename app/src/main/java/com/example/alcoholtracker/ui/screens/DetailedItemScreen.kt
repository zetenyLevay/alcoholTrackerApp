package com.example.alcoholtracker.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.R
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory.BEER
import com.example.alcoholtracker.domain.model.DrinkCategory.COCKTAIL
import com.example.alcoholtracker.domain.model.DrinkCategory.OTHER
import com.example.alcoholtracker.domain.model.DrinkCategory.SPIRIT
import com.example.alcoholtracker.domain.model.DrinkCategory.WINE
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

    Crossfade(userDrink, animationSpec = tween(1000)) { userDrinkState ->
        if (userDrinkState == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }else {

            val src = when (userDrink?.category) {
                BEER -> R.drawable.beer_icon
                WINE -> 1
                SPIRIT -> 2
                COCKTAIL -> 3
                OTHER -> 4
                else -> R.drawable.beer_icon
            }
            DetailedItemScreen(
                src = src,
                userDrink = userDrink!!,
                onBackClick = { onBackClick() },
                onDeleteClick = { viewModel.deleteDrink(it) },
                onEditClick = { onEditClick(it) },
                onFavoriteClick = {
                    viewModel.updateDrink(
                        it.logId,
                        createNewRequest(it.copy(isFavorite = !it.isFavorite))
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedItemScreen(
    src: Int,
    userDrink: UserDrinkLog,
    onBackClick: () -> Unit,
    onDeleteClick: (UserDrinkLog) -> Unit,
    onEditClick: (UserDrinkLog) -> Unit,
    onFavoriteClick: (UserDrinkLog) -> Unit,
){

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            ,
        topBar = {
            DetailTopBar(
                onEditClick = {
                    onEditClick(userDrink)
                },
                onDeleteClick = {
                    onDeleteClick(userDrink)
                },
                onBackClick = { onBackClick() },
                onFavoriteClick = { onFavoriteClick(userDrink) },
                isFavorite = userDrink.isFavorite,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            ImageCard(
                src = userDrink.imgURI?.toInt() ?: src,
                name = userDrink.name,
                description = "A glass of ${userDrink.category.name }",
                abv = userDrink.alcoholPercentage ?: 0.0,
                category = userDrink.category.name
            )
            CardGrid(
                cost = userDrink.cost ?: 0.0,
                amount = userDrink.amount  ,
                dateTime = userDrink.date
            )
            DetailRow()
            LocationCard(
                location = userDrink.locationName ?: "No Location",
                notes = userDrink.notes ?: "No Notes",
                recipient = userDrink.recipient ?: "No Recipient"
            )
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}




