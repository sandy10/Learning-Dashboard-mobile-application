package com.learning.dashboardmobileapp.core.domain.util

class EmailValidator {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValid(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.isNotEmpty() && emailRegex.matches(trimmed)
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }
}
