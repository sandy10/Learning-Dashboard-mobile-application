package com.learning.dashboardmobileapp.core.domain.util

/**
 * Validates and sanitizes authentication inputs against injection attacks,
 * oversized payloads, control characters, and malformed formats.
 */
class EmailValidator {

    private val emailRegex = Regex(AuthConstants.EMAIL_REGEX_PATTERN)

    fun isValid(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isEmpty() || trimmed.length > AuthConstants.MAX_EMAIL_LENGTH) {
            return false
        }
        // Reject null bytes, control characters, or script/injection tags
        if (trimmed.any { it.isISOControl() } || trimmed.contains("<") || trimmed.contains(">") || trimmed.contains("\"")) {
            return false
        }
        return emailRegex.matches(trimmed)
    }

    fun validatePassword(password: String): Boolean {
        if (password.length < AuthConstants.MIN_PASSWORD_LENGTH || password.length > AuthConstants.MAX_PASSWORD_LENGTH) {
            return false
        }
        // Reject control characters and null bytes
        if (password.any { it.isISOControl() }) {
            return false
        }
        return true
    }
}
