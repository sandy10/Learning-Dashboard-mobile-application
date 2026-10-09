package com.learning.dashboardmobileapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.dashboardmobileapp.core.domain.usecase.LoginUseCase
import com.learning.dashboardmobileapp.core.domain.util.AppError
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.AuthConstants
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
                        email = AuthConstants.DEMO_EMAIL,
                        password = AuthConstants.DEMO_PASSWORD,
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
            emailErr = AuthConstants.ERROR_EMAIL_REQUIRED
            hasError = true
        } else if (!emailValidator.isValid(email)) {
            emailErr = AuthConstants.ERROR_EMAIL_INVALID
            hasError = true
        }

        if (password.isEmpty()) {
            passwordErr = AuthConstants.ERROR_PASSWORD_REQUIRED
            hasError = true
        } else if (!emailValidator.validatePassword(password)) {
            passwordErr = AuthConstants.ERROR_PASSWORD_LENGTH
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
                        is AppError.InvalidCredentials -> AuthConstants.ERROR_INVALID_CREDENTIALS
                        is AppError.Network -> AuthConstants.ERROR_NETWORK_UNAVAILABLE
                        is AppError.Validation -> error.message
                        else -> AuthConstants.ERROR_LOGIN_FAILED
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
