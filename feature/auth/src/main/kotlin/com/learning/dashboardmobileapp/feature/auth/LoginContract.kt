package com.learning.dashboardmobileapp.feature.auth

sealed interface LoginUiAction {
    data class OnEmailChanged(val email: String) : LoginUiAction
    data class OnPasswordChanged(val password: String) : LoginUiAction
    data object OnTogglePasswordVisibility : LoginUiAction
    data object OnLoginClicked : LoginUiAction
    data object OnAutoFillDemo : LoginUiAction
    data object OnDismissError : LoginUiAction
}

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginUiEvent {
    data object NavigateToDashboard : LoginUiEvent
    data class ShowSnackbar(val message: String) : LoginUiEvent
}
