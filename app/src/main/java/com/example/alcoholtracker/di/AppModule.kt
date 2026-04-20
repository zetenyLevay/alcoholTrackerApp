package com.example.alcoholtracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.alcoholtracker.data.local.dao.DrinkDao
import com.example.alcoholtracker.data.local.dao.UserAndUserDrinkLogDao
import com.example.alcoholtracker.data.local.database.DrinksDatabase
import com.example.alcoholtracker.data.local.database.UserAndUserDrinkLogDataBase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDrinkDao(db: DrinksDatabase): DrinkDao = db.drinkDao()

    @Provides
    @Singleton
    fun provideUserDrinkLogDatabase(@ApplicationContext context: Context): UserAndUserDrinkLogDataBase {
        return UserAndUserDrinkLogDataBase.getDatabase(context)
    }

    @Provides
    fun provideUserDrinkLogDao(db: UserAndUserDrinkLogDataBase): UserAndUserDrinkLogDao {
        return db.userAndUserDrinkLogDao()
    }

    @Provides
    @Singleton
    fun provideDrinksDatabase(@ApplicationContext context: Context): DrinksDatabase {
        return DrinksDatabase.getDatabase(context)
    }
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
