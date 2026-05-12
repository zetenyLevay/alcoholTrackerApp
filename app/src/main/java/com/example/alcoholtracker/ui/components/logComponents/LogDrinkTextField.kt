package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.domain.notImplemented
import kotlin.math.round


@Composable
fun ABVAndPriceTextFields(
    abv: TextFieldState,
    price: TextFieldState,
) {


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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                state = abv,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                suffix = { Text("%") },
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val currentAbv = abv.text.toString().toDoubleOrNull() ?: 0.0
                                val doubleAbv = round((currentAbv + 0.1)*10)/10
                                abv.setTextAndPlaceCursorAtEnd(doubleAbv.toString())
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {
                                val currentAbv = abv.text.toString().toDoubleOrNull() ?: 0.0
                                if (currentAbv > 0.0) {
                                     val doubleAbv = round((currentAbv - 0.1)*10)/10
                                    abv.setTextAndPlaceCursorAtEnd(doubleAbv.toString())
                                }
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                state = price,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                suffix = { Text("€") },
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val currentPrice = price.text.toString().toDoubleOrNull() ?: 0.0
                                val doublePrice = round((currentPrice + 0.1)*10)/10
                                price.setTextAndPlaceCursorAtEnd(doublePrice.toString())
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {
                                val currentPrice = price.text.toString().toDoubleOrNull() ?: 0.0
                                if (currentPrice > 0.0) {
                                    val doublePrice = round((currentPrice - 0.1)*10)/10
                                price.setTextAndPlaceCursorAtEnd(doublePrice.toString())
                                }
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
                color = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.colorScheme.primary,
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
