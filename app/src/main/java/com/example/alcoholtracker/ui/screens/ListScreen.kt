package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.ListTopBar
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListFull
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel
import com.example.alcoholtracker.ui.viewmodel.ListEffect
import com.example.alcoholtracker.ui.viewmodel.ListEvent
import com.example.alcoholtracker.ui.viewmodel.ListUiState
import com.example.alcoholtracker.ui.viewmodel.ListViewModel
import com.example.compose.AlcoholTrackerTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ListScreen(
    onFABClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {

    val state = viewModel.listUiState.collectAsState()

    LaunchedEffect(state.value.effect){
        when(val effect = state.value.effect){
            is ListEffect.NavigateToDetailedItem -> {
                viewModel.processEvent(ListEvent.ConsumeEffect)
                onItemClick(effect.logId)
            }
            is ListEffect.NavigateToDrinkForm -> {
                viewModel.processEvent(ListEvent.ConsumeEffect)
                if (effect.logId != -1) {
                    onEditClick(effect.logId)
                }else {
                    onFABClick()
                }

            }
            is ListEffect.ShowError -> {
                viewModel.processEvent(ListEvent.ConsumeEffect)
            }
            ListEffect.ShowItemRemoved -> {
                viewModel.processEvent(ListEvent.ConsumeEffect)
            }
            null -> {

            }
        }
    }
    ListScreen(
        viewModel::processEvent,
        state.value
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onEvent: (ListEvent) -> Unit,
    state: ListUiState,
){


    val lifecycleOwner = LocalLifecycleOwner.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = { AddButton {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            onEvent(ListEvent.OnFABClick)
        } },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ListTopBar()
        }
    ) { innerPadding ->

        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    TextField(
                        state = state.query,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(Icons.Default.FilterList,
                            contentDescription = "Filter")
                    }
                }

                AlcoholListFull(
                    onEditClick = { onEvent(ListEvent.OnEditClick(it)) },
                    onItemClick = { onEvent(ListEvent.OnItemClick(it)) },
                    onRemove = {onEvent(ListEvent.OnRemoveItem(it))},
                    drinkLogs = state.drinkLogs,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewListScreen(){
    AlcoholTrackerTheme() {
         ListScreen(
        onEvent = {},
        state = ListUiState(
            drinkLogs = mapOf(
                LocalDate.now() to listOf(
                    UserDrinkLog(
                        name = "Beer",
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
                    )
                )
            )
        )
    )
    }

}