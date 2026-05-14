package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.viewmodel.HistoryUiModel
import java.time.LocalDate


@Composable
fun AlcoholListHome(
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onRemove:(DrinkLog) -> Unit,
    drinkLogs: List<DrinkLog>,
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
    onRemove:(DrinkLog) -> Unit,
    drinkLogs: LazyPagingItems<HistoryUiModel>,
    modifier: Modifier = Modifier
) {

    val count = drinkLogs.itemCount

    Box(modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = drinkLogs.itemCount,
                key = drinkLogs.itemKey { item ->
                    when (item) {
                        is HistoryUiModel.DrinkItem -> item.log.logId
                        is HistoryUiModel.Header -> item.date.toString()
                    }
                },
                contentType = drinkLogs.itemContentType { item ->
                    when (item) {
                        is HistoryUiModel.DrinkItem -> "DrinkItem"
                        is HistoryUiModel.Header -> "Header"
                    }
                },
            ) { index ->



                when (val item = drinkLogs[index]) {
                    is HistoryUiModel.Header -> {
                        DateHeader(
                            item.date,
                            item.summary.totalAmount ?: 0.0,
                            item.summary.totalCost ?: 0.0,
                            modifier = Modifier.animateItem(
                                fadeOutSpec = tween(1000),
                                placementSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                fadeInSpec = tween(1000),
                            )
                        )
                    }
                    is HistoryUiModel.DrinkItem -> {


                        val previousItem = if (index > 0) drinkLogs.peek(index - 1) else null


                        val nextItem = if (index < drinkLogs.itemCount - 1) drinkLogs.peek(index + 1) else null

                        val isTop = previousItem is HistoryUiModel.Header || previousItem == null
                        val isBottom = nextItem is HistoryUiModel.Header || nextItem == null


                        val shape = when {
                            isTop && isBottom -> RoundedCornerShape(12.dp)
                            isTop -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            isBottom -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            else -> RectangleShape
                        }

                        SwipeToDismissItem(
                            item = item.log,
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
                        if (index < count-1) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        }
                    }
                    null -> {
                        CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
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
    onRemove:(DrinkLog) -> Unit,
    drinkLogs: List<DrinkLog>,
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