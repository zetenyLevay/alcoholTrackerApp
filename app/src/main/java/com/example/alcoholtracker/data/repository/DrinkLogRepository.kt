package com.example.alcoholtracker.data.repository

import com.example.alcoholtracker.data.local.dao.DrinkLogDao
import com.example.alcoholtracker.data.model.DrinkLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class DrinkLogRepository @Inject constructor(
    private val drinkLogDao: DrinkLogDao,
    private val userRepo: UserRepository
) {

    suspend fun insertDrinkLog(log: DrinkLog) {

        val user = userRepo.getCurrentUser()
        if (user == null) {
            throw Exception("User not found")
        }else{
            val completeLog = log.copy(userId = user)
            drinkLogDao.insertDrinkLog(completeLog)
        }
    }

    suspend fun updateDrinkLog(log: DrinkLog) {
        drinkLogDao.updateDrinkLog(log)
    }

    suspend fun deleteDrinkLog(log: DrinkLog) {
        drinkLogDao.deleteDrinkLog(log)
    }
    suspend fun getDrinkById(logId: Int): DrinkLog? {
        return drinkLogDao.getDrinkById(logId)
    }

    fun getDrinkByIdFlow(logId: Int): Flow<DrinkLog?> {
        return drinkLogDao.getDrinkByIdFlow(logId)
    }

    fun getAllLogs(): Flow<List<DrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                drinkLogDao.getDrinkLogsByUserId(userId)
            }
        }
    }

    fun getRecipients(): Flow<List<String>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                drinkLogDao.getRecipients(userId)
            }
        }
    }

    fun getRecentLogs(): Flow<List<DrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                drinkLogDao.getRecentLogs(userId)
            }
        }
    }

    fun getFrequentLogs(): Flow<List<DrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                drinkLogDao.getFrequentLogs(userId)
            }
        }
    }

    fun getFavoriteLogs(): Flow<List<DrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                drinkLogDao.getFavoritesLogs(userId)
            }
        }
    }

    fun getTonightLogs(): Flow<List<DrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                val (start, end) = getCurrentSessionWindow()
                drinkLogDao.getTonightLogs(userId, start, end)
            }
        }
    }

    fun getCurrentSessionWindow(): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()


        val sessionStart = if (now.hour < 6) {
            now.minusDays(1).withHour(6).withMinute(0)
        } else {
            now.withHour(6).withMinute(0)
        }

        val sessionEnd = sessionStart.plusDays(1).minusNanos(1)

        return Pair(sessionStart, sessionEnd)
    }

}
