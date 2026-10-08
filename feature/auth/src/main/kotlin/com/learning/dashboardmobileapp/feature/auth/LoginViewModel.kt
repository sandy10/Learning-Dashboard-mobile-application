package com.learning.dashboardmobileapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.dashboardmobileapp.core.domain.usecase.LoginUseCase
import com.learning.dashboardmobileapp.core.domain.util.AppError
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.EmailValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val emailValidator: EmailValidator
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = action.email,
                        emailError = null,
                        errorMessage = null
                    )
                }
            }

            is LoginUiAction.OnPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = action.password,
                        passwordError = null,
                        errorMessage = null
                    )
                }
            }

            LoginUiAction.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            LoginUiAction.OnAutoFillDemo -> {
                _uiState.update {
                    it.copy(
                        email = "learner@example.com",
                        password = "password123",
                        emailError = null,
                        passwordError = null,
                        errorMessage = null
                    )
                }
            }

            LoginUiAction.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            LoginUiAction.OnLoginClicked -> {
                performLogin()
            }
        }
    }

    private fun performLogin() {
        val currentState = _uiState.value
        val email = currentState.email.trim()
        val password = currentState.password

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isEmpty()) {
            emailErr = "Email is required"
            hasError = true
        } else if (!emailValidator.isValid(email)) {
            emailErr = "Please enter a valid email address"
            hasError = true
        }

        if (password.isEmpty()) {
            passwordErr = "Password is required"
            hasError = true
        } else if (!emailValidator.validatePassword(password)) {
            passwordErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = loginUseCase(email = email, password = password)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(LoginUiEvent.NavigateToDashboard)
                }

                is AppResult.Error -> {
                    val errorMsg = when (val error = result.error) {
                        is AppError.InvalidCredentials -> "Invalid email or password. Use demo account hint: password123"
                        is AppError.Network -> "Network unavailable. Please check your connection."
                        is AppError.Validation -> error.message
                        else -> "Login failed. Please try again."
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            }
        }
    }
}
