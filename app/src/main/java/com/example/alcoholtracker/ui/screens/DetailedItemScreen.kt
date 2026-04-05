package com.example.alcoholtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.DetailTopBar
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel

@Composable
fun DetailedItemScreen(
    logId: Int,
    onBackClick: () -> Unit,
    onEditClick: (UserDrinkLog) -> Unit,
    viewModel: UserAndUserDrinkLogViewModel = hiltViewModel()
) {


    val userDrink by viewModel.drinkById.collectAsState()

    LaunchedEffect(logId) {
        viewModel.getDrinkById(logId)
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            DetailTopBar(
                onEditClick = {
                    onEditClick(userDrink!!)
                },
                onDeleteClick = {
                    viewModel.deleteDrink(userDrink!!)
                    onBackClick()
                },
                onBackClick = { onBackClick() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("Detailed Item Screen")
            Text(userDrink?.name ?: "No Drink")
        }
    }
}