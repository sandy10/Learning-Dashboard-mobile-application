package com.learning.dashboardmobileapp.feature.auth

import app.cash.turbine.test
import com.learning.dashboardmobileapp.core.domain.model.User
import com.learning.dashboardmobileapp.core.domain.repository.AuthRepository
import com.learning.dashboardmobileapp.core.domain.usecase.LoginUseCase
import com.learning.dashboardmobileapp.core.domain.util.AppError
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.EmailValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val validator = EmailValidator()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        loginUseCase = LoginUseCase(fakeAuthRepository, validator)
        viewModel = LoginViewModel(loginUseCase, validator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialUiState_isCleanAndEmpty() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.errorMessage)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun onEmailChanged_updatesEmailAndClearsErrors() {
        viewModel.onAction(LoginUiAction.OnEmailChanged("test@example.com"))
        val state = viewModel.uiState.value
        assertEquals("test@example.com", state.email)
        assertNull(state.emailError)
    }

    @Test
    fun onAutoFillDemo_populatesValidCredentials() {
        viewModel.onAction(LoginUiAction.OnAutoFillDemo)
        val state = viewModel.uiState.value
        assertEquals("learner@example.com", state.email)
        assertEquals("password123", state.password)
        assertNull(state.emailError)
        assertNull(state.passwordError)
    }

    @Test
    fun loginWithEmptyCredentials_setsValidationErrorsAndDoesNotCallApi() = runTest {
        viewModel.onAction(LoginUiAction.OnLoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertEquals(false, state.isLoading)
        assertEquals(0, fakeAuthRepository.loginCallCount)
    }

    @Test
    fun loginWithInvalidEmail_setsEmailError() = runTest {
        viewModel.onAction(LoginUiAction.OnEmailChanged("not-an-email"))
        viewModel.onAction(LoginUiAction.OnPasswordChanged("password123"))
        viewModel.onAction(LoginUiAction.OnLoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Please enter a valid email address", state.emailError)
        assertNull(state.passwordError)
        assertEquals(0, fakeAuthRepository.loginCallCount)
    }

    @Test
    fun loginWithShortPassword_setsPasswordError() = runTest {
        viewModel.onAction(LoginUiAction.OnEmailChanged("learner@example.com"))
        viewModel.onAction(LoginUiAction.OnPasswordChanged("123"))
        viewModel.onAction(LoginUiAction.OnLoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.emailError)
        assertEquals("Password must be at least 6 characters", state.passwordError)
        assertEquals(0, fakeAuthRepository.loginCallCount)
    }

    @Test
    fun loginWithSuccessfulCredentials_emitsNavigateEvent() = runTest {
        fakeAuthRepository.loginResult = AppResult.Success(
            User(id = "1", email = "learner@example.com", name = "Learner")
        )

        viewModel.uiEvent.test {
            viewModel.onAction(LoginUiAction.OnEmailChanged("learner@example.com"))
            viewModel.onAction(LoginUiAction.OnPasswordChanged("password123"))
            viewModel.onAction(LoginUiAction.OnLoginClicked)

            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is LoginUiEvent.NavigateToDashboard)
        }

        assertEquals(1, fakeAuthRepository.loginCallCount)
    }

    @Test
    fun loginWithInvalidCredentials_setsErrorMessage() = runTest {
        fakeAuthRepository.loginResult = AppResult.Error(AppError.InvalidCredentials)

        viewModel.onAction(LoginUiAction.OnEmailChanged("learner@example.com"))
        viewModel.onAction(LoginUiAction.OnPasswordChanged("wrongpassword"))
        viewModel.onAction(LoginUiAction.OnLoginClicked)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertNotNull(state.errorMessage)
        assertTrue(state.errorMessage!!.contains("Invalid email or password"))
    }
}

class FakeAuthRepository : AuthRepository {
    var loginResult: AppResult<User> = AppResult.Success(
        User(id = "1", email = "learner@example.com", name = "Learner")
    )
    var loginCallCount = 0

    override suspend fun login(email: String, password: String): AppResult<User> {
        loginCallCount++
        return loginResult
    }

    override suspend fun logout(): AppResult<Unit> = AppResult.Success(Unit)

    override fun observeIsLoggedIn(): Flow<Boolean> = flowOf(true)

    override suspend fun getLoggedInUser(): User? = null
}
