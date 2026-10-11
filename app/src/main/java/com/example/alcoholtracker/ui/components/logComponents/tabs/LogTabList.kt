package com.example.alcoholtracker.ui.components.logComponents.tabs

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.components.DrinkItem
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LogTabList(
    logs: List<DrinkLog>,
    emptyText: String,
    onLogClick: (DrinkLog) -> Unit,
    onLogAgainClick: (DrinkLog) -> Unit,
    onFavoriteToggle: (DrinkLog) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(logs, key = { it.groupKey() }) { log ->
                Column(modifier = Modifier.animateItem()) {
                    LogTabItem(
                        log = log,
                        onClick = { onLogClick(log) },
                        onLogAgainClick = { onLogAgainClick(log) },
                        onFavoriteToggle = { onFavoriteToggle(log) }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = logs.isEmpty(),
            enter = fadeIn(tween(durationMillis = 200, delayMillis = 200)),
            exit = fadeOut(tween(durationMillis = 100)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Text(
                emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


private fun DrinkLog.groupKey(): String = "$name|$category|$alcoholPercentage|$amount|$cost"

@Composable
private fun LogTabItem(
    log: DrinkLog,
    onClick: () -> Unit,
    onLogAgainClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DrinkItem(
            item = log,
            listType = AlcoholListType.LOG,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onFavoriteToggle) {
            Icon(
                if (log.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (log.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        LogAgainButton(onClick = onLogAgainClick)
    }
}

@Composable
private fun LogAgainButton(onClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    var showConfirmed by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            onClick()
            showConfirmed = true
            scope.launch {
                scale.animateTo(1.3f, tween(durationMillis = 90))
                scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
        }
    ) {
        Crossfade(
            targetState = showConfirmed,
            label = "LogAgainIcon",
            modifier = Modifier.scale(scale.value)
        ) { confirmed ->
            Icon(
                if (confirmed) Icons.Filled.Check else Icons.Filled.Add,
                contentDescription = if (confirmed) "Logged" else "Log again",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    LaunchedEffect(showConfirmed) {
        if (showConfirmed) {
            delay(1000.milliseconds)
            showConfirmed = false
        }
    }
}
