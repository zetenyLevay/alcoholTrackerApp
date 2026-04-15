package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientAutoComplete(
    drinkToEditRecipient: String? = null,
    onAction: (String) -> Unit,
    userAndUserDrinkLogViewModel: UserAndUserDrinkLogViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {

    val userId = authViewModel.getUserID().value
    val options =
        userAndUserDrinkLogViewModel.getRecipients(userId!!).collectAsState(emptyList()).value
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(drinkToEditRecipient ?: "Me") }


    Column(
        modifier = Modifier.fillMaxWidth(),

        ) {
        Text(
            text = "Recipient",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {
                    selected = it
                    onAction(selected)
                },
                label = null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .clickable { expanded = true }
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                placeholder = { Text("Select a recipient") }
            )

            val filteringOptions =
                options.filter { it.contains(selected, ignoreCase = true) }
            if (filteringOptions.isNotEmpty()) {
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    filteringOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                selected = selectionOption
                                expanded = false
                                onAction(selectionOption)
                            },
                            text = { Text(text = selectionOption) }
                        )
                    }
                }
            }
        }
    }


}