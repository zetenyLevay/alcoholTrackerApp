package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListFull
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel
import com.example.alcoholtracker.ui.viewmodel.ListEffect
import com.example.alcoholtracker.ui.viewmodel.ListEvent
import com.example.alcoholtracker.ui.viewmodel.ListUiState
import com.example.alcoholtracker.ui.viewmodel.ListViewModel
import java.time.LocalDate

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
                onItemClick(effect.logId)
                viewModel.processEvent(ListEvent.ConsumeEffect)
            }
            is ListEffect.NavigateToDrinkForm -> {
                if (effect.logId != -1) {
                    onEditClick(effect.logId)
                }else {
                    onFABClick()
                }
                viewModel.processEvent(ListEvent.ConsumeEffect)
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

@Composable
fun ListScreen(
    onEvent: (ListEvent) -> Unit,
    state: ListUiState,
){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = { AddButton { onEvent(ListEvent.OnFABClick) } },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Surface(modifier = Modifier.padding(innerPadding)) {

            AlcoholListFull(
                onEditClick = { onEvent(ListEvent.OnEditClick(it)) },
                onItemClick = { onEvent(ListEvent.OnItemClick(it)) },
                onRemove = {onEvent(ListEvent.OnRemoveItem(it))},
                drinkLogs = state.drinkLogs
            )
        }
    }
}
