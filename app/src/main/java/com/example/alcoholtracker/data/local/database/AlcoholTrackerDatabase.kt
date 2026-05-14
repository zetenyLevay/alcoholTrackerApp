package com.example.alcoholtracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alcoholtracker.data.local.dao.DrinkDao
import com.example.alcoholtracker.data.local.dao.DrinkLogDao
import com.example.alcoholtracker.data.local.dao.UserDao
import com.example.alcoholtracker.data.model.Drink
import com.example.alcoholtracker.data.model.User
import com.example.alcoholtracker.data.model.DrinkLog
import com.example.alcoholtracker.di.Converters


@Database(entities = [User::class, DrinkLog::class, Drink::class], version = 1)
@TypeConverters(Converters::class)
abstract class AlcoholTrackerDatabase : RoomDatabase() {

    abstract fun drinkLogDao(): DrinkLogDao
    abstract fun drinkDao(): DrinkDao
    abstract fun userDao(): UserDao


    companion object {
        @Volatile
        private var INSTANCE: AlcoholTrackerDatabase? = null

        fun getDatabase(context: Context): AlcoholTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AlcoholTrackerDatabase::class.java,
                    "alcohol_tracker_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build().also { INSTANCE = it }
            }
        }
    }
}