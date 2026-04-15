package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePicker(
    currentDate: LocalDate?,
    currentTime: LocalTime?,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
) {


    var showDateDialog by remember {
        mutableStateOf(false)
    }
    var showTimeDialog by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState()

    LaunchedEffect(currentDate) {
        currentDate?.let { date ->
            datePickerState.selectedDateMillis = date.atStartOfDay(java.time.ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }
    }

    val formattedDate = remember(currentDate) {
        currentDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: ""
    }

    val formattedTime = remember(currentTime) {
        currentTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
    }

    Column() {
        Row(  ) {
            Text(
                text = "Time",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier

                    .padding(start = 8.dp, bottom = 8.dp)
                    .weight(0.5F)
            )
            Text(
                text = "Date",
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
                value = formattedTime,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                suffix = {
                    Icon(Icons.Default.Timer, contentDescription = "Time")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
                label = null,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(0.5f)
                    .clickable(
                        onClick = {
                            showTimeDialog = true
                        }
                    )
                )


            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
                suffix = { Icon(Icons.Default.CalendarMonth, contentDescription = "Date") },
                label = null,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            showDateDialog = true
                        }
                    )

            )

            if (showDateDialog) {

                DatePickerDialog(
                    onDismissRequest = { showDateDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val localDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.of("UTC")) // Match the timezone used in LaunchedEffect
                                    .toLocalDate()
                                onDateSelected(localDate)
                            }
                            showDateDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateDialog = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            if (showTimeDialog) {

                val timeState = rememberTimePickerState(
                    initialHour = currentTime?.hour ?: 0,
                    initialMinute = currentTime?.minute ?: 0,
                    is24Hour = true
                )

                TimePickerDialog(
                    onDismissRequest = { showTimeDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val selectedTime = LocalTime.of(timeState.hour, timeState.minute)
                            onTimeSelected(selectedTime)
                            showTimeDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimeDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("") }
                ) {
                    TimePicker(state = timeState)

                }

            }
        }
    }
}
