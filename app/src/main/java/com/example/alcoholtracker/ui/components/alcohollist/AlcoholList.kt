package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import java.time.LocalDate


@Composable
fun AlcoholListHome(
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onRemove:(UserDrinkLog) -> Unit,
    drinkLogs: List<UserDrinkLog>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            items(
                items = drinkLogs,
                key = { it.logId })
            { item ->
                SwipeToDismissItem(
                    item = item,
                    onRemove = { onRemove(it) },
                    listType = AlcoholListType.HOME,
                    onEditClick = { onEditClick(it) },
                    onItemClick = { onItemClick(it) },
                    modifier = Modifier.animateItem(fadeOutSpec = tween(500))
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlcoholListFull(
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onRemove:(UserDrinkLog) -> Unit,
    drinkLogs: Map<LocalDate,List<UserDrinkLog>>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            drinkLogs.forEach { (date, logs) ->
                stickyHeader {
                    DateHeader(date,logs)
                }
                items(
                    items = logs,
                    key = { it.logId }){
                    item ->
                        SwipeToDismissItem(
                            item = item,
                            onRemove = { onRemove(it) },
                            listType = AlcoholListType.FULL,
                            onEditClick = { onEditClick(it)
                            },
                            onItemClick = { onItemClick(it) },
                            modifier = Modifier.animateItem(fadeOutSpec = tween(500))
                        )
                    }
            }
        }
    }
}

@Composable
fun AlcoholListLog(
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onRemove:(UserDrinkLog) -> Unit,
    drinkLogs: List<UserDrinkLog>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            items(
                items = drinkLogs,
                key = { it.logId })
            { item ->
                SwipeToDismissItem(
                    item = item,
                    onRemove = { onRemove(it) },
                    listType = AlcoholListType.LOG,
                    onEditClick = { onEditClick(it) },
                    onItemClick = { onItemClick(it) },
                    modifier = Modifier.animateItem(fadeOutSpec = tween(500))
                )
            }
        }
    }
}