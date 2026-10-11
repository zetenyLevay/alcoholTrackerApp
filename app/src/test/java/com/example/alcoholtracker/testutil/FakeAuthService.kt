package com.example.alcoholtracker.testutil

import com.example.alcoholtracker.data.auth.AuthService
import com.example.alcoholtracker.data.auth.AuthUser

class FakeAuthService(initialUser: AuthUser? = null) : AuthService {

    private val listeners = mutableListOf<(AuthUser?) -> Unit>()
    private val accounts = mutableMapOf<String, Pair<String, AuthUser>>()
    private var guestCount = 0

    var failure: Exception? = null

    override var currentUser: AuthUser? = initialUser
        private set

    val listenerCount: Int get() = listeners.size

    fun setUser(user: AuthUser?) {
        currentUser = user
        listeners.toList().forEach { it(user) }
    }

    fun registerAccount(email: String, password: String, uid: String = "uid-$email"): AuthUser {
        val user = AuthUser(uid = uid, isAnonymous = false, email = email)
        accounts[email] = password to user
        return user
    }

    override fun addAuthStateListener(listener: (AuthUser?) -> Unit): () -> Unit {
        listeners += listener
        listener(currentUser)
        return { listeners -= listener }
    }

    override suspend fun signIn(email: String, password: String) {
        failure?.let { throw it }
        val (storedPassword, user) = accounts[email] ?: throw IllegalArgumentException("No account for $email")
        require(storedPassword == password) { "Wrong password" }
        setUser(user)
    }

    override suspend fun createAccount(email: String, password: String) {
        failure?.let { throw it }
        setUser(registerAccount(email, password))
    }

    override suspend fun signInAnonymously() {
        failure?.let { throw it }
        setUser(AuthUser(uid = "guest-${++guestCount}", isAnonymous = true, email = null))
    }

    // Like Firebase, linking keeps the same user (and uid) and does not notify auth state listeners
    override suspend fun linkAnonymousAccount(email: String, password: String) {
        failure?.let { throw it }
        val user = currentUser ?: throw IllegalStateException("Not signed in")
        val linked = user.copy(isAnonymous = false, email = email)
        accounts[email] = password to linked
        currentUser = linked
    }

    override fun signOut() {
        setUser(null)
    }
}
