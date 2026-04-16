package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholList
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel

@Composable
fun ListScreen(
    onFABClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    viewModel: UserAndUserDrinkLogViewModel = hiltViewModel()
) {
    val drinkLogs by viewModel.allDrinkLogs.collectAsState()

    ListScreen(
        onFABClick,
        onEditClick,
        onItemClick,
        onRemove = {viewModel.deleteDrink(it)},
        drinkLogs
    )

}

@Composable
fun ListScreen(
    onFABClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onRemove: (UserDrinkLog) -> Unit,
    drinkLogs: List<UserDrinkLog>
){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = { AddButton(onFABClick) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Surface(modifier = Modifier.padding(innerPadding)) {

            AlcoholList(
                listType =  AlcoholListType.FULL,
                onEditClick = { onEditClick(it) },
                onItemClick = { onItemClick(it) },
                onRemove = {onRemove(it)},
                drinkLogs = drinkLogs
            )
        }
    }
}
