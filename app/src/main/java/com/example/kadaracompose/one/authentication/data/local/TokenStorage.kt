package com.example.kadaracompose.one.authentication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.kadaracompose.one.authentication.domain.model.AuthTokens
import com.example.kadaracompose.one.authentication.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "auth_tokens")

/**
 * Persists JWT tokens and user info using DataStore.
 *
 * Why DataStore over SharedPreferences?
 * - Coroutine-safe — no blocking reads
 * - Atomic writes — no partial state corruption
 * - Type-safe keys
 *
 * Note: For production apps with high security requirements,
 * consider EncryptedSharedPreferences or the Android Keystore.
 * DataStore is sufficient for most apps and much easier to work with.
 */
@Singleton
class TokenStorage @Inject constructor(
    private val context: Context
) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_EXPIRES_AT = longPreferencesKey("expires_at")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_display_name")
        private val KEY_USER_AVATAR = stringPreferencesKey("user_avatar_url")
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    val tokens: Flow<AuthTokens?> = context.authDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val accessToken = prefs[KEY_ACCESS_TOKEN] ?: return@map null
            val refreshToken = prefs[KEY_REFRESH_TOKEN] ?: return@map null
            val expiresAt = prefs[KEY_EXPIRES_AT] ?: return@map null
            AuthTokens(accessToken, refreshToken, expiresAt)
        }

    val user: Flow<AuthUser?> = context.authDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val id = prefs[KEY_USER_ID] ?: return@map null
            val email = prefs[KEY_USER_EMAIL] ?: return@map null
            val name = prefs[KEY_USER_NAME] ?: return@map null
            AuthUser(
                id = id,
                email = email,
                displayName = name,
                avatarUrl = prefs[KEY_USER_AVATAR]
            )
        }

    // ── Write ─────────────────────────────────────────────────────────────────

    suspend fun saveTokens(tokens: AuthTokens) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = tokens.accessToken
            prefs[KEY_REFRESH_TOKEN] = tokens.refreshToken
            prefs[KEY_EXPIRES_AT] = tokens.accessTokenExpiresAt
        }
    }

    suspend fun saveUser(user: AuthUser) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_USER_NAME] = user.displayName
            user.avatarUrl?.let { prefs[KEY_USER_AVATAR] = it }
        }
    }

    suspend fun updateAccessToken(accessToken: String, expiresAt: Long) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_EXPIRES_AT] = expiresAt
        }
    }

    /** Called on logout — wipes everything */
    suspend fun clearAll() {
        context.authDataStore.edit { it.clear() }
    }
}
