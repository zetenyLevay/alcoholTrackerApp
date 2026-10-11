package com.example.alcoholtracker.testutil

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.alcoholtracker.data.local.dao.DailySummary
import com.example.alcoholtracker.data.local.dao.DrinkDao
import com.example.alcoholtracker.data.local.dao.DrinkLogDao
import com.example.alcoholtracker.data.local.dao.FilterBounds
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.domain.model.DrinkCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

class FakeDrinkDao : DrinkDao {
    val drinks = mutableListOf<Drink>()
    var failReads = false
    override suspend fun getAllDrinks(): List<Drink> {
        if (failReads) throw IllegalStateException("read failed")
        return drinks.toList()
    }
    override suspend fun insertDrinks(drink: List<Drink>) { drinks += drink }
    override suspend fun insertDrink(drink: Drink) { drinks += drink }
}

data class PagedQuery(
    val userId: String,
    val query: String?,
    val category: String?,
    val recipient: String?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val minPrice: Float?,
    val maxPrice: Float?,
    val minAbv: Float?,
    val maxAbv: Float?,
    val isFavorite: Boolean?,
)

// Keeps logs in memory. List queries are simplified (newest first, no grouping); grouping is covered by the Room tests.
class FakeDrinkLogDao : DrinkLogDao {

    val logs = MutableStateFlow<List<DrinkLog>>(emptyList())
    val pagedQueries = mutableListOf<PagedQuery>()
    var failWrites = false
    var filterBounds: FilterBounds? = null
    private var nextId = 1

    private fun checkWrite() {
        if (failWrites) throw IllegalStateException("write failed")
    }

    private fun userLogs(userId: String): Flow<List<DrinkLog>> =
        logs.map { all -> all.filter { it.userId == userId }.sortedByDescending { it.date } }

    override suspend fun insertDrinkLog(log: DrinkLog): Long {
        checkWrite()
        val id = if (log.logId == 0) nextId++ else log.logId
        logs.update { all -> all.filterNot { it.logId == id } + log.copy(logId = id) }
        return id.toLong()
    }

    override suspend fun updateDrinkLog(log: DrinkLog) {
        checkWrite()
        logs.update { all -> all.map { if (it.logId == log.logId) log else it } }
    }

    override suspend fun deleteDrinkLog(log: DrinkLog) {
        checkWrite()
        logs.update { all -> all.filterNot { it.logId == log.logId } }
    }

    override suspend fun reassignLogs(oldUserId: String, newUserId: String) {
        checkWrite()
        logs.update { all -> all.map { if (it.userId == oldUserId) it.copy(userId = newUserId) else it } }
    }

    override fun getDrinkLogsByUserId(userId: String) = userLogs(userId)

    override fun getRecipients(userId: String): Flow<List<String>> =
        userLogs(userId).map { list -> list.mapNotNull { it.recipient }.distinct() }

    override suspend fun getDrinkById(logId: Int): DrinkLog? = logs.value.firstOrNull { it.logId == logId }

    override fun getDrinkByIdFlow(logId: Int): Flow<DrinkLog?> = logs.map { all -> all.firstOrNull { it.logId == logId } }

    override fun getRecentLogs(userId: String) = userLogs(userId)

    override fun getFilterBounds(userId: String): Flow<FilterBounds?> = MutableStateFlow(filterBounds)

    override fun getFrequentLogs(userId: String) = userLogs(userId)

    override fun getFavoritesLogs(userId: String): Flow<List<DrinkLog>> =
        userLogs(userId).map { list -> list.filter { it.isFavorite } }

    override suspend fun setFavoriteForGroup(
        userId: String,
        name: String,
        category: DrinkCategory,
        alcoholPercentage: Double?,
        amount: Int,
        cost: Double?,
        isFavorite: Boolean
    ) {
        checkWrite()
        logs.update { all ->
            all.map {
                val matches = it.userId == userId && it.name.equals(name, ignoreCase = true) &&
                        it.category == category && it.alcoholPercentage == alcoholPercentage &&
                        it.amount == amount && it.cost == cost
                if (matches) it.copy(isFavorite = isFavorite) else it
            }
        }
    }

    override fun getTonightLogs(userId: String, start: LocalDateTime, end: LocalDateTime): Flow<List<DrinkLog>> =
        userLogs(userId).map { list -> list.filter { it.date in start..end } }

    override fun getPagedLogs(
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
        isFavorite: Boolean?
    ): PagingSource<Int, DrinkLog> {
        pagedQueries += PagedQuery(userId, query, category, recipient, startDate, endDate, minPrice, maxPrice, minAbv, maxAbv, isFavorite)
        return ListPagingSource(logs.value.filter { it.userId == userId }.sortedByDescending { it.date })
    }

    override suspend fun getDailySummary(
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
    ): DailySummary {
        val day = logs.value.filter { it.userId == userId && it.date in startDate..endDate }
        return DailySummary(day.sumOf { it.cost ?: 0.0 }, day.sumOf { it.amount }.toDouble())
    }
}

class ListPagingSource(private val items: List<DrinkLog>) : PagingSource<Int, DrinkLog>() {
    override fun getRefreshKey(state: PagingState<Int, DrinkLog>): Int? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DrinkLog> =
        LoadResult.Page(data = items, prevKey = null, nextKey = null)
}
