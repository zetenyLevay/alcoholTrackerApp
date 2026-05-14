package com.example.alcoholtracker.ui.screens


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarAction
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.HistoryTopBar
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListFull
import com.example.compose.AlcoholTrackerTheme
import java.time.LocalDate
import java.time.LocalDateTime
import com.example.alcoholtracker.ui.viewmodel.HistoryEffect
import com.example.alcoholtracker.ui.viewmodel.HistoryEvent
import com.example.alcoholtracker.ui.viewmodel.HistoryFilterStates
import com.example.alcoholtracker.ui.viewmodel.HistoryUiModel
import com.example.alcoholtracker.ui.viewmodel.HistoryUiState
import com.example.alcoholtracker.ui.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    onFABClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {

    val state = viewModel.historyUiState.collectAsStateWithLifecycle()
    val pagedLogs = viewModel.pagedLogs.collectAsLazyPagingItems()

    LaunchedEffect(state.value.effect){
        when(val effect = state.value.effect){
            is HistoryEffect.NavigateToDetailedItem -> {
                viewModel.processEvent(HistoryEvent.ConsumeEffect)
                onItemClick(effect.logId)
            }
            is HistoryEffect.NavigateToDrinkForm -> {
                viewModel.processEvent(HistoryEvent.ConsumeEffect)
                if (effect.logId != -1) {
                    onEditClick(effect.logId)
                }else {
                    onFABClick()
                }

            }
            is HistoryEffect.ShowError -> {
                viewModel.processEvent(HistoryEvent.ConsumeEffect)
            }
            is HistoryEffect.ShowItemRemoved -> {
                viewModel.processEvent(HistoryEvent.ConsumeEffect)
                SnackbarController.sendEvent(
                    event = SnackBarEvent(
                        message = "Item Removed",
                        action = SnackbarAction(
                            name = "Undo",
                            action = { viewModel.processEvent(HistoryEvent.OnUndoRemoveItem(effect.log)) }

                        )
                    )
                )
            }
            null -> {

            }
        }
    }
    HistoryScreen(
        viewModel::processEvent,
        state.value,
        viewModel.filterState,
        pagedLogs

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onEvent: (HistoryEvent) -> Unit,
    state: HistoryUiState,
    filterState: HistoryFilterStates,
    pagedLogs: LazyPagingItems<HistoryUiModel>
) {


    val lifecycleOwner = LocalLifecycleOwner.current




    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        floatingActionButton = {
            AddButton {
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                    onEvent(HistoryEvent.OnFABClick)
            }
        },
        modifier = Modifier.fillMaxSize(),
        topBar = { HistoryTopBar() }
    ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp).animateContentSize()) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        state = filterState.queryState,
                        modifier = Modifier.weight(1f).height(44.dp),
                        leadingIcon = {Icon(Icons.Default.Search, "Search")},
                        placeholder = {Text("Search history...")},
                        lineLimits = TextFieldLineLimits.SingleLine,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    )
                    IconButton(
                        onClick = { /* TODO: Open filter sheet or dialog */ },
                        modifier = Modifier.padding(start = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .size(44.dp)

                    ) {
                        Icon(
                            Icons.Default.FilterList  ,
                            contentDescription = "Filter"
                        )
                    }
                }


                AlcoholListFull(
                    onEditClick = { onEvent(HistoryEvent.OnEditClick(it)) },
                    onItemClick = { onEvent(HistoryEvent.OnItemClick(it)) },
                    onRemove = { onEvent(HistoryEvent.OnRemoveItem(it)) },
                    drinkLogs = pagedLogs,
                    modifier = Modifier.weight(1f)
                )
            }
        }

}

//@Preview
//@Composable
//fun PreviewListScreen() {
//    AlcoholTrackerTheme() {
//        HistoryScreen(
//            onEvent = {},
//            state = HistoryUiState(
//                drinkLogs = mapOf(
//                    LocalDate.now() to listOf(
//                        DrinkLog(
//                            name = "Beer",
//                            category = DrinkCategory.BEER,
//                            cost = 6.2,
//                            amount = 500,
//                            alcoholPercentage = 10.2,
//                            date = LocalDateTime.now(),
//                            imgURI = "",
//                            locationName = "",
//                            notes = "",
//                            recipient = "",
//                            isFavorite = false,
//                            logId = 0,
//                            drinkId = 0,
//                            userId = "a",
//                            inputAmount = 500.0,
//                            longitude = null,
//                            latitude = null,
//                            drinkUnit = DrinkUnit("milliliters", 1),
//                        ),
//                                DrinkLog(
//                                name = "Beer",
//                        category = DrinkCategory.BEER,
//                        cost = 6.2,
//                        amount = 500,
//                        alcoholPercentage = 10.2,
//                        date = LocalDateTime.now(),
//                        imgURI = "",
//                        locationName = "",
//                        notes = "",
//                        recipient = "",
//                        isFavorite = false,
//                        logId = 1,
//                        drinkId = 0,
//                        userId = "a",
//                        inputAmount = 500.0,
//                        longitude = null,
//                        latitude = null,
//                        drinkUnit = DrinkUnit("milliliters", 1),
//                    )
//                    ),
//                    LocalDate.now().minusDays(2) to listOf(
//                        DrinkLog(
//                            name = "Beer",
//                            category = DrinkCategory.BEER,
//                            cost = 6.2,
//                            amount = 500,
//                            alcoholPercentage = 10.2,
//                            date = LocalDateTime.now(),
//                            imgURI = "",
//                            locationName = "",
//                            notes = "",
//                            recipient = "",
//                            isFavorite = false,
//                            logId = 4,
//                            drinkId = 0,
//                            userId = "a",
//                            inputAmount = 500.0,
//                            longitude = null,
//                            latitude = null,
//                            drinkUnit = DrinkUnit("milliliters", 1),
//                        ),
//                        DrinkLog(
//                            name = "Beer",
//                            category = DrinkCategory.BEER,
//                            cost = 6.2,
//                            amount = 500,
//                            alcoholPercentage = 10.2,
//                            date = LocalDateTime.now(),
//                            imgURI = "",
//                            locationName = "",
//                            notes = "",
//                            recipient = "",
//                            isFavorite = false,
//                            logId = 3,
//                            drinkId = 0,
//                            userId = "a",
//                            inputAmount = 500.0,
//                            longitude = null,
//                            latitude = null,
//                            drinkUnit = DrinkUnit("milliliters", 1),
//                        )
//                    )
//                )
//            )
//        )
//    }
//}

