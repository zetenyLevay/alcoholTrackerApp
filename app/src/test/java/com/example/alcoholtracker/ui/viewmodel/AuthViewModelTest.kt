package com.example.alcoholtracker.ui.viewmodel

import com.example.alcoholtracker.data.model.Account
import com.example.alcoholtracker.data.repository.DrinkLogRepository
import com.example.alcoholtracker.data.repository.UserRepository
import com.example.alcoholtracker.testutil.FakeAuthService
import com.example.alcoholtracker.testutil.FakeDrinkLogDao
import com.example.alcoholtracker.testutil.MainDispatcherRule
import com.example.alcoholtracker.testutil.testGuest
import com.example.alcoholtracker.testutil.testLog
import com.example.alcoholtracker.testutil.testUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthService()
    private val dao = FakeDrinkLogDao()
    private val userRepo = UserRepository(auth)
    private val vm by lazy { AuthViewModel(userRepo, DrinkLogRepository(dao, userRepo)) }

    private fun fillIn(email: String = "me@example.com", password: String = "secret1") {
        vm.processEvent(UserEvents.OnEmailChange(email))
        vm.processEvent(UserEvents.OnPasswordChange(password))
    }

    private fun signInFailsWith(error: Exception): String? {
        auth.failure = error
        fillIn()
        vm.processEvent(UserEvents.SignIn)
        return vm.uiState.value.errorMessage
    }

    @Test
    fun typing_updatesInputsAndClearsError() = runTest {
        signInFailsWith(IllegalStateException("boom"))
        vm.processEvent(UserEvents.OnEmailChange("a@b.c"))
        assertEquals("a@b.c", vm.uiState.value.emailInput)
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun signIn_successNavigatesHome() = runTest {
        auth.registerAccount("me@example.com", "secret1")
        fillIn(email = "  me@example.com ")
        vm.processEvent(UserEvents.SignIn)

        assertEquals(UserEffect.NavigateToHome, vm.uiState.value.effect)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(Account(isGuest = false, email = "me@example.com"), vm.account.value)
    }

    @Test
    fun signIn_asGuest_movesGuestDrinksToAccount() = runTest {
        auth.setUser(testGuest)
        dao.insertDrinkLog(testLog(name = "Guest beer", userId = "guest"))
        auth.registerAccount("me@example.com", "secret1", uid = "account")
        fillIn()

        vm.processEvent(UserEvents.SignIn)

        assertEquals(listOf("account"), dao.logs.value.map { it.userId })
    }

    @Test
    fun signIn_fromAnotherAccount_doesNotMoveDrinks() = runTest {
        auth.setUser(testUser)
        dao.insertDrinkLog(testLog(userId = "user"))
        auth.registerAccount("other@example.com", "secret1", uid = "other")
        fillIn(email = "other@example.com")

        vm.processEvent(UserEvents.SignIn)

        assertEquals(listOf("user"), dao.logs.value.map { it.userId })
    }

    @Test
    fun signIn_failedAsGuest_keepsGuestDrinks() = runTest {
        auth.setUser(testGuest)
        dao.insertDrinkLog(testLog(userId = "guest"))
        signInFailsWith(IllegalStateException("boom"))
        assertEquals(listOf("guest"), dao.logs.value.map { it.userId })
        assertNull(vm.uiState.value.effect)
    }

    @Test
    fun errorMessages_areFriendly() = runTest {
        assertEquals("Enter a valid email address", signInFailsWith(FirebaseAuthInvalidCredentialsException("ERROR_INVALID_EMAIL", "x")))
        assertEquals("Wrong email or password", signInFailsWith(FirebaseAuthInvalidCredentialsException("ERROR_WRONG_PASSWORD", "x")))
        assertEquals("No account found with this email", signInFailsWith(FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "x")))
        assertEquals("An account with this email already exists", signInFailsWith(FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREADY_IN_USE", "x")))
        assertEquals("Use a password with at least 6 characters", signInFailsWith(FirebaseAuthWeakPasswordException("ERROR_WEAK_PASSWORD", "x", "too short")))
        assertEquals("No connection. Try again when you're online", signInFailsWith(FirebaseNetworkException("offline")))
        assertEquals("boom", signInFailsWith(IllegalStateException("boom")))
        assertEquals("Something went wrong", signInFailsWith(IllegalStateException()))
    }

    @Test
    fun signUp_asGuest_linksAndKeepsDrinks() = runTest {
        auth.setUser(testGuest)
        dao.insertDrinkLog(testLog(userId = "guest"))
        fillIn()

        vm.processEvent(UserEvents.SignUp)

        assertEquals(UserEffect.AccountCreated, vm.uiState.value.effect)
        assertEquals(Account(isGuest = false, email = "me@example.com"), vm.account.value)
        assertEquals("guest", userRepo.getCurrentUser())
        assertEquals(listOf("guest"), dao.logs.value.map { it.userId })
    }

    @Test
    fun signUp_whenSignedOut_createsAccount() = runTest {
        fillIn()
        vm.processEvent(UserEvents.SignUp)
        assertEquals(UserEffect.AccountCreated, vm.uiState.value.effect)
        assertEquals(Account(isGuest = false, email = "me@example.com"), vm.account.value)
    }

    @Test
    fun signUp_failureShowsMessage() = runTest {
        auth.failure = FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREADY_IN_USE", "x")
        fillIn()
        vm.processEvent(UserEvents.SignUp)
        assertEquals("An account with this email already exists", vm.uiState.value.errorMessage)
        assertNull(vm.uiState.value.effect)
    }

    @Test
    fun continueAsGuest_signsInAnonymously() = runTest {
        vm.processEvent(UserEvents.AnonymousSignIn)
        assertEquals(UserEffect.NavigateToHome, vm.uiState.value.effect)
        assertEquals(true, vm.account.value?.isGuest)
    }

    @Test
    fun signOut_clearsAccount() = runTest {
        auth.setUser(testUser)
        vm.processEvent(UserEvents.SignOut)
        assertNull(vm.account.value)
    }

    @Test
    fun consumeEffect_clearsIt() = runTest {
        vm.processEvent(UserEvents.AnonymousSignIn)
        vm.processEvent(UserEvents.ConsumeEffect)
        assertNull(vm.uiState.value.effect)
    }
}
