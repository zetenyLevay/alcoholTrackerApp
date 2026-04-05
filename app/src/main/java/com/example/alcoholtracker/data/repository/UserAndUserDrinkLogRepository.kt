package com.example.alcoholtracker.data.repository

import android.util.Log
import com.example.alcoholtracker.data.local.dao.UserAndUserDrinkLogDao
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.model.UserDrinkLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserAndUserDrinkLogRepository @Inject constructor(private val userAndUserDrinkLogDao: UserAndUserDrinkLogDao) {

    suspend fun insertUser(user: User) {
        userAndUserDrinkLogDao.insertUser(user)
    }

    suspend fun insertDrinkLog(log: UserDrinkLog) {
        userAndUserDrinkLogDao.insertDrinkLog(log)
    }

    suspend fun updateDrinkLog(log: UserDrinkLog) {
        userAndUserDrinkLogDao.updateDrinkLog(log)
    }

    suspend fun deleteDrinkLog(log: UserDrinkLog) {
        userAndUserDrinkLogDao.deleteDrinkLog(log)
    }

    suspend fun getUserById(userId: String): User? {
        return userAndUserDrinkLogDao.getUserById(userId)
    }

    fun getRecipients(userId: String): Flow<List<String>> {
        return userAndUserDrinkLogDao.getRecipients(userId)
    }

    fun getDrinkLogsByUserId(userId: String): Flow<List<UserDrinkLog>> {
        Log.d("LogsByUserID", "Getting logs by userid logs for user: $userId")
        return userAndUserDrinkLogDao.getDrinkLogsByUserId(userId)

    }

    fun getTwoDayLogsByUser(userId: String): Flow<List<UserDrinkLog>> {
        return userAndUserDrinkLogDao.getTwoDayLogs(userId)
    }

    suspend fun getDrinkById(logId: Int): UserDrinkLog? {
        return userAndUserDrinkLogDao.getDrinkById(logId)
    }

    fun getRecentLogs(userId: String): Flow<List<UserDrinkLog>> {
        return userAndUserDrinkLogDao.getRecentLogs(userId)
    }

    fun getFrequentLogs(userId: String): Flow<List<UserDrinkLog>> {
        return userAndUserDrinkLogDao.getFrequentLogs(userId)
    }

    fun getFavoriteLogs(userId: String): Flow<List<UserDrinkLog>> {
        Log.d("FavoriteLogs", "Getting favorite logs for user: $userId")
        return userAndUserDrinkLogDao.getFavoritesLogs(userId)
    }

}
