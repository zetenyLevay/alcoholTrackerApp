package com.example.alcoholtracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.alcoholtracker.ui.components.BottomNavigationBar
import com.example.alcoholtracker.ui.components.logComponents.LogNavBar
import com.example.alcoholtracker.ui.navigation.AddDrink
import com.example.alcoholtracker.ui.navigation.DetailedItem
import com.example.alcoholtracker.ui.navigation.Details
import com.example.alcoholtracker.ui.navigation.EditDrink
import com.example.alcoholtracker.ui.navigation.Home
import com.example.alcoholtracker.ui.navigation.List
import com.example.alcoholtracker.ui.navigation.Overview
import com.example.alcoholtracker.ui.navigation.Profile
import com.example.alcoholtracker.ui.navigation.Search
import com.example.alcoholtracker.ui.screens.drinkform.AddDrinkScreen
import com.example.alcoholtracker.ui.screens.AnalyticsScreen
import com.example.alcoholtracker.ui.screens.DetailedItemScreen
import com.example.alcoholtracker.ui.screens.drinkform.EditDrinkScreen
import com.example.alcoholtracker.ui.screens.HomeScreen
import com.example.alcoholtracker.ui.screens.ListScreen
import com.example.alcoholtracker.ui.screens.ProfileScreen
import com.example.alcoholtracker.ui.screens.SearchScreen
import com.example.alcoholtracker.ui.screens.SignInScreen
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.DrinkFormViewModel
import com.example.compose.AlcoholTrackerTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.vamsi.snapnotify.SnapNotifyProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SnapNotifyProvider {
                AlcoholTrackerTheme {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val userId by authViewModel.userId.collectAsState()


                    if (userId != null) {
                        MainScreen()
                    } else {
                        SignInScreen(
                            authViewModel,
                            onGuestLogin = { authViewModel.signInAnonymously() })
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("problem", "i am starting")
    }
}

@Composable
fun MainScreen() {

    val authViewModel: AuthViewModel = hiltViewModel()
    val drinkFormViewModel: DrinkFormViewModel = hiltViewModel()
    val userId by authViewModel.userId.collectAsState()

    LaunchedEffect(userId) {
        userId?.let { uid ->
            drinkFormViewModel.setUserId(uid)
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
        {
            composable<Home> {
                HomeScreen(
                    onFABClick = {
                        navController.navigate(AddDrink)
                    },
                    onItemClick = {
                        navController.navigate(DetailedItem(it))
                    }
                )
            }
            composable<List> {
                ListScreen(
                    onFABClick = {
                        navController.navigate(AddDrink)
                    },
                    onEditClick = {

                        navController.navigate(EditDrink(it))
                    },
                    onItemClick = {
                        navController.navigate(DetailedItem(it))
                    }
                )
            }
            composable<AddDrink>() {

                AddDrinkScreen(
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
            composable<EditDrink> { backStackEntry ->


                val item: EditDrink = backStackEntry.toRoute()
                EditDrinkScreen(
                    onAddDrink = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    drinkToEditId = item.logId

                )

            }
            composable<Search> {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
            composable<DetailedItem> { backStackEntry ->
                val item: DetailedItem = backStackEntry.toRoute()
                DetailedItemScreen(
                    item.logId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate(EditDrink(it.logId))
                    },
                )
            }
        }
    }
}
