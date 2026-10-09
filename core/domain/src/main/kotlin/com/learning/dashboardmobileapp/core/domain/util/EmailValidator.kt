package com.learning.dashboardmobileapp.core.domain.util

class EmailValidator {

    private val emailRegex = Regex(AuthConstants.EMAIL_REGEX_PATTERN)

    fun isValid(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.isNotEmpty() && emailRegex.matches(trimmed)
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= AuthConstants.MIN_PASSWORD_LENGTH
    }
}
