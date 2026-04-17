package com.example.alcoholtracker.ui.screens


import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.domain.notImplemented
import com.example.alcoholtracker.domain.usecase.DrinkCreateRequest
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.createNewRequest
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.getFinalAmount
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.getLocalDateTime
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
import com.example.alcoholtracker.ui.viewmodel.DrinkFormState
import com.example.alcoholtracker.ui.viewmodel.DrinkViewModel
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel
import com.vamsi.snapnotify.SnapNotify
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AddDrinkScreen(
    onAddDrink: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: UserAndUserDrinkLogViewModel = hiltViewModel(),
    drinkViewModel: DrinkViewModel = hiltViewModel()
){


    val drinkUiState = drinkViewModel.uiState.collectAsState()
    val formState = viewModel.formState.collectAsState()
    val recipientOptions = viewModel.recipientOptions.collectAsState(initial = emptyList())
    val categoryOptions = DrinkCategory.entries.toList()
    val amountOptions = drinkViewModel.getDrinkUnitsForCategory(formState.value.selectedCategory)
    val drinkOptions = drinkUiState.value.suggestions

    LaunchedEffect(formState.value.drinkName, formState.value.selectedCategory) {

        delay(300)
        if (formState.value.drinkName.isNotBlank()) {
            drinkViewModel.searchDrinks(formState.value.drinkName, formState.value.selectedCategory)
        }
    }

     val events = remember(viewModel) {
        DrinkFormEvents(
            onDrinkNameChange = viewModel::updateDrinkName,
            onCategoryChange = viewModel::updateSelectedCategory,
            onDrinkChange = viewModel::updateSelectedDrink,
            onDrinkUnitChange = viewModel::updateSelectedDrinkUnit,
            onAmountChange = { viewModel.updateSelectedAmount(it, formState.value.selectedDrinkUnit) },
            onABVChange = viewModel::updateABV,
            onPriceChange = viewModel::updatePrice,
            onRecipientChange = viewModel::updateRecipient,
            onDateChange = viewModel::updateSelectedDate,
            onTimeChange = viewModel::updateSelectedTime,
            onNotesChange = viewModel::updateNotes,
            onLocationChange = viewModel::updateLocation,
            onSaveDrink = {
                if (formState.value.drinkName.isNotBlank() ) {
                    val request = createNewRequest(formState.value)
                    viewModel.logDrink(request)
                    onAddDrink()
                }
            }
        )
    }




    DrinkFormContent(
        onBackClick = onBackClick,
        categoryOptions = categoryOptions,
        amountOptions = amountOptions,
        drinkOptions = drinkOptions,
        recipientOptions = recipientOptions.value,
        state = formState.value,
        events = events

    )
}

@Composable
fun EditDrinkScreen(
    onAddDrink: () -> Unit,
    onBackClick: () -> Unit,
    drinkToEditId: Int,
    viewModel: UserAndUserDrinkLogViewModel = hiltViewModel(),
    drinkViewModel: DrinkViewModel = hiltViewModel()
){



    val drinkUiState = drinkViewModel.uiState.collectAsState()
    val formState = viewModel.formState.collectAsState()
    val recipientOptions = viewModel.recipientOptions.collectAsState(initial = emptyList())
    val categoryOptions = DrinkCategory.entries.toList()
    val amountOptions = drinkViewModel.getDrinkUnitsForCategory(formState.value.selectedCategory)
    val drinkOptions = drinkUiState.value.suggestions

    val events = remember(viewModel) {
        DrinkFormEvents(
            onDrinkNameChange = viewModel::updateDrinkName,
            onCategoryChange = viewModel::updateSelectedCategory,
            onDrinkChange = viewModel::updateSelectedDrink,
            onDrinkUnitChange = viewModel::updateSelectedDrinkUnit,
            onAmountChange = { viewModel.updateSelectedAmount(formState.value.inputAmount, formState.value.selectedDrinkUnit) },
            onABVChange = viewModel::updateABV,
            onPriceChange = viewModel::updatePrice,
            onRecipientChange = viewModel::updateRecipient,
            onDateChange = viewModel::updateSelectedDate,
            onTimeChange = viewModel::updateSelectedTime,
            onNotesChange = viewModel::updateNotes,
            onLocationChange = viewModel::updateLocation,
            onSaveDrink = {
                if (formState.value.drinkName.isNotBlank() ) {
                    val request = createNewRequest(formState.value)
                    viewModel.updateDrink(drinkToEditId,request)
                    onAddDrink()
                }
            }
        )
    }


    LaunchedEffect(drinkToEditId) {
        viewModel.getDrinkById(drinkToEditId)
    }
    val drinkToEdit by viewModel.drinkById.collectAsState()

    LaunchedEffect(drinkToEdit) {
    drinkToEdit?.let { loadedDrink ->
        viewModel.populateFormForEdit(loadedDrink)
    }
}



    Crossfade(drinkToEdit, animationSpec = tween(1000)) {
        if (it == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
        } else {
            DrinkFormContent(
                onBackClick = onBackClick,
                categoryOptions = categoryOptions,
                amountOptions = amountOptions,
                drinkOptions = drinkOptions,
                recipientOptions = recipientOptions.value,
                state = formState.value.copy(isEdit = true),
                events = events
            )
        }
    }
}


@Composable
fun DrinkFormContent(
    onBackClick: () -> Unit,
    categoryOptions: List<DrinkCategory>,
    amountOptions: List<DrinkUnit>,
    drinkOptions: List<Drink>,
    recipientOptions: List<String>,
    state: DrinkFormState,
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

