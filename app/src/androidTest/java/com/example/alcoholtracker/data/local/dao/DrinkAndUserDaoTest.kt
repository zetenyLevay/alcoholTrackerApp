package com.example.alcoholtracker.data.local.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.alcoholtracker.data.local.database.AlcoholTrackerDatabase
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrinkAndUserDaoTest {

    private lateinit var db: AlcoholTrackerDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AlcoholTrackerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun drinks_insertAndReadBack() = runBlocking {
        val dao = db.drinkDao()
        dao.insertDrink(Drink(name = "Heineken", alcoholContent = 5.0, category = "Beer"))
        dao.insertDrinks(listOf(
            Drink(name = "Red Wine", alcoholContent = 13.0, category = "Wine"),
            Drink(name = "Vodka", alcoholContent = 40.0, category = "Spirit"),
        ))

        assertEquals(setOf("Heineken", "Red Wine", "Vodka"), dao.getAllDrinks().map { it.name }.toSet())
    }

    @Test
    fun drinks_replaceOnSameId() = runBlocking {
        val dao = db.drinkDao()
        dao.insertDrink(Drink(drinkId = 1, name = "Old", alcoholContent = 5.0, category = "Beer"))
        dao.insertDrink(Drink(drinkId = 1, name = "New", alcoholContent = 5.0, category = "Beer"))

        assertEquals(listOf("New"), dao.getAllDrinks().map { it.name })
    }

    @Test
    fun users_insertReplaceAndLookup() = runBlocking {
        val dao = db.userDao()
        dao.insertUser(User("uid", "Zé", "old@example.com"))
        dao.insertUser(User("uid", "Zé", "new@example.com"))

        assertEquals("new@example.com", dao.getUserById("uid")?.userEmail)
        assertNull(dao.getUserById("missing"))
    }
}
