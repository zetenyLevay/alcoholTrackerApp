package com.example.alcoholtracker.data.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthService {

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    override fun addAuthStateListener(listener: (AuthUser?) -> Unit): () -> Unit {
        val firebaseListener = FirebaseAuth.AuthStateListener { listener(it.currentUser?.toAuthUser()) }
        auth.addAuthStateListener(firebaseListener)
        return { auth.removeAuthStateListener(firebaseListener) }
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInAnonymously() {
        auth.signInAnonymously().await()
    }

    override suspend fun linkAnonymousAccount(email: String, password: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        user.linkWithCredential(EmailAuthProvider.getCredential(email, password)).await()
    }

    override fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.toAuthUser() = AuthUser(uid = uid, isAnonymous = isAnonymous, email = email)
}
