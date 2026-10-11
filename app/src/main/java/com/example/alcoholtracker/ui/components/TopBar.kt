@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.alcoholtracker.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.ui.components.detailitemcomponents.MinimalDropdownMenu


@Composable
fun HomeTopBar(isScrolled: Boolean = false) {
    TopAppBar(
        title = { TopBarTitle("Tonight") },
        colors = appTopBarColors(isScrolled),
    )
}

@Composable
fun FilterTopBar(
    onDismissRequest: () -> Unit,
    onResetClick: () -> Unit
){
    TopAppBar(
        colors = filterTopBarColors(),
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
fun AnalyticsTopBar(isScrolled: Boolean = false) {
    TopAppBar(
        title = { TopBarTitle("Analytics") },
        colors = appTopBarColors(isScrolled),
    )
}

@Composable
fun LogDrinkTopBar(
    onBackClick: () -> Unit,
    isEdit: Boolean,
    isScrolled: Boolean = false,
) {
    TopAppBar(
        colors = appTopBarColors(isScrolled),
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
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
fun ProfileTopBar(isScrolled: Boolean = false) {
    TopAppBar(
        title = { TopBarTitle("Profile") },
        colors = appTopBarColors(isScrolled),
    )
}

@Composable
fun SignUpTopBar(onBackClick: () -> Unit, isScrolled: Boolean = false) {
    TopAppBar(
        title = { TopBarTitle("Create account") },
        colors = appTopBarColors(isScrolled),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    )
}

@Composable
fun HistoryTopBar(isScrolled: Boolean = false){
    TopAppBar(
        title = { TopBarTitle("History") },
        colors = appTopBarColors(isScrolled),
    )
}

@Composable
private fun appTopBarColors(isScrolled: Boolean) = TopAppBarDefaults.topAppBarColors(
    containerColor = headerColor(isScrolled),
)

// The filter dialog's own container is surfaceContainer, so its bar matches it rather than the screen background
@Composable
private fun filterTopBarColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
)

// Header blends with the background at rest and takes the accent color once content scrolls under it
@Composable
fun headerColor(isScrolled: Boolean): Color {
    val color by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background,
        label = "HeaderColor"
    )
    return color
}

@Composable
private fun TopBarTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold)
}
