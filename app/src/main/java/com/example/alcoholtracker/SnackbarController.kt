package com.example.alcoholtracker

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

enum class SnackbarType { SUCCESS, INFO, ERROR }

data class SnackBarEvent(
    val message: String,
    val action: SnackbarAction? = null,
    val type: SnackbarType = SnackbarType.INFO,
) {
    val durationMillis: Long
        get() = when {
            type == SnackbarType.ERROR -> 4000L
            action != null -> 4000L
            type == SnackbarType.SUCCESS -> 2500L
            else -> 3000L
        }
}

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
