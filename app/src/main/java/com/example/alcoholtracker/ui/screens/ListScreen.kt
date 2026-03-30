package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListComposable

@Composable
fun ListScreen(
    onFABClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = { AddButton(onFABClick) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Surface(modifier = Modifier.padding(innerPadding)) {

            AlcoholListComposable(
                AlcoholListType.FULL,
                { onEditClick(it) },
                { onItemClick(it) }
            )
        }
    }
}
