package com.example.alcoholtracker.di

import com.example.alcoholtracker.data.auth.AuthService
import com.example.alcoholtracker.data.auth.FirebaseAuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindAuthService(impl: FirebaseAuthService): AuthService
}
