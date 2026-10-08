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
    fun passwordValidation_requiresAtLeastSixCharacters() {
        assertFalse(validator.validatePassword(""))
        assertFalse(validator.validatePassword("12345"))
        assertTrue(validator.validatePassword("123456"))
        assertTrue(validator.validatePassword("password123"))
    }
}
