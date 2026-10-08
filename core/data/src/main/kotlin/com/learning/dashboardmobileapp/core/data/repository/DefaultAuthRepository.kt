package com.learning.dashboardmobileapp.core.data.repository

import com.learning.dashboardmobileapp.core.data.mapper.toDomainUser
import com.learning.dashboardmobileapp.core.data.session.SessionManager
import com.learning.dashboardmobileapp.core.domain.model.User
import com.learning.dashboardmobileapp.core.domain.repository.AuthRepository
import com.learning.dashboardmobileapp.core.domain.util.AppError
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.network.api.AuthApi
import com.learning.dashboardmobileapp.core.network.model.LoginRequestDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

class DefaultAuthRepository(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> = withContext(ioDispatcher) {
        try {
            val response = authApi.login(LoginRequestDto(email = email, password = password))
            val user = response.toDomainUser()
            sessionManager.saveSession(response.token, user)
            AppResult.Success(user)
        } catch (e: IOException) {
            AppResult.Error(AppError.Network(e.message ?: "Network error during login"))
        } catch (e: IllegalArgumentException) {
            AppResult.Error(AppError.InvalidCredentials)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e, e.message ?: "Authentication failed"))
        }
    }

    override suspend fun logout(): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            sessionManager.clearSession()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e, "Logout failed"))
        }
    }

    override fun observeIsLoggedIn(): Flow<Boolean> {
        return sessionManager.isLoggedIn
    }

    override suspend fun getLoggedInUser(): User? {
        return sessionManager.getCurrentUser()
    }
}
