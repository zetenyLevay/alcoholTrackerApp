package com.example.alcoholtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.alcoholtracker.data.model.DrinkLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface DrinkLogDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrinkLog(log: DrinkLog)

    @Update
    suspend fun updateDrinkLog(log: DrinkLog)

    @Delete
    suspend fun deleteDrinkLog(log: DrinkLog)

    @Query("SELECT * FROM log WHERE userId = :userId")
    fun getDrinkLogsByUserId(userId: String): Flow<List<DrinkLog>>

    @Query("SELECT DISTINCT recipient FROM log WHERE userId = :userId")
    fun getRecipients(userId: String): Flow<List<String>>

    @Query("SELECT * FROM log WHERE logId = :logId")
    suspend fun getDrinkById(logId: Int): DrinkLog?
    @Query("SELECT * FROM log WHERE logId = :logId")
    fun getDrinkByIdFlow(logId: Int): Flow<DrinkLog?>

    @Query("SELECT * FROM log WHERE userId = :userId ORDER BY date DESC")
    fun getRecentLogs(userId: String): Flow<List<DrinkLog>>

    @Query(
        """
        SELECT *, COUNT(*) as frequency 
        FROM log 
        WHERE userId = :userId 
        GROUP BY name, category, alcoholPercentage, amount 
        ORDER BY frequency DESC
        """
    )
    fun getFrequentLogs(userId: String): Flow<List<DrinkLog>>

    @Query("Select * from log where userId = :userId and isFavorite = 1")
    fun getFavoritesLogs(userId: String): Flow<List<DrinkLog>>

    @Query("SELECT * FROM log WHERE date BETWEEN :start AND :end AND userId = :userId")
    fun getTonightLogs(userId: String, start: LocalDateTime, end: LocalDateTime): Flow<List<DrinkLog>>


}
