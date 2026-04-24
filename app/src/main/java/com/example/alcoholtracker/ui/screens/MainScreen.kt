package com.example.alcoholtracker.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.alcoholtracker.ObserverAsEvents
import com.example.alcoholtracker.SnackbarController
import com.example.alcoholtracker.ui.components.BottomNavigationBar
import com.example.alcoholtracker.ui.components.logComponents.LogNavBar
import com.example.alcoholtracker.ui.navigation.AddDrink
import com.example.alcoholtracker.ui.navigation.DetailedLog
import com.example.alcoholtracker.ui.navigation.Details
import com.example.alcoholtracker.ui.navigation.Home
import com.example.alcoholtracker.ui.navigation.List
import com.example.alcoholtracker.ui.navigation.Overview
import com.example.alcoholtracker.ui.navigation.Profile
import com.example.alcoholtracker.ui.navigation.Search
import com.example.alcoholtracker.ui.navigation.SignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MainScreen(

) {


    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackBarHostState = remember { SnackbarHostState() }

    val auth = FirebaseAuth.getInstance()


    val userState = remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            userState.value = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }


    val isSignedIn = userState.value != null


    val showBottomBar = remember(currentDestination) {
        currentDestination?.hierarchy?.any {
            it.hasRoute(Home::class) ||
                    it.hasRoute(List::class) ||
                    it.hasRoute(Overview::class) ||
                    it.hasRoute(Profile::class)
        } ?: false
    }

    val showLogBar = remember(currentDestination) {
        currentDestination?.hierarchy?.any {
            it.hasRoute(AddDrink::class) || it.hasRoute(Search::class)
        } ?: false
    }
    val scope = rememberCoroutineScope()

    ObserverAsEvents(flow = SnackbarController.events, snackBarHostState) { event ->
        scope.launch {
            snackBarHostState.currentSnackbarData?.dismiss()
            val result = snackBarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedContent(
                targetState = when {
                    showBottomBar -> 0
                    showLogBar -> 1
                    else -> -1
                },
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "BottomBarTransition"
            ) { state ->
                when (state) {
                    0 -> BottomNavigationBar(navController, currentDestination)
                    1 -> LogNavBar(navController, currentDestination)
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (isSignedIn) Home::class else SignIn::class,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
        {
            composable<Home> {
                HomeScreen(
                    onFABClick = {
                        navController.navigate(AddDrink())
                    },
                    onItemClick = {
                        navController.navigate(DetailedLog(it))
                    }
                )
            }
            composable<List> {
                ListScreen(
                    onFABClick = {
                        navController.navigate(AddDrink())
                    },
                    onEditClick = {

                        navController.navigate(AddDrink(it))
                    },
                    onItemClick = {
                        navController.navigate(DetailedLog(it))
                    }
                )
            }
            composable<AddDrink>() {

                DrinkFormScreen(
                    onAddDrink = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                )
            }
            composable<Profile> {
                ProfileScreen()
            }
            composable<Overview> {
                AnalyticsScreen()
            }
            composable<Details> {

            }
            composable<Search> {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable<DetailedLog> { backStackEntry ->
                val item: DetailedLog = backStackEntry.toRoute()
                DetailedLogScreen(
                    item.logId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate(AddDrink(it))
                    },
                )
            }
        }
    }
}