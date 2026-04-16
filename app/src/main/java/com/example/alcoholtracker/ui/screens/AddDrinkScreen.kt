package com.example.alcoholtracker.ui.screens


import android.util.Log
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
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel
import com.vamsi.snapnotify.SnapNotify
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun AddDrinkScreen(
    onAddDrink: () -> Unit,
    onBackClick: () -> Unit,
    drinkToEditId: Int?,
    viewModel: UserAndUserDrinkLogViewModel = hiltViewModel(),

){



    LaunchedEffect(drinkToEditId) {
        if (drinkToEditId != null) {
            viewModel.getDrinkById(drinkToEditId)
        } else {

            viewModel.getDrinkById(null)
        }
    }
    val drinkToEdit by viewModel.drinkById.collectAsState()

    if (drinkToEditId != null && drinkToEdit == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {

        AddDrinkScreen(
            drinkToEdit = drinkToEdit,
            onBackClick = onBackClick,
            onSaveDrink = { request ->
                if (drinkToEditId != null) {
                    viewModel.updateDrink(drinkToEditId, request)
                } else {
                    viewModel.logDrink(request)
                }
                onAddDrink()
            }
        )
    }
}

@Composable
fun AddDrinkScreen(
    onBackClick: () -> Unit,
    onSaveDrink: (DrinkCreateRequest) -> Unit,
    drinkToEdit: UserDrinkLog?,
) {

    val isEdit = drinkToEdit != null
    val scrollState = rememberScrollState()

    var alcoholPercentage by remember { mutableDoubleStateOf(0.0) }
    var cost by remember { mutableDoubleStateOf(0.0) }
    var selectedCategory by remember { mutableStateOf<DrinkCategory?>(null) }
    var selectedDrink by remember { mutableStateOf<Drink?>(null) }
    var selectedDrinkUnit by remember { mutableStateOf<DrinkUnit?>(DrinkUnit("milliliters", 1)) }
    var selectedAmount by remember { mutableIntStateOf(500) }
    var typedDrinkName by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("Me") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var locationName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(drinkToEdit) {
        drinkToEdit?.let {
            alcoholPercentage = it.alcoholPercentage ?: 0.0
            cost = it.cost ?: 0.0
            selectedCategory = it.category
            selectedAmount = it.amount
            typedDrinkName = it.name
            recipient = it.recipient ?: "Me"
            selectedDate = it.date.toLocalDate()
            selectedTime = it.date.toLocalTime()
            locationName = it.locationName ?: ""
            notes = it.notes ?: ""
        }
    }

    Scaffold(
        topBar = {
            LogDrinkTopBar(
                onBackClick = { onBackClick() },
                isEdit = isEdit
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val request = DrinkCreateRequest(
                        name = selectedDrink?.name ?: typedDrinkName,
                        category = selectedCategory ?: DrinkCategory.OTHER,
                        abv = selectedDrink?.alcoholContent ?: alcoholPercentage,
                        volume = getFinalAmount(selectedDrinkUnit, selectedAmount.toDouble()),
                        cost = cost,
                        recipient = recipient,
                        inputAmount = selectedAmount.toDouble(),
                        drinkUnit = selectedDrinkUnit,
                        dateTime = getLocalDateTime(selectedDate, selectedTime),
                        logId = drinkToEdit?.logId,
                        isFavorite = drinkToEdit?.isFavorite ?: false,
                        imgURI = drinkToEdit?.imgURI,
                        notes = notes,
                        locationName = locationName,
                        longitude = drinkToEdit?.longitude,
                        latitude = drinkToEdit?.latitude
                    )
                    onSaveDrink(request)
                },
                icon = { Icon(Icons.Filled.Add, "Add Button") },
                text = {Text(if (isEdit) "Update Drink" else "Add Drink")}

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
                selected = selectedCategory,
                onSelected = {
                    tryOrNotify() {
                        selectedCategory = it
                    }
                },
            )

            DrinkAutoComplete(
                drinkToEditName = typedDrinkName,
                category = selectedCategory,
                onTyped = { typedDrinkName = it },
                onSelected = { selectedDrink = it },
            )

            AmountDropDown(
                amount = selectedAmount,
                selectedUnit = selectedDrinkUnit,
                drinkCategory = selectedCategory ?: DrinkCategory.OTHER,
                onSelected = { selectedDrinkUnit = it },
                onTyped = { selectedAmount = it },
            )

            ABVAndPriceTextFields(
                abv = selectedDrink?.alcoholContent ?: alcoholPercentage,
                price = cost,
                defaultABV = selectedDrink?.alcoholContent ?: 0.0,
                onABVChange = { alcoholPercentage = it },
                onPriceChange = { cost = it }
            )


            RecipientAutoComplete(
                drinkToEditRecipient = drinkToEdit?.recipient,
                onAction = { recipient = it },
            )

            DateAndTimePicker(
                currentDate = selectedDate,
                currentTime = selectedTime,
                onTimeSelected = { selectedTime = it },
                onDateSelected = {
                    selectedDate = it
                },
            )
            LocationTextField(
                location = locationName,
                onLocationChange = { locationName = it }
            )
            NotesTextField(
                notes = notes,
                onNotesChange = { notes = it }
            )
            Spacer(modifier = Modifier.padding(bottom = 16.dp))

        }
    }
}

@Composable
fun AddDrinkScreen(){

}

inline fun tryOrNotify(block: () -> Unit) {
    try {
        block()
    } catch (e: NotImplementedError) {
        notImplemented()
    }
}
