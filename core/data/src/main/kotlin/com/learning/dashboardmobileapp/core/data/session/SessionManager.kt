package com.learning.dashboardmobileapp.core.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learning.dashboardmobileapp.core.data.security.CryptoManager
import com.learning.dashboardmobileapp.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "user_session")

interface SessionManager {
    val isLoggedIn: Flow<Boolean>
    suspend fun saveSession(token: String, user: User)
    suspend fun clearSession()
    suspend fun getCurrentUser(): User?
    suspend fun getAuthToken(): String?
}

class DataStoreSessionManager(
    private val context: Context,
    private val cryptoManager: CryptoManager
) : SessionManager {

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
    }

    override val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }

    override suspend fun saveSession(token: String, user: User) {
        val encryptedToken = cryptoManager.encrypt(token)
        val encryptedEmail = cryptoManager.encrypt(user.email)
        val encryptedName = cryptoManager.encrypt(user.name)

        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_AUTH_TOKEN] = encryptedToken
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_USER_EMAIL] = encryptedEmail
            preferences[KEY_USER_NAME] = encryptedName
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun getCurrentUser(): User? {
        val preferences = context.dataStore.data.firstOrNull() ?: return null
        val id = preferences[KEY_USER_ID] ?: return null
        val rawEmail = preferences[KEY_USER_EMAIL] ?: return null
        val rawName = preferences[KEY_USER_NAME] ?: return null

        val email = cryptoManager.decrypt(rawEmail)
        val name = cryptoManager.decrypt(rawName)
        return User(id = id, email = email, name = name)
    }

    override suspend fun getAuthToken(): String? {
        val preferences = context.dataStore.data.firstOrNull() ?: return null
        val rawToken = preferences[KEY_AUTH_TOKEN] ?: return null
        return cryptoManager.decrypt(rawToken)
    }
}
