package com.learning.dashboardmobileapp.core.data.security

/**
 * Contract for encrypting and decrypting sensitive credentials and tokens
 * using hardware-backed or secure cryptographic algorithms.
 */
interface CryptoManager {
    /**
     * Encrypts plain text string into a Base64-encoded ciphertext with authentication tag.
     */
    fun encrypt(plainText: String): String

    /**
     * Decrypts Base64-encoded ciphertext back into original plain text.
     */
    fun decrypt(cipherText: String): String
}
