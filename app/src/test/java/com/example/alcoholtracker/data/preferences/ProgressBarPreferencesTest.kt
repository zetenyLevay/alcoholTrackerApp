package com.example.alcoholtracker.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProgressBarPreferencesTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataStore by lazy {
        PreferenceDataStoreFactory.create(scope = scope) { folder.newFile("user_preferences.preferences_pb").also { it.delete() } }
    }
    private val preferences by lazy { ProgressBarPreferences(dataStore) }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun defaults() = runBlocking {
        assertEquals(ProgressBarType.MONEY, preferences.activeProgressBarType.first())
        assertEquals(50.0, preferences.moneyTarget.first(), 0.0)
        assertEquals(5.0, preferences.countTarget.first(), 0.0)
        assertEquals(1500.0, preferences.amountTarget.first(), 0.0)
    }

    @Test
    fun updates_areReadBack() = runBlocking {
        preferences.updateType(ProgressBarType.COUNT)
        preferences.updateMoneyTarget(40.0)
        preferences.updateCountTarget(3.0)
        preferences.updateAmountTarget(1000.0)

        assertEquals(ProgressBarType.COUNT, preferences.activeProgressBarType.first())
        assertEquals(40.0, preferences.moneyTarget.first(), 0.0)
        assertEquals(3.0, preferences.countTarget.first(), 0.0)
        assertEquals(1000.0, preferences.amountTarget.first(), 0.0)
    }

    @Test
    fun unknownStoredType_fallsBackToMoney() = runBlocking {
        dataStore.edit { it[ProgressBarPreferences.PROGRESS_BAR_TYPE] = "VIBES" }
        assertEquals(ProgressBarType.MONEY, preferences.activeProgressBarType.first())
    }
}
