package com.example.alcoholtracker

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackBarEvent(
    val message: String,
    val action: SnackbarAction? = null,
)

data class SnackbarAction(
    val name: String,
    val action: () -> Unit,
)

object SnackbarController {

    private val _events = Channel<SnackBarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackBarEvent) {
        _events.send(event)
    }

}