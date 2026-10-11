package com.example.alcoholtracker.data.repository

import com.example.alcoholtracker.data.auth.AuthService
import com.example.alcoholtracker.data.auth.AuthUser
import com.example.alcoholtracker.data.model.Account
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: AuthService,
){

    val currentUser: Flow<String?> = callbackFlow {
        val removeListener = auth.addAuthStateListener { user ->
            trySend(user?.uid)
        }
        awaitClose {
            removeListener()
        }
    }

    fun getCurrentUser(): String? = auth.currentUser?.uid

    private val _account = MutableStateFlow(auth.currentUser?.toAccount())
    val account: StateFlow<Account?> = _account.asStateFlow()

    init {
        auth.addAuthStateListener { _account.value = it?.toAccount() }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signIn(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAccount(email: String, password: String): Result<Unit> {
        return try {
            auth.createAccount(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun signInAnonymously(): Result<Unit> {
        return try {
            auth.signInAnonymously()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun linkAnonymousAccount(email: String, password: String): Result<Unit> {
        return try {
            auth.linkAnonymousAccount(email, password)
            _account.value = auth.currentUser?.toAccount()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun AuthUser.toAccount() = Account(isGuest = isAnonymous, email = email)
}
