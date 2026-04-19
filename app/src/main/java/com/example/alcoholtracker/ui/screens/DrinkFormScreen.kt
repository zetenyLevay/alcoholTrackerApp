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
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnABVChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnAmountChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnCategoryChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDateChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDrinkLogChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDrinkLogNameChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnDrinkLogUnitChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnLocationChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnNotesChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnPriceChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnRecipientChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnSaveDrinkLog
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormEvent.OnTimeChange
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormUiState
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel
import com.vamsi.snapnotify.SnapNotify

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
                onAddDrink()
                viewModel.processEvent(DrinkLogFormEvent.ConsumeEffect)
            }
            is DrinkLogFormEffect.ShowError -> {
                SnapNotify.show(effect.message)
                viewModel.processEvent(DrinkLogFormEvent.ConsumeEffect)
            }
            null -> {
            }
        }
    }


    DrinkFormContent(
        onBackClick = onBackClick,
        onEvent = viewModel::processEvent,
        state = formState.value,

    )
}

@Composable
fun DrinkFormContent(
    onBackClick: () -> Unit,
    onEvent: (DrinkLogFormEvent) -> Unit,
    state: DrinkLogFormUiState,
    ) {

    val scrollState = rememberScrollState()

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
                onClick = { onEvent(OnSaveDrinkLog) },
                icon = { Icon(Icons.Filled.Add, "Add Button") },
                text = {Text(if (state.isEdit) "Update Drink" else "Add Drink")}

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
                drinkName = state.inputs.drinkName,
                onTyped = { onEvent(OnDrinkLogNameChange(it)) },
                onSelected = { onEvent(OnDrinkLogChange(it)) },
                options = state.options.drinkOptions
            )
            AmountDropDown(
                amount = state.inputs.inputAmount,
                selectedUnit = state.inputs.selectedDrinkUnit,
                onSelected = { onEvent(OnDrinkLogUnitChange(it)) },
                onTyped = { onEvent(OnAmountChange(it)) },
                options = state.options.amountOptions
            )
            ABVAndPriceTextFields(
                abv = state.inputs.alcoholPercentage,
                price = state.inputs.cost,
                onABVChange = { onEvent(OnABVChange(it)) },
                onPriceChange = { onEvent(OnPriceChange(it)) }
            )
            RecipientAutoComplete(
                recipient = state.inputs.recipient,
                onRecipientChange = { onEvent(OnRecipientChange(it)) },
                recipientOptions = state.options.recipientOptions
            )
            DateAndTimePicker(
                currentDate = state.inputs.selectedDate,
                currentTime = state.inputs.selectedTime,
                onTimeSelected = { onEvent(OnTimeChange(it)) },
                onDateSelected = { onEvent(OnDateChange(it)) },
            )
            LocationTextField(
                location = state.inputs.locationName,
                onLocationChange = { onEvent(OnLocationChange(it)) }
            )
            NotesTextField(
                notes = state.inputs.notes,
                onNotesChange = { onEvent(OnNotesChange(it)) }
            )
            Spacer(modifier = Modifier.padding(bottom = 16.dp))

        }
    }
}



