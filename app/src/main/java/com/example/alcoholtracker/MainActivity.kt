package com.example.alcoholtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alcoholtracker.ui.screens.MainScreen
import com.example.alcoholtracker.ui.screens.SignInScreen
import com.example.compose.AlcoholTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import com.vamsi.snapnotify.SnapNotifyProvider
import dagger.hilt.android.AndroidEntryPoint


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


