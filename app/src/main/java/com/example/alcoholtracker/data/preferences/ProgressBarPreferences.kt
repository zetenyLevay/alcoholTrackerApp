package com.example.alcoholtracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class ProgressBarPreferences @Inject constructor (
    private val dataStore: DataStore<Preferences>
){

    val activeProgressBarType: Flow<ProgressBarType> = dataStore.data
        .map { preferences ->
            val typeString = preferences[PROGRESS_BAR_TYPE] ?: ProgressBarType.MONEY.name
            try {
                ProgressBarType.valueOf(typeString)
            } catch (e: IllegalArgumentException) {
                ProgressBarType.MONEY
            }
        }


    val amountTarget: Flow<Double> = dataStore.data
        .map { preferences -> preferences[AMOUNT_PROGRESS_BAR_TARGET] ?: 1500.0 }

    val countTarget: Flow<Double> = dataStore.data
        .map { preferences -> preferences[COUNT_PROGRESS_BAR_TARGET] ?: 5.0 }

    val moneyTarget: Flow<Double> = dataStore.data
        .map { preferences -> preferences[MONEY_PROGRESS_BAR_TARGET] ?: 50.0 }

    suspend fun updateAmountTarget(target: Double) {
        dataStore.edit { it[AMOUNT_PROGRESS_BAR_TARGET] = target }
    }

    suspend fun updateCountTarget(target: Double) {
        dataStore.edit { it[COUNT_PROGRESS_BAR_TARGET] = target }
    }

    suspend fun updateMoneyTarget(target: Double) {
        dataStore.edit { it[MONEY_PROGRESS_BAR_TARGET] = target }
    }

    suspend fun updateType(type: ProgressBarType) {
        dataStore.edit { it[PROGRESS_BAR_TYPE] = type.name }
    }

    companion object {
        val PROGRESS_BAR_TYPE = stringPreferencesKey("progress_bar_type")
        val AMOUNT_PROGRESS_BAR_TARGET = doublePreferencesKey("amount_progress_bar_target")
        val COUNT_PROGRESS_BAR_TARGET = doublePreferencesKey("count_progress_bar_target")
        val MONEY_PROGRESS_BAR_TARGET = doublePreferencesKey("money_progress_bar_target")
    }
}