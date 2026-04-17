package com.example.alcoholtracker.ui.screens.drinkform


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.domain.usecase.adddrinkfuns.createNewRequest
import com.example.alcoholtracker.ui.viewmodel.DrinkEffect
import com.example.alcoholtracker.ui.viewmodel.DrinkEvent
import com.example.alcoholtracker.ui.viewmodel.DrinkFormEvents
import com.example.alcoholtracker.ui.viewmodel.DrinkViewModel
import com.example.alcoholtracker.ui.viewmodel.DrinkFormViewModel
import com.example.alcoholtracker.utils.LaunchedUiEffectHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@Composable
fun AddDrinkScreen(
    onAddDrink: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: DrinkFormViewModel = hiltViewModel(),
    drinkViewModel: DrinkViewModel = hiltViewModel()
) {


    val drinkUiState = drinkViewModel.drinkUiState.collectAsState()
    val formState = viewModel.formState.collectAsState()

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
            onAmountChange = {
                viewModel.updateSelectedAmount(
                    it,
                    formState.value.selectedDrinkUnit
                )
            },
            onABVChange = viewModel::updateABV,
            onPriceChange = viewModel::updatePrice,
            onRecipientChange = viewModel::updateRecipient,
            onDateChange = viewModel::updateSelectedDate,
            onTimeChange = viewModel::updateSelectedTime,
            onNotesChange = viewModel::updateNotes,
            onLocationChange = viewModel::updateLocation,
            onSaveDrink = {
                if (formState.value.drinkName.isNotBlank()) {
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
private fun EffectHandler(
    effectFlow: Flow<DrinkEffect?>,
    onEvent: (DrinkEvent) -> Unit,
    ) {
    LaunchedUiEffectHandler(
        effectFlow = effectFlow,
        onEffect = { effect ->
            when (effect){
                is DrinkEffect.ShowError -> {

                }
            }
        },
        onConsumeEffect = {
            onEvent(DrinkEvent.ConsumeEffect)
        }
    )
}


