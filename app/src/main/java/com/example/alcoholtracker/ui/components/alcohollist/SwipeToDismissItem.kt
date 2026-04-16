package com.example.alcoholtracker.ui.components.alcohollist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.components.DrinkItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SwipeToDismissItem(
    item: UserDrinkLog,
    onRemove: (UserDrinkLog) -> Unit,
    listType: AlcoholListType,
    onEditClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeToDismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if (state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    delay(500)
                    onRemove(item)
                }
                true
            } else false
        }
    )
    SwipeToDismissBox(
        state = swipeToDismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val progress = swipeToDismissState.progress

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFD32F2F)
                            ),
                            startX = 0f,
                            endX = 600f + (400f * progress)
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(12.dp)
                )
            }
        },
        modifier = Modifier.clickable(onClick = { onItemClick(item.logId) }) then modifier
    ) {
        DrinkItem(item, listType, { onEditClick(it) }, {})

    }
    HorizontalDivider()
}