package com.example.alcoholtracker.data.auth

data class AuthUser(
    val uid: String,
    val isAnonymous: Boolean,
    val email: String?,
)

interface AuthService {
    val currentUser: AuthUser?

    // Called right away with the current user, then on every sign-in/sign-out. Returns a function that unregisters it.
    fun addAuthStateListener(listener: (AuthUser?) -> Unit): () -> Unit

    suspend fun signIn(email: String, password: String)
    suspend fun createAccount(email: String, password: String)
    suspend fun signInAnonymously()
    suspend fun linkAnonymousAccount(email: String, password: String)
    fun signOut()
}
