package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
    abv: Double,
    price: Double,
    defaultABV: Double,
    onABVChange: (Double) -> Unit,
    onPriceChange: (Double) -> Unit
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
                value = "$abv",
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() || it == '.' }
                    val parsed = filtered.toDoubleOrNull() ?: 0.0
                    if (parsed in 0.0..100.0 && filtered.length <= 5) {
                        onABVChange(parsed)
                    }
                },
                suffix = { Text("%") },
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val newAbv = round((abv + 0.1) * 10.0) / 10.0
                                onABVChange(newAbv)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {

                                if (abv > 0.0) {
                                    val newAbv = round((abv - 0.1) * 10.0) / 10.0
                                    onABVChange(newAbv)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
                value = "$price",
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() || it == '.' }
                    val parsed = filtered.toDoubleOrNull() ?: 0.0
                    onPriceChange(parsed)
                },
                suffix = { Text("€") },
                trailingIcon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val newPrice = round((price + 0.1) * 10.0) / 10.0
                                onPriceChange(newPrice)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                        IconButton(
                            onClick = {

                                if (price > 0.0) {
                                    val newPrice = round((price - 0.1) * 10.0) / 10.0
                                    onPriceChange(newPrice)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}

@Composable
fun LocationTextField(
    location: String,
    onLocationChange: (String) -> Unit
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
            value = location,
            onValueChange = {onLocationChange(it)},
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
    notes: String,
    onNotesChange: (String) -> Unit
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
            value = notes,
            onValueChange = {onNotesChange(it)},
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


