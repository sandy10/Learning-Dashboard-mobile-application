package com.learning.dashboardmobileapp.core.domain.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidatorTest {

    private val validator = EmailValidator()

    @Test
    fun validEmails_returnTrue() {
        assertTrue(validator.isValid("learner@example.com"))
        assertTrue(validator.isValid("user.name+tag@domain.co.uk"))
        assertTrue(validator.isValid("student123@academy.org"))
    }

    @Test
    fun invalidEmails_returnFalse() {
        assertFalse(validator.isValid(""))
        assertFalse(validator.isValid("   "))
        assertFalse(validator.isValid("notanemail"))
        assertFalse(validator.isValid("@missingusername.com"))
        assertFalse(validator.isValid("missingdomain@"))
        assertFalse(validator.isValid("missingdot@com"))
    }

    @Test
    fun maliciousAndOversizedEmails_returnFalse() {
        // Script / HTML injection attempt
        assertFalse(validator.isValid("<script>alert('xss')</script>@example.com"))
        assertFalse(validator.isValid("attacker\"@example.com"))
        // Null byte injection attempt
        assertFalse(validator.isValid("learner\u0000@example.com"))
        // Oversized email (> 254 chars)
        val oversized = "a".repeat(250) + "@domain.com"
        assertFalse(validator.isValid(oversized))
    }

    @Test
    fun passwordValidation_requiresAtLeastSixCharacters() {
        assertFalse(validator.validatePassword(""))
        assertFalse(validator.validatePassword("12345"))
        assertTrue(validator.validatePassword("123456"))
        assertTrue(validator.validatePassword("password123"))
    }

    @Test
    fun maliciousAndOversizedPasswords_returnFalse() {
        // Null byte in password
        assertFalse(validator.validatePassword("pass\u0000word123"))
        // Control character in password
        assertFalse(validator.validatePassword("pass\u0007word123"))
        // Oversized password (> 128 chars)
        val oversizedPassword = "P".repeat(129)
        assertFalse(validator.validatePassword(oversizedPassword))
    }
}
