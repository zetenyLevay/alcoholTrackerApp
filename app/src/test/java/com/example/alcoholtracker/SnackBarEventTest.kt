package com.example.alcoholtracker

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class SnackBarEventTest {

    private val undo = SnackbarAction("Undo") {}

    @Test
    fun duration_successIsShortest() {
        assertEquals(2500L, SnackBarEvent("Drink saved", type = SnackbarType.SUCCESS).durationMillis)
    }

    @Test
    fun duration_infoIsDefault() {
        assertEquals(SnackbarType.INFO, SnackBarEvent("Hi").type)
        assertEquals(3000L, SnackBarEvent("Hi").durationMillis)
    }

    @Test
    fun duration_actionsAndErrorsStayLonger() {
        assertEquals(4000L, SnackBarEvent("Error", type = SnackbarType.ERROR).durationMillis)
        assertEquals(4000L, SnackBarEvent("Drink removed", action = undo).durationMillis)
        assertEquals(4000L, SnackBarEvent("Logged", action = undo, type = SnackbarType.SUCCESS).durationMillis)
        assertEquals(4000L, SnackBarEvent("Failed", action = undo, type = SnackbarType.ERROR).durationMillis)
    }

    @Test
    fun durations_areAllShorterThanMaterialLong() {
        SnackbarType.entries.forEach { type ->
            listOf(null, undo).forEach { action ->
                assert(SnackBarEvent("m", action, type).durationMillis < 10_000L)
            }
        }
    }

    @Test
    fun controller_deliversEventsInOrder() = runBlocking {
        val first = SnackBarEvent("first")
        val second = SnackBarEvent("second", type = SnackbarType.ERROR)

        withTimeout(2_000) {
            val received = async { SnackbarController.events.take(2).toList() }
            SnackbarController.sendEvent(first)
            SnackbarController.sendEvent(second)
            assertEquals(listOf(first, second), received.await())
        }
    }

    @Test
    fun action_invokesCallback() {
        var invoked = false
        SnackbarAction("Undo") { invoked = true }.action()
        assert(invoked)
    }
}
