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
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel


@Composable
fun AlcoholList(
    listType: AlcoholListType,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onRemove:(UserDrinkLog) -> Unit,
    drinkLogs: List<UserDrinkLog>,
) {


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {

            items(
                items = drinkLogs,
                key = { it.logId })
            { item ->
                SwipeToDismissItem(
                    item = item,
                    onRemove = { onRemove(it) },
                    listType = listType,
                    onEditClick = { onEditClick(it) },
                    onItemClick = { onItemClick(it) }
                )
            }
        }

    }
}