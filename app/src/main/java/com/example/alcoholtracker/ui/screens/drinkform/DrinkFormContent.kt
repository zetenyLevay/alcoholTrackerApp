package com.example.alcoholtracker.ui.screens.drinkform

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.components.LogDrinkTopBar
import com.example.alcoholtracker.ui.components.logComponents.ABVAndPriceTextFields
import com.example.alcoholtracker.ui.components.logComponents.AmountDropDown
import com.example.alcoholtracker.ui.components.logComponents.CategoryDropDown
import com.example.alcoholtracker.ui.components.logComponents.DateAndTimePicker
import com.example.alcoholtracker.ui.components.logComponents.DrinkAutoComplete
import com.example.alcoholtracker.ui.components.logComponents.LocationTextField
import com.example.alcoholtracker.ui.components.logComponents.NotesTextField
import com.example.alcoholtracker.ui.components.logComponents.RecipientAutoComplete
import com.example.alcoholtracker.ui.viewmodel.DrinkFormEvents
import com.example.alcoholtracker.ui.viewmodel.DrinkFormUiState

@Composable
fun DrinkFormContent(
    onBackClick: () -> Unit,
    categoryOptions: List<DrinkCategory>,
    amountOptions: List<DrinkUnit>,
    drinkOptions: List<Drink>,
    recipientOptions: List<String>,
    state: DrinkFormUiState,
    events: DrinkFormEvents,

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
                onClick = { events.onSaveDrink() },
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
                selected = state.selectedCategory,
                onSelected = { events.onCategoryChange(it) },
                categoryList = categoryOptions
            )

            DrinkAutoComplete(
                drinkName = state.drinkName,
                onTyped = { events.onDrinkNameChange(it) },
                onSelected = { events.onDrinkChange(it) },
                options = drinkOptions
            )

            AmountDropDown(
                amount = state.inputAmount,
                selectedUnit = state.selectedDrinkUnit,
                onSelected = { events.onDrinkUnitChange(it) },
                onTyped = { events.onAmountChange(it) },
                options = amountOptions
            )

            ABVAndPriceTextFields(
                abv = state.alcoholPercentage,
                price = state.cost,
                onABVChange = { events.onABVChange(it) },
                onPriceChange = { events.onPriceChange(it) }
            )


            RecipientAutoComplete(
                recipient = state.recipient,
                onRecipientChange = { events.onRecipientChange(it) },
                recipientOptions = recipientOptions
            )

            DateAndTimePicker(
                currentDate = state.selectedDate,
                currentTime = state.selectedTime,
                onTimeSelected = { events.onTimeChange(it) },
                onDateSelected = { events.onDateChange(it) },
            )
            LocationTextField(
                location = state.locationName,
                onLocationChange = { events.onLocationChange(it) }
            )
            NotesTextField(
                notes = state.notes,
                onNotesChange = { events.onNotesChange(it) }
            )
            Spacer(modifier = Modifier.padding(bottom = 16.dp))

        }
    }
}