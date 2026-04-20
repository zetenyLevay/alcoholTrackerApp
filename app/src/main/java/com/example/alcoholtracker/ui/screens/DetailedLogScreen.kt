package com.example.alcoholtracker.ui.screens

import android.util.Log
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
import com.example.alcoholtracker.ui.viewmodel.DetailedLogEffect
import com.example.alcoholtracker.ui.viewmodel.DetailedLogEvent
import com.example.alcoholtracker.ui.viewmodel.DetailedLogUiState
import com.example.alcoholtracker.ui.viewmodel.DetailedLogViewModel
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel

@Composable
fun DetailedLogScreen(
    logId: Int,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: DetailedLogViewModel = hiltViewModel()
) {

    val state by viewModel.detailedLogUiState.collectAsState()

    LaunchedEffect(state.effect) {
        when (val effect = state.effect) {
            is DetailedLogEffect.ShowError -> {
                viewModel.processEvent(DetailedLogEvent.ConsumeEffect)
            }
            is DetailedLogEffect.ShowItemRemoved -> {
                viewModel.processEvent(DetailedLogEvent.ConsumeEffect)
            }
            is DetailedLogEffect.NavigateToDrinkForm -> {
                onEditClick(effect.logId)
            }
            null -> {
                // No effect
            }
        }
    }

    DetailedLogScreen(
        src = R.drawable.beer_icon,
        onBackClick = onBackClick,
        onEvent = viewModel::processEvent,
        state = state
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedLogScreen(
    src: Int,
    onBackClick: () -> Unit,
    onEvent: (DetailedLogEvent) -> Unit,
    state: DetailedLogUiState,
){

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val drinkLog = state.drinkLog ?: return


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            ,
        topBar = {
            DetailTopBar(
                onEditClick = {
                    onEvent(DetailedLogEvent.OnEditClick(drinkLog.logId))
                },
                onDeleteClick = {
                    onEvent(DetailedLogEvent.OnRemoveClick(drinkLog))
                },
                onBackClick = { onBackClick() },
                onFavoriteClick = { onEvent(DetailedLogEvent.OnFavoriteClick(drinkLog)) },
                isFavorite = drinkLog.isFavorite,
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
                src = drinkLog.imgURI?.toInt() ?: src,
                name = drinkLog.name,
                description = "A glass of ${drinkLog.category.name }",
                abv = drinkLog.alcoholPercentage ?: 0.0,
                category = drinkLog.category.name
            )
            CardGrid(
                cost = drinkLog.cost ?: 0.0,
                amount = drinkLog.amount  ,
                dateTime = drinkLog.date
            )
            DetailRow()
            LocationCard(
                location = drinkLog.locationName ?: "No Location",
                notes = drinkLog.notes ?: "No Notes",
                recipient = drinkLog.recipient ?: "No Recipient"
            )
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}




