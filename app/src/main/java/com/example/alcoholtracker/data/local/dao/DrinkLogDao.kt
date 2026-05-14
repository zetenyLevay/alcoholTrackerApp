package com.example.alcoholtracker.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import com.example.alcoholtracker.data.model.DrinkLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
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

    @Query("""
    SELECT 
        MIN(cost) as minPrice, 
        MAX(cost) as maxPrice, 
        MIN(alcoholPercentage) as minAbv, 
        MAX(alcoholPercentage) as maxAbv 
    FROM log 
    WHERE userId = :userId
    """)
    fun getFilterBounds(userId: String): Flow<FilterBounds?>

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


    @Query("""
        SELECT * FROM log 
        WHERE userId = :userId 
        
        -- 1. General Search (Matches Drink Name or Notes)
        AND (:query IS NULL OR name LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%')
        
        -- 2. Category Search
        AND (:category IS NULL OR category LIKE '%' || :category || '%')
        
        -- 3. Recipient Search
        AND (:recipient IS NULL OR recipient LIKE '%' || :recipient || '%')
        
        -- 4. Date Range 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        
        -- 5. Price Range
        AND (:minPrice IS NULL OR cost >= :minPrice)
        AND (:maxPrice IS NULL OR cost <= :maxPrice)
        
        -- 6. ABV Range
        AND (:minAbv IS NULL OR alcoholPercentage >= :minAbv)
        AND (:maxAbv IS NULL OR alcoholPercentage <= :maxAbv)
        
        -- 7. Favorites Toggle
        AND (:isFavorite IS NULL OR isFavorite = :isFavorite)
        
        ORDER BY date DESC
    """)
    fun getPagedLogs(
        userId: String,
        query: String?,
        category: String?,
        recipient: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        minPrice: Float?,
        maxPrice: Float?,
        minAbv: Float?,
        maxAbv: Float?,
        isFavorite: Boolean?): PagingSource<Int, DrinkLog>
    @Query("""
    SELECT 
        SUM(cost) as totalCost, 
        SUM(amount) as totalAmount 
    FROM log 
    WHERE userId = :userId 
    

    AND date >= :startDate AND date <= :endDate
    AND (:query IS NULL OR name LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%')
    AND (:category IS NULL OR category LIKE '%' || :category || '%')
    AND (:recipient IS NULL OR recipient LIKE '%' || :recipient || '%')
    AND (:minPrice IS NULL OR cost >= :minPrice)
    AND (:maxPrice IS NULL OR cost <= :maxPrice)
    AND (:minAbv IS NULL OR alcoholPercentage >= :minAbv)
    AND (:maxAbv IS NULL OR alcoholPercentage <= :maxAbv)
    AND (:isFavorite IS NULL OR isFavorite = :isFavorite)
""")
    suspend fun getDailySummary(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        query: String?,
        category: String?,
        recipient: String?,
        minPrice: Float?,
        maxPrice: Float?,
        minAbv: Float?,
        maxAbv: Float?,
        isFavorite: Boolean?
    ): DailySummary


}

data class FilterBounds(
    val minPrice: Float?,
    val maxPrice: Float?,
    val minAbv: Float?,
    val maxAbv: Float?
)

data class DailySummary(
    val totalCost: Double?,
    val totalAmount: Double? // Or total standard drinks, depending on your logic
)

