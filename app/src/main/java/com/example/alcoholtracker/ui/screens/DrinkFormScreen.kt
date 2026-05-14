package com.example.alcoholtracker.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.alcoholtracker.SnackBarEvent
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.ui.components.LogDrinkTopBar
import com.example.alcoholtracker.ui.components.logComponents.ABVAndPriceTextFields
import com.example.alcoholtracker.ui.components.logComponents.AmountDropDown
import com.example.alcoholtracker.ui.components.logComponents.CategoryDropDown
import com.example.alcoholtracker.ui.components.logComponents.DateAndTimePicker
import com.example.alcoholtracker.ui.components.logComponents.DrinkAutoComplete
import com.example.alcoholtracker.ui.components.logComponents.LocationTextField
import com.example.alcoholtracker.ui.components.logComponents.NotesTextField
import com.example.alcoholtracker.ui.components.logComponents.RecipientAutoComplete
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEffect
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent

import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnCategoryChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDateChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDrinkLogChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDrinkLogNameChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDrinkLogUnitChange

import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnSaveDrinkLog
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnTimeChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormTextStates
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormUiState
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel

@Composable
fun DrinkFormScreen(
    onAddDrink: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: DrinkLogFormViewModel = hiltViewModel(),
) {


    val formState = viewModel.formUiState.collectAsState()

    LaunchedEffect(formState.value.effect) {
        when (val effect = formState.value.effect) {

            is DrinkLogFormEffect.SaveDrinkLog -> {
                viewModel.processEvent(DrinkLogFormEvent.ConsumeEffect)
                onAddDrink()
                SnackbarController.sendEvent(
                    event = SnackBarEvent(
                        message = "Drink Saved",
                    )
                )

            }

            is DrinkLogFormEffect.ShowError -> {
                viewModel.processEvent(DrinkLogFormEvent.ConsumeEffect)
                SnackbarController.sendEvent(
                    event = SnackBarEvent(
                        effect.message
                    )
                )

            }

            null -> {
            }
        }
    }


    DrinkFormScreen(
        onBackClick = onBackClick,
        onEvent = viewModel::processEvent,
        state = formState.value,
        textState = viewModel.textStates

        )
}

@Composable
fun DrinkFormScreen(
    onBackClick: () -> Unit,
    onEvent: (DrinkLogFormEvent) -> Unit,
    state: DrinkLogFormUiState,
    textState: DrinkLogFormTextStates,
) {

    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    Scaffold(
        topBar = {
            LogDrinkTopBar(
                onBackClick = { onBackClick() },
                isEdit = state.isEdit
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                    {
                        onEvent(OnSaveDrinkLog)
                    }},
                icon = { Icon(Icons.Filled.Add, "Add Button") },
                text = { Text(if (state.isEdit) "Update Drink" else "Add Drink") }

            )
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        )
        {
            CategoryDropDown(
                selected = state.inputs.selectedCategory,
                onSelected = { onEvent(OnCategoryChange(it)) },
                categoryList = state.options.categoryOptions
            )
            DrinkAutoComplete(
                drinkName = textState.drinkName,
                onTyped = { onEvent(OnDrinkLogNameChange(it)) },
                onSelected = { onEvent(OnDrinkLogChange(it)) },
                options = state.options.drinkOptions
            )
            AmountDropDown(
                amount = textState.inputAmount,
                selectedUnit = state.inputs.selectedDrinkUnit,
                onSelected = { onEvent(OnDrinkLogUnitChange(it)) },
                options = state.options.amountOptions
            )
            ABVAndPriceTextFields(
                abv = textState.alcoholPercentage,
                price = textState.cost,
            )
            RecipientAutoComplete(
                recipient = textState.recipient,
                onRecipientChange = { }, //onEvent(OnRecipientChange(it)) },
                recipientOptions = state.options.recipientOptions
            )
            DateAndTimePicker(
                currentDate = state.inputs.selectedDate,
                currentTime = state.inputs.selectedTime,
                onTimeSelected = { onEvent(OnTimeChange(it)) },
                onDateSelected = { onEvent(OnDateChange(it)) },
            )
            LocationTextField(
                location = textState.locationName,
            )
            NotesTextField(
                notes = textState.notes,
            )
            Spacer(modifier = Modifier.padding(bottom = 16.dp))

        }
    }
}



