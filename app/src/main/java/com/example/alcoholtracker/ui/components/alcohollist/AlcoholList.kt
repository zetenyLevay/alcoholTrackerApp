package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel


@Composable
fun AlcoholListComposable(
    listType: AlcoholListType,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    userDrinkLogViewModel: UserAndUserDrinkLogViewModel = hiltViewModel(),
) {

    val userId by authViewModel.getUserID()
    val drinkLogs by userDrinkLogViewModel.getDrinkLogs(userId!!).collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {

            items(
                items = drinkLogs,
                key = { it.logId })
            { item ->
                SwipeToDismissItem(
                    item = item,
                    onRemove = { userDrinkLogViewModel.deleteDrink(item) },
                    modifier = Modifier.animateItem(tween(200)),
                    listType = listType,
                    onEditClick = { onEditClick(it) },
                    onItemClick = { onItemClick(it) }
                )
            }
        }

    }
}