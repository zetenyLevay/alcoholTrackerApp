@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.ui.components.detailitemcomponents.MinimalDropdownMenu


@Composable
fun HomeTopBar() {
    TopAppBar(
        title = { TopBarTitle("Tonight") },
        colors = appTopBarColors(),
    )
}

@Composable
fun FilterTopBar(
    onDismissRequest: () -> Unit,
    onResetClick: () -> Unit
){
    TopAppBar(
        colors = appTopBarColors(),
        navigationIcon = {
            IconButton(onClick = { onDismissRequest() }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                )
            }
        },
        title = {
            TopBarTitle("Filters")
        },
        actions = {
            TextButton(
                onClick = { onResetClick() },
                modifier = Modifier.padding(8.dp),
            ) {
                Text("Reset")
            }
        }
    )
}

@Composable
fun AnalyticsTopBar() {
    TopAppBar(
        title = { TopBarTitle("Analytics") },
        colors = appTopBarColors(),
    )
}

@Composable
fun LogDrinkTopBar(
    onBackClick: () -> Unit,
    isEdit: Boolean
) {
    TopAppBar(
        colors = appTopBarColors(),
        title = {
            TopBarTitle(if (isEdit) "Edit drink" else "Log drink")
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
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
    isFavorite: Boolean,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        colors = appTopBarColors(),
        title = {
            TopBarTitle("Drink details")
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions =
             {
                IconButton(
                    onClick = { onFavoriteClick() }
                ) {
                    if (isFavorite) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Remove from favorites",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    else{
                        Icon(
                            Icons.Filled.FavoriteBorder,
                            contentDescription = "Add to favorites",
                        )
                    }
                }
                MinimalDropdownMenu(
                    onEditClick = { onEditClick() },
                    onDeleteClick = { onDeleteClick() }
                )

            },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun HistoryTopBar(){
    TopAppBar(
        title = { TopBarTitle("History") },
        colors = appTopBarColors(),
    )
}

@Composable
private fun appTopBarColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
)

@Composable
private fun TopBarTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold)
}
