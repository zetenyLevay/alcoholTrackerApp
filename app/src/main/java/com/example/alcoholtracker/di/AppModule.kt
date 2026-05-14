package com.example.alcoholtracker.di

import android.content.Context
import com.example.alcoholtracker.data.local.dao.DrinkDao
import com.example.alcoholtracker.data.local.dao.DrinkLogDao
import com.example.alcoholtracker.data.local.dao.UserDao
import com.example.alcoholtracker.data.local.database.AlcoholTrackerDatabase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlcoholTrackerDatabase {
        return AlcoholTrackerDatabase.getDatabase(context)
    }

    @Provides
    fun provideDrinkDao(db: AlcoholTrackerDatabase): DrinkDao = db.drinkDao()

    @Provides
    fun provideDrinkLogDao(db: AlcoholTrackerDatabase): DrinkLogDao {
        return db.drinkLogDao()
    }

    fun provideUserDao(db: AlcoholTrackerDatabase): UserDao {
        return db.userDao()
    }


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
