package com.example.alcoholtracker.ui.components.progressbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressBarEditDialog(
    currentType: ProgressBarType,
    currentTargets: Map<ProgressBarType, Double>,
    onDismiss: () -> Unit,
    onEvent: (ProgressBarType,Double) -> Unit ){


    val types by remember { mutableStateOf(ProgressBarType.entries.map{it.name}) }
    var selectedGoal by remember {  mutableStateOf(currentType.name)  }
    var isExpanded by remember { mutableStateOf(false) }
    var newTarget by remember { mutableStateOf("")}
    val currentTargetValue = currentTargets[ProgressBarType.valueOf(selectedGoal)]?.toString() ?: "0.0"


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                ) {
                    TextField(
                        value = selectedGoal.lowercase().replaceFirstChar { it.titlecase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Track") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    )
                    {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type.lowercase().replaceFirstChar { it.titlecase() }) },
                                onClick = {
                                    selectedGoal = type
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                TextField(value = newTarget,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$")))
                        { newTarget = it } } ,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = { Text("Goal") },
                    placeholder = { Text(currentTargetValue) },
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedType = ProgressBarType.valueOf(selectedGoal.uppercase())

                val safeTarget = newTarget.toDoubleOrNull() ?: currentTargets[selectedType] ?: 0.0

                onEvent(selectedType, safeTarget)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
