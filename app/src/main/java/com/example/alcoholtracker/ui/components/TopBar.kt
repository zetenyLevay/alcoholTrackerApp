@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.alcoholtracker.ui.components.detailitemcomponents.MinimalDropdownMenu
import com.example.alcoholtracker.utils.getFormattedDate
import java.time.LocalDate


@Composable
fun HomeTopBar(onCalendarClick: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrentDayText()
            }
        },
        actions = {
            IconButton(onClick = onCalendarClick) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
fun AnalyticsTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row {
                Text("Analytics")
            }
        },
        modifier = Modifier.statusBarsPadding()
    )
}

@Composable
fun CurrentDayText() {
    val todayDate = getFormattedDate(LocalDate.now())
    val yesterdayDate = getFormattedDate(LocalDate.now().plusDays(-1))
    Text(
        "$yesterdayDate - $todayDate",
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
fun LogDrinkTopBar(
    onBackClick: () -> Unit,
    isEdit: Boolean
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row {
                if (isEdit) {
                    Text("Edit Drink")
                } else {
                    Text("Log Drink")
                }

            }
        },
        modifier = Modifier.statusBarsPadding(),
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
fun DetailTopBar(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text("Drink Details")
        },
        modifier = Modifier.statusBarsPadding(),
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onFavoriteClick() }
            ) {
                if (isFavorite) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else{
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            MinimalDropdownMenu(
                onEditClick = { onEditClick() },
                onDeleteClick = { onDeleteClick() }
            )
        }
    )
}

