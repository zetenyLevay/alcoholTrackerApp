package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun EditButton(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            Icons.Default.Edit,
            "Edit drink",
        )
    }
}