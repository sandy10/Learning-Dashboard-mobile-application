package com.learning.dashboardmobileapp.core.domain.repository

import com.learning.dashboardmobileapp.core.domain.model.User
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun logout(): AppResult<Unit>
    fun observeIsLoggedIn(): Flow<Boolean>
    suspend fun getLoggedInUser(): User?
}
