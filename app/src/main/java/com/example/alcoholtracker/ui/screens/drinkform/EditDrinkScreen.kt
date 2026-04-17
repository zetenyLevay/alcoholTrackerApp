package com.example.alcoholtracker.ui.screens.drinkform

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.createNewRequest
import com.example.alcoholtracker.ui.viewmodel.DrinkFormEvents
import com.example.alcoholtracker.ui.viewmodel.DrinkViewModel
import com.example.alcoholtracker.ui.viewmodel.DrinkFormViewModel

@Composable
fun EditDrinkScreen(
    onAddDrink: () -> Unit,
    onBackClick: () -> Unit,
    drinkToEditId: Int,
    viewModel: DrinkFormViewModel = hiltViewModel(),
    drinkViewModel: DrinkViewModel = hiltViewModel()
){



    val drinkUiState = drinkViewModel.drinkUiState.collectAsState()
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
