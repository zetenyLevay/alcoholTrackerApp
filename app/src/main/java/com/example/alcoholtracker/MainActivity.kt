package com.example.alcoholtracker

import android.os.Bundle
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
import com.example.alcoholtracker.data.repository.UserRepository
import com.example.alcoholtracker.ui.components.BottomNavigationBar
import com.example.alcoholtracker.ui.components.logComponents.LogNavBar
import com.example.alcoholtracker.ui.navigation.AddDrink
import com.example.alcoholtracker.ui.navigation.DetailedItem
import com.example.alcoholtracker.ui.navigation.Details
import com.example.alcoholtracker.ui.navigation.Home
import com.example.alcoholtracker.ui.navigation.List
import com.example.alcoholtracker.ui.navigation.Overview
import com.example.alcoholtracker.ui.navigation.Profile
import com.example.alcoholtracker.ui.navigation.Search
import com.example.alcoholtracker.ui.screens.DrinkFormScreen
import com.example.alcoholtracker.ui.screens.AnalyticsScreen
import com.example.alcoholtracker.ui.screens.DetailedItemScreen
import com.example.alcoholtracker.ui.screens.HomeScreen
import com.example.alcoholtracker.ui.screens.ListScreen
import com.example.alcoholtracker.ui.screens.MainScreen
import com.example.alcoholtracker.ui.screens.ProfileScreen
import com.example.alcoholtracker.ui.screens.SearchScreen
import com.example.alcoholtracker.ui.screens.SignInScreen
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.UserViewModel
import com.example.compose.AlcoholTrackerTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.vamsi.snapnotify.SnapNotifyProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnapNotifyProvider {
                AlcoholTrackerTheme {

                    val auth = FirebaseAuth.getInstance()
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        MainScreen()
                    } else {
                        SignInScreen(
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}


