package com.example.alcoholtracker.data.repository

import android.util.Log
import com.example.alcoholtracker.data.local.dao.UserAndUserDrinkLogDao
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.model.UserDrinkLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList
import kotlin.math.log

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class DrinkLogRepository @Inject constructor(
    private val userAndUserDrinkLogDao: UserAndUserDrinkLogDao,
    private val userRepo: UserRepository
) {

    suspend fun insertDrinkLog(log: UserDrinkLog) {

        val user = userRepo.getCurrentUser()
        if (user == null) {
            throw Exception("User not found")
        }else{
            val completeLog = log.copy(userId = user)
            userAndUserDrinkLogDao.insertDrinkLog(completeLog)
        }
    }

    suspend fun updateDrinkLog(log: UserDrinkLog) {
        userAndUserDrinkLogDao.updateDrinkLog(log)
    }

    suspend fun deleteDrinkLog(log: UserDrinkLog) {
        userAndUserDrinkLogDao.deleteDrinkLog(log)
    }
    suspend fun getDrinkById(logId: Int): UserDrinkLog? {
        return userAndUserDrinkLogDao.getDrinkById(logId)
    }
    fun getRecipients(): Flow<List<String>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                userAndUserDrinkLogDao.getRecipients(userId)
            }
        }
    }


    fun getTwoDayLogs(): Flow<List<UserDrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                userAndUserDrinkLogDao.getTwoDayLogs(userId)
            }
        }
    }



    fun getRecentLogs(): Flow<List<UserDrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                userAndUserDrinkLogDao.getRecentLogs(userId)
            }
        }
    }

    fun getFrequentLogs(): Flow<List<UserDrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                userAndUserDrinkLogDao.getFrequentLogs(userId)
            }
        }
    }

    fun getFavoriteLogs(): Flow<List<UserDrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            }
            else{
                userAndUserDrinkLogDao.getFavoritesLogs(userId)
            }
        }
    }

    fun getTonightLogs(): Flow<List<UserDrinkLog>> {
        return userRepo.currentUser.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                val (start, end) = getCurrentSessionWindow()
                userAndUserDrinkLogDao.getTonightLogs(userId, start, end)
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
