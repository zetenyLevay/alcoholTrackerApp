package com.example.alcoholtracker.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object List

@Serializable
object Profile

@Serializable
object SignIn

@Serializable
object SignUp

@Serializable
data class EditDrink(val logId: Int)

@Serializable
object AddDrink


@Serializable
object Search

@Serializable
object Overview

@Serializable
object Details

@Serializable
object Finance

@Serializable
object Health

@Serializable
data class DetailedItem(val logId: Int)