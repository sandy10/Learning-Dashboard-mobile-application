package com.learning.dashboardmobileapp.core.data.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CryptoManagerTest {

    private lateinit var cryptoManager: CryptoManager

    @Before
    fun setUp() {
        cryptoManager = KeystoreCryptoManager(keyAlias = "test_auth_key")
    }

    @Test
    fun encryptAndDecrypt_returnsOriginalPlainText() {
        val originalToken = "mock-jwt-token-xyz-123456-secret-payload"
        val encrypted = cryptoManager.encrypt(originalToken)

        assertNotEquals(originalToken, encrypted)
        assertTrue(encrypted.isNotEmpty())

        val decrypted = cryptoManager.decrypt(encrypted)
        assertEquals(originalToken, decrypted)
    }

    @Test
    fun encryptSameTextMultipleTimes_producesDifferentCiphertextsDueToRandomIv() {
        val token = "sensitive-user-access-token"
        val encrypted1 = cryptoManager.encrypt(token)
        val encrypted2 = cryptoManager.encrypt(token)

        assertNotEquals(encrypted1, encrypted2)
        assertEquals(token, cryptoManager.decrypt(encrypted1))
        assertEquals(token, cryptoManager.decrypt(encrypted2))
    }

    @Test
    fun encryptEmptyString_returnsEmptyString() {
        val encrypted = cryptoManager.encrypt("")
        assertEquals("", encrypted)

        val decrypted = cryptoManager.decrypt("")
        assertEquals("", decrypted)
    }

    @Test
    fun decryptTamperedCiphertext_handlesGracefully() {
        val token = "sensitive-user-token"
        val encrypted = cryptoManager.encrypt(token)

        // Corrupt the ciphertext
        val tampered = encrypted.substring(0, encrypted.length - 4) + "XXXX"
        val decrypted = cryptoManager.decrypt(tampered)

        // Decryption fails or returns fallback string without crashing the app
        assertNotEquals(token, decrypted)
    }
}
