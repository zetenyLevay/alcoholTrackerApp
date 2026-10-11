package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.remember
import com.example.alcoholtracker.utils.DrinkFormNumbers
import com.example.alcoholtracker.utils.format
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.domain.notImplemented


@Composable
fun ABVAndPriceTextFields(
    abv: TextFieldState,
    price: TextFieldState,
) {
    val abvSpec = DrinkFormNumbers.ABV
    val priceSpec = DrinkFormNumbers.PRICE
    val abvTransformation = remember { NumberInputTransformation(abvSpec) }
    val priceTransformation = remember { NumberInputTransformation(priceSpec) }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = "ABV",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                state = abv,
                inputTransformation = abvTransformation,
                lineLimits = TextFieldLineLimits.SingleLine,
                suffix = { Text("%") },
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val currentAbv = abv.text.toString().toDoubleOrNull() ?: 0.0
                                abv.setTextAndPlaceCursorAtEnd(abvSpec.format(currentAbv + 0.1))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {
                                val currentAbv = abv.text.toString().toDoubleOrNull() ?: 0.0
                                abv.setTextAndPlaceCursorAtEnd(abvSpec.format(currentAbv - 0.1))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),

            )
        }

        Column(
            modifier = Modifier
                .weight(1F)
        ) {
            Text(
                text = "Price",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                state = price,
                inputTransformation = priceTransformation,
                lineLimits = TextFieldLineLimits.SingleLine,
                suffix = { Text("€") },
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val currentPrice = price.text.toString().toDoubleOrNull() ?: 0.0
                                price.setTextAndPlaceCursorAtEnd(priceSpec.format(currentPrice + 0.1))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {
                                val currentPrice = price.text.toString().toDoubleOrNull() ?: 0.0
                                price.setTextAndPlaceCursorAtEnd(priceSpec.format(currentPrice - 0.1))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),

            )
        }
    }
}

@Composable
fun LocationTextField(
    location: TextFieldState,
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
                text = "Location",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

        OutlinedTextField(
            state = location,
            colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        notImplemented()
                    }
                ) {
                    Icon(Icons.Default.LocationOff, contentDescription = "Location")
                }

            }
        )
    }
}

@Composable
fun NotesTextField(
    notes: TextFieldState,
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
                text = "Notes",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

        OutlinedTextField(
            state = notes,
            colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
        )
    }
}
