package com.example.alcoholtracker.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.ui.components.DrinkBanner
import com.example.alcoholtracker.ui.components.HomeTopBar
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListHome
import com.example.alcoholtracker.ui.components.progressbar.ProgressBar
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarEditDialog
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel
import com.example.alcoholtracker.ui.viewmodel.HomeEffect
import com.example.alcoholtracker.ui.viewmodel.HomeEvent
import com.example.alcoholtracker.ui.viewmodel.HomeEvent.*
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
                //TODO
            }

            is HomeEffect.NavigateToDetailedItem -> {
                onItemClick(effect.logId)
            }

            HomeEffect.NavigateToDrinkForm -> {
                onFABClick()
            }

            HomeEffect.ShowItemRemoved -> {

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

    ){

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopBar() {} },
        floatingActionButton = { onEvent(OnFABClick) },
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
                        onItemClick ={ onEvent(OnItemClick(it)) },
                        onRemove = { onEvent(OnItemRemove(it)) },
                        drinkLogs = listOf()
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
                        "${state.currentDrinkCount} drinks, ${state.currentAmountMl}ml",
                        (state.currentMoneySpent / target).toFloat()
                    )
                    ProgressBarType.COUNT -> Triple(
                        "${state.currentDrinkCount}/${target.toInt()} drinks",
                        "${state.currentAmountMl}ml, ${state.currentMoneySpent}$",
                        (state.currentDrinkCount / target).toFloat()
                    )
                    ProgressBarType.AMOUNT -> Triple(
                        "${state.currentAmountMl}/${target.toInt()}ml",
                        "${state.currentMoneySpent}$ , ${state.currentDrinkCount} drinks",
                        (state.currentAmountMl / target).toFloat()
                    )
                }


                Card() {
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
