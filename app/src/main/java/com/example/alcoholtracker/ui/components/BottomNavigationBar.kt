package com.example.alcoholtracker.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.alcoholtracker.ui.navigation.Home
import com.example.alcoholtracker.ui.navigation.List
import com.example.alcoholtracker.ui.navigation.Overview
import com.example.alcoholtracker.ui.navigation.Profile

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)


@Composable
fun BottomNavigationBar(navController: NavController, currentDestination: NavDestination?) {

    val topLevelRoutes = listOf(
        TopLevelRoute("Home", Home, Icons.Default.Home),
        TopLevelRoute("List", List, Icons.Default.FormatListNumbered),
        TopLevelRoute("Analytics", Overview, Icons.Default.Analytics),
        TopLevelRoute("Profile", Profile, Icons.Default.Person)

    )

    NavigationBar {
        topLevelRoutes.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
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
