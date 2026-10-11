package com.example.alcoholtracker.ui.components.logComponents

import com.example.alcoholtracker.utils.formatVolume
import com.example.alcoholtracker.utils.isMilliliters
import com.example.alcoholtracker.utils.DrinkFormNumbers
import com.example.alcoholtracker.utils.format
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.domain.model.DrinkUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountDropDown(
    amount: TextFieldState,
    selectedUnit: DrinkUnit? = null,
    onSelected: (DrinkUnit) -> Unit,
    options: List<DrinkUnit>,
) {

    var expanded by remember { mutableStateOf(false) }
    // Milliliters are whole numbers; cans/pints/bottles can be fractional (e.g. 2.5)
    val amountSpec = DrinkFormNumbers.amountFor(selectedUnit)
    val amountTransformation = remember(amountSpec) { NumberInputTransformation(amountSpec) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(0.5f)) {
            Text(
                text = "Amount",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                state = amount,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                lineLimits = TextFieldLineLimits.SingleLine,
                inputTransformation = amountTransformation,
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val currentAmount = amount.text.toString().toDoubleOrNull() ?: 0.0
                                amount.setTextAndPlaceCursorAtEnd(amountSpec.format(currentAmount + 1))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {
                                val currentAmount = amount.text.toString().toDoubleOrNull() ?: 0.0
                                amount.setTextAndPlaceCursorAtEnd(amountSpec.format(currentAmount - 1))
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
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Unit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedUnit?.name ?: "Select a unit",
                    onValueChange = { },
                    singleLine = true,
                    readOnly = true,
                    label = null,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .clickable {
                            expanded = true
                        },
                    shape = RoundedCornerShape(24.dp),
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            // Size comes from the unit's amount, so it always matches what gets saved
                            trailingIcon = if (option.isMilliliters) null else {
                                {
                                    Text(
                                        formatVolume(option.amount),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
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
