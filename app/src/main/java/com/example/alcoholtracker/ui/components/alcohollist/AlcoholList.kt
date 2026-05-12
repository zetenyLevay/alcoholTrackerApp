package com.example.alcoholtracker.ui.components.alcohollist

import android.R.attr.clipToPadding
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
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
    Box(modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        if (drinkLogs.isEmpty()){
            Text("Nothing to show...")
        }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                drinkLogs.forEach { (date, logs) ->
                    stickyHeader(key = date) {

                        DateHeader(
                            date,
                            logs,
                            modifier = Modifier.animateItem(
                                fadeOutSpec = tween(1000),
                                placementSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                fadeInSpec = tween(1000),
                            )
                        )
                    }
                    itemsIndexed(
                        items = logs,
                        key = { _, item -> item.logId }) { index, item ->

                        val shape = when {
                            logs.size == 1 -> RoundedCornerShape(12.dp)
                            index == 0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            index == logs.lastIndex -> RoundedCornerShape(
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp
                            )

                            else -> RectangleShape
                        }
                        SwipeToDismissItem(
                            item = item,
                            onRemove = { onRemove(it) },
                            listType = AlcoholListType.FULL,
                            onEditClick = {
                                onEditClick(it)
                            },
                            onItemClick = { onItemClick(it) },
                            modifier = Modifier.animateItem(
                                fadeOutSpec = tween(1000),
                                placementSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                fadeInSpec = tween(1000),
                            ).clip(shape)
                        )

                        if (index < logs.lastIndex) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        }

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