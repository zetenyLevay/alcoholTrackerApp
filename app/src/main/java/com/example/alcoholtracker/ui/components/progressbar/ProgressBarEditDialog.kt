package com.example.alcoholtracker.ui.components.progressbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.alcoholtracker.ui.viewmodel.HomeEvent


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


    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
    {
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
            shape = RoundedCornerShape(16.dp),)
        {
            Column(modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .zIndex(1f)
            ) {
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {

                    Text(text = "Change Goal")

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onDismiss,
                        modifier = Modifier
                    )
                    {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart)
                    .zIndex(2f)) {
                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = {isExpanded = it},
                        modifier = Modifier
                            .heightIn(max = 100.dp)
                            .widthIn(max = 200.dp)
                            .zIndex(3f))
                    {
                        TextField(
                            value = selectedGoal.lowercase().replaceFirstChar { it.titlecase() },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)},
                            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = newTarget,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$")))
                        { newTarget = it } } ,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = { Text("Goal") },
                    placeholder = { Text(currentTargetValue)})

                Button(onClick = {
                    val selectedType = ProgressBarType.valueOf(selectedGoal.uppercase())

                    val safeTarget = newTarget.toDoubleOrNull() ?: currentTargets[selectedType] ?: 0.0

                    onEvent(selectedType, safeTarget)
                    onDismiss()
                }) {
                    Text("Save")
                }

            }
        }
    }
}
