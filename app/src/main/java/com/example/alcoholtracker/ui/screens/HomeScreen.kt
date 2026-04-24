package com.example.alcoholtracker.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarAction
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.DrinkBanner
import com.example.alcoholtracker.ui.components.HomeTopBar
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListHome
import com.example.alcoholtracker.ui.components.progressbar.ProgressBar
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarEditDialog
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import com.example.alcoholtracker.ui.viewmodel.HomeEffect
import com.example.alcoholtracker.ui.viewmodel.HomeEvent
import com.example.alcoholtracker.ui.viewmodel.HomeEvent.ConsumeEffect
import com.example.alcoholtracker.ui.viewmodel.HomeEvent.OnFABClick
import com.example.alcoholtracker.ui.viewmodel.HomeEvent.OnItemClick
import com.example.alcoholtracker.ui.viewmodel.HomeEvent.OnItemRemove
import com.example.alcoholtracker.ui.viewmodel.HomeEvent.OnProgressBarUpdate
import com.example.alcoholtracker.ui.viewmodel.HomeUiState
import com.example.alcoholtracker.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onFABClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state = viewModel.homeUiState.collectAsState()

    LaunchedEffect(state.value.effect) {
        when (val effect = state.value.effect) {
            is HomeEffect.ShowError -> {
                viewModel.processEvent(ConsumeEffect)
            }

            is HomeEffect.NavigateToDetailedItem -> {
                onItemClick(effect.logId)
                viewModel.processEvent(ConsumeEffect)
            }

            HomeEffect.NavigateToDrinkForm -> {
                onFABClick()
                viewModel.processEvent(ConsumeEffect)
            }

            is HomeEffect.ShowItemRemoved -> {
                SnackbarController.sendEvent(
                    event = SnackBarEvent(
                        message = "Item Removed",
                        action = SnackbarAction(
                            name = "Undo",
                            action = { viewModel.processEvent(HomeEvent.OnUndoItemRemove(effect.log)) }

                        )
                    )
                )
                viewModel.processEvent(ConsumeEffect)
            }

            null -> {

            }
        }
    }

    HomeScreen(
        onEvent = viewModel::processEvent,
        state = state.value
    )

}

@Composable
fun HomeScreen(
    onEvent: (HomeEvent) -> Unit,
    state: HomeUiState,

    ) {

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopBar() {} },
        floatingActionButton = { AddButton(onClick = { onEvent(OnFABClick) }) },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {

            Column {

                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(260.dp)
                        .fillMaxWidth(),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    DrinkBanner(state.drinkCount)
                    HorizontalDivider()
                    AlcoholListHome(
                        onEditClick = {},
                        onItemClick = { onEvent(OnItemClick(it)) },
                        onRemove = { onEvent(OnItemRemove(it)) },
                        drinkLogs = state.tonightDrinkLogs
                    )
                }

                if (showDialog) {
                    ProgressBarEditDialog(
                        currentType = state.progressBarType,
                        currentTargets = state.targets,
                        onDismiss = { showDialog = false },
                        onEvent = { type, target ->
                            onEvent(OnProgressBarUpdate(type, target))
                            showDialog = false
                        }
                    )
                }

                val activeType = state.progressBarType
                val target = state.targets[activeType] ?: 1.0

                val (primaryText, secondaryText, progress) = when (activeType) {
                    ProgressBarType.MONEY -> Triple(
                        "${state.currentMoneySpent}/$target$",
                        "${state.drinkCount} drinks, ${state.currentAmountMl}ml",
                        (state.currentMoneySpent / target).toFloat()
                    )

                    ProgressBarType.COUNT -> Triple(
                        "${state.drinkCount}/${target.toInt()} drinks",
                        "${state.currentAmountMl}ml, ${state.currentMoneySpent}$",
                        (state.drinkCount / target).toFloat()
                    )

                    ProgressBarType.AMOUNT -> Triple(
                        "${state.currentAmountMl}/${target.toInt()}ml",
                        "${state.currentMoneySpent}$ , ${state.drinkCount} drinks",
                        (state.currentAmountMl / target).toFloat()
                    )
                }

                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth(),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    ProgressBar(
                        progress = progress,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        onEditClick = { showDialog = true }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFunction() {

}
