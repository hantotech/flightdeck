package com.example.flightdeck.utils

import android.util.Patterns

/**
 * Utility object for email validation
 */
object EmailValidator {

    /**
     * Validates an email address using Android's built-in pattern matcher
     *
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validates an email address using a custom regex pattern
     * This provides more strict validation than the Android built-in pattern
     *
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    fun isValidEmailStrict(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            return false
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Validates an email address and returns detailed error information
     *
     * @param email The email address to validate
     * @return ValidationResult containing validation status and error message if any
     */
    fun validateEmailWithDetails(email: String?): ValidationResult {
        return when {
            email.isNullOrBlank() -> ValidationResult(false, "Email cannot be empty")
            !email.contains("@") -> ValidationResult(false, "Email must contain @")
            email.indexOf("@") != email.lastIndexOf("@") -> ValidationResult(false, "Email cannot contain multiple @")
            !email.substringAfter("@").contains(".") -> ValidationResult(false, "Email domain must contain a period")
            email.startsWith("@") -> ValidationResult(false, "Email cannot start with @")
            email.endsWith("@") -> ValidationResult(false, "Email cannot end with @")
            email.substringBefore("@").isEmpty() -> ValidationResult(false, "Email must have a username before @")
            email.substringAfter("@").isEmpty() -> ValidationResult(false, "Email must have a domain after @")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(false, "Invalid email format")
            else -> ValidationResult(true, null)
        }
    }

    /**
     * Data class to hold validation result with optional error message
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String?
    )
}
