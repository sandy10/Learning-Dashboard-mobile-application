package com.learning.dashboardmobileapp.core.domain.util

object AuthConstants {
    const val DEMO_EMAIL = "learner@example.com"
    const val DEMO_PASSWORD = "password123"
    const val ERROR_EMAIL_REQUIRED = "Email is required"
    const val ERROR_EMAIL_INVALID = "Please enter a valid email address"
    const val ERROR_PASSWORD_REQUIRED = "Password is required"
    const val ERROR_PASSWORD_LENGTH = "Password must be at least 6 characters"
    const val ERROR_INVALID_CREDENTIALS = "Invalid email or password. Use demo account hint: password123"
    const val ERROR_NETWORK_UNAVAILABLE = "Network unavailable. Please check your connection."
    const val ERROR_LOGIN_FAILED = "Login failed. Please try again."
    const val ERROR_NETWORK_LOGIN = "Network error during login"
    const val ERROR_AUTH_FAILED = "Authentication failed"
    const val ERROR_LOGOUT_FAILED = "Logout failed"
    const val EMAIL_REGEX_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    const val MIN_PASSWORD_LENGTH = 6
}

object CourseConstants {
    const val ERROR_FAILED_TO_CONNECT = "Failed to connect to server"
    const val ERROR_UNEXPECTED = "Unexpected error occurred"
    const val ERROR_FAILED_TO_UPDATE_LESSON = "Failed to update lesson"
    const val ERROR_COURSES_OFFLINE = "Offline: Showing cached courses"
    const val ERROR_COURSES_NETWORK = "Unable to load courses. Please check your network connection."
    const val ERROR_COURSE_ID_REQUIRED = "courseId must be provided as a navigation argument"
    const val MSG_LESSON_COMPLETED_SUCCESS = "Lesson marked as completed! Course progress updated."
    const val ERROR_COULD_NOT_UPDATE_LESSON = "Could not update lesson status"
    const val ERROR_NETWORK_COULD_NOT_REACH = "Network unavailable. Could not reach server."
    const val ERROR_NETWORK_CONNECTION = "Network connection error. Please check your internet."
    const val ERROR_COURSE_NOT_FOUND = "Course not found"
}
