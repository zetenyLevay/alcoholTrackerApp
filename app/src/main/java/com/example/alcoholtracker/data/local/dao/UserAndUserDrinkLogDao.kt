package com.example.alcoholtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.model.UserDrinkLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface UserAndUserDrinkLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrinkLog(log: UserDrinkLog)

    @Update
    suspend fun updateDrinkLog(log: UserDrinkLog)

    @Delete
    suspend fun deleteDrinkLog(log: UserDrinkLog)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM log WHERE userId = :userId")
    fun getDrinkLogsByUserId(userId: String): Flow<List<UserDrinkLog>>

    @Query("SELECT DISTINCT recipient FROM log WHERE userId = :userId")
    fun getRecipients(userId: String): Flow<List<String>>

    @Query("SELECT * FROM log WHERE logId = :logId")
    suspend fun getDrinkById(logId: Int): UserDrinkLog?
    @Query("SELECT * FROM log WHERE logId = :logId")
    fun getDrinkByIdFlow(logId: Int): Flow<UserDrinkLog?>

    @Query("SELECT * FROM log WHERE userId = :userId ORDER BY date DESC")
    fun getRecentLogs(userId: String): Flow<List<UserDrinkLog>>

    @Query(
        """
        SELECT *, COUNT(*) as frequency 
        FROM log 
        WHERE userId = :userId 
        GROUP BY name, category, alcoholPercentage, amount 
        ORDER BY frequency DESC
        """
    )
    fun getFrequentLogs(userId: String): Flow<List<UserDrinkLog>>

    @Query("Select * from log where userId = :userId and isFavorite = 1")
    fun getFavoritesLogs(userId: String): Flow<List<UserDrinkLog>>

    @Query(
        """
    SELECT * FROM log 
    WHERE userId = :userId 
    AND date(date) IN (date('now'), date('now', '-1 day'))
"""
    )
    fun getTwoDayLogs(userId: String): Flow<List<UserDrinkLog>>

    @Query("SELECT * FROM log WHERE date BETWEEN :start AND :end AND userId = :userId")
    fun getTonightLogs(userId: String, start: LocalDateTime, end: LocalDateTime): Flow<List<UserDrinkLog>>


}
