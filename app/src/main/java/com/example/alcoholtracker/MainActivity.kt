package com.example.alcoholtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alcoholtracker.ui.screens.MainScreen
import com.example.compose.AlcoholTrackerTheme
import com.vamsi.snapnotify.SnapNotifyProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnapNotifyProvider {
                AlcoholTrackerTheme {
                    MainScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}


