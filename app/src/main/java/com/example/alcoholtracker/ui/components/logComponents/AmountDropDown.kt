package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.domain.model.DrinkCategory
import com.example.alcoholtracker.domain.model.DrinkUnit
import com.example.alcoholtracker.ui.viewmodel.DrinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountDropDown(
    amount: Int,
    selectedUnit: DrinkUnit? = null,
    drinkCategory: DrinkCategory?,
    onSelected: (DrinkUnit) -> Unit,
    onTyped: (Int) -> Unit,
    viewModel: DrinkViewModel = hiltViewModel(),
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        var expanded by remember { mutableStateOf(false) }
        val options = viewModel.getDrinkUnitsForCategory(drinkCategory ?: DrinkCategory.OTHER)
        var previousUnit by remember { mutableStateOf<DrinkUnit?>(DrinkUnit("milliliters", 1)) }

        LaunchedEffect(selectedUnit) {
            val isCurrentUnitMilliliters = selectedUnit?.name == "milliliters"
            val wasPreviousUnitMilliliters = previousUnit?.name == "milliliters"

            when {
                !isCurrentUnitMilliliters && wasPreviousUnitMilliliters -> {
                    onTyped(1)
                }

                !wasPreviousUnitMilliliters && isCurrentUnitMilliliters -> {
                    onTyped(100)
                }

                else -> {

                }
            }
            previousUnit = selectedUnit

        }


        Column() {
            Row(  ) {
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 8.dp)
                        .weight(0.5F)
                )
                Text(
                    text = "Unit",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 8.dp)
                        .weight(1F)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = amount.toString(),
                    onValueChange = {
                        val newValue = it.toIntOrNull() ?: 0.0
                        onTyped(newValue.toInt())
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .weight(0.5f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    trailingIcon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    onTyped(amount + 1)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                            }
                            IconButton(
                                onClick = {
                                    if (amount > 0) {
                                        onTyped(amount - 1)
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Decrease"
                                )
                            }
                        }
                    },
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                ) {
                    OutlinedTextField(
                        value = selectedUnit?.name ?: "Select a unit",
                        onValueChange = { },
                        readOnly = true,
                        label = null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable)
                            .clickable {
                                expanded = true
                            },
                        shape = RoundedCornerShape(24.dp)

                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    onSelected(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
