package com.learning.dashboardmobileapp.core.domain.usecase

import com.learning.dashboardmobileapp.core.domain.model.User
import com.learning.dashboardmobileapp.core.domain.repository.AuthRepository
import com.learning.dashboardmobileapp.core.domain.util.AppError
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.EmailValidator
import kotlinx.coroutines.flow.Flow

class LoginUseCase(
    private val repository: AuthRepository,
    private val validator: EmailValidator
) {
    suspend operator fun invoke(email: String, password: String): AppResult<User> {
        val trimmedEmail = email.trim()
        if (!validator.isValid(trimmedEmail)) {
            return AppResult.Error(AppError.Validation("Please enter a valid email address"))
        }
        if (!validator.validatePassword(password)) {
            return AppResult.Error(AppError.Validation("Password must be at least 6 characters"))
        }
        return repository.login(trimmedEmail, password)
    }
}

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = repository.logout()
}

class ObserveAuthStateUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeIsLoggedIn()
}
