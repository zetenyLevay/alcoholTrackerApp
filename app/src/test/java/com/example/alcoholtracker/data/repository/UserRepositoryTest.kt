package com.example.alcoholtracker.data.repository

import com.example.alcoholtracker.data.model.Account
import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.testGuest
import com.example.alcoholtracker.testutil.testUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private val auth = FakeAuthService()
    private val repository = UserRepository(auth)

    @Test
    fun getCurrentUser_followsAuth() {
        assertNull(repository.getCurrentUser())
        auth.setUser(testUser)
        assertEquals("user", repository.getCurrentUser())
    }

    @Test
    fun currentUser_emitsOnEveryChange() = runTest {
        val emitted = mutableListOf<String?>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { repository.currentUser.toList(emitted) }

        auth.setUser(testGuest)
        auth.setUser(testUser)
        auth.signOut()
        job.cancel()

        assertEquals(listOf(null, "guest", "user", null), emitted)
    }

    @Test
    fun currentUser_removesListenerWhenCollectionStops() = runTest {
        val before = auth.listenerCount
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { repository.currentUser.collect {} }
        assertEquals(before + 1, auth.listenerCount)
        job.cancel()
        assertEquals(before, auth.listenerCount)
    }

    @Test
    fun account_reflectsGuestAndSignedInUsers() {
        assertNull(repository.account.value)
        auth.setUser(testGuest)
        assertEquals(Account(isGuest = true, email = null), repository.account.value)
        auth.setUser(testUser)
        assertEquals(Account(isGuest = false, email = "me@example.com"), repository.account.value)
    }

    @Test
    fun account_startsFromAlreadySignedInUser() {
        val repo = UserRepository(FakeAuthService(testUser))
        assertEquals(Account(isGuest = false, email = "me@example.com"), repo.account.value)
    }

    @Test
    fun signIn_successSwitchesUser() = runTest {
        auth.registerAccount("me@example.com", "secret1", uid = "account")
        assertTrue(repository.signIn("me@example.com", "secret1").isSuccess)
        assertEquals("account", repository.getCurrentUser())
    }

    @Test
    fun signIn_failureIsReturnedNotThrown() = runTest {
        val error = IllegalStateException("network")
        auth.failure = error
        val result = repository.signIn("me@example.com", "secret1")
        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun createAccount_and_signInAnonymously() = runTest {
        assertTrue(repository.signInAnonymously().isSuccess)
        assertEquals(true, repository.account.value?.isGuest)
        repository.signOut()
        assertTrue(repository.createAccount("new@example.com", "secret1").isSuccess)
        assertEquals(Account(isGuest = false, email = "new@example.com"), repository.account.value)
    }

    @Test
    fun linkAnonymousAccount_updatesAccountWithoutAuthEvent() = runTest {
        auth.setUser(testGuest)
        assertTrue(repository.linkAnonymousAccount("me@example.com", "secret1").isSuccess)
        assertEquals(Account(isGuest = false, email = "me@example.com"), repository.account.value)
        assertEquals("guest", repository.getCurrentUser())
    }

    @Test
    fun linkAnonymousAccount_whenSignedOutFails() = runTest {
        assertTrue(repository.linkAnonymousAccount("me@example.com", "secret1").exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun signOut_clearsAccount() {
        auth.setUser(testUser)
        repository.signOut()
        assertNull(repository.account.value)
        assertNull(repository.getCurrentUser())
    }
}
