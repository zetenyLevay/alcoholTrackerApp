package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.alcoholtracker.ui.navigation.AddDrink
import com.example.alcoholtracker.ui.navigation.Search

data class LogNavRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)


@Composable
fun LogNavBar(navController: NavController, currentDestination: NavDestination?) {

    val topLevelRoutes = listOf(
        LogNavRoute("Search", Search, Icons.Default.Search),
        LogNavRoute("Custom", AddDrink, Icons.Default.Code),
    )


    NavigationBar {
        topLevelRoutes.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true,
                onClick = {
                    navController.navigate(destination.route) {
                        currentDestination?.id?.let { id ->
                            popUpTo(id) {
                                inclusive = true
                                saveState = true
                            }
                        }

                        launchSingleTop = true
                        restoreState = true
                    }

                },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = "Icon"
                    )
                },
                label = { Text(destination.name) }
            )
        }

    }
}