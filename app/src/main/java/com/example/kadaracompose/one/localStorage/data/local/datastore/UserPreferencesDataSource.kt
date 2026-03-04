package com.example.kadaracompose.one.localStorage.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore is the modern replacement for SharedPreferences.
 *
 * Key differences vs Room:
 * - No SQL, no tables — just typed key-value pairs
 * - Backed by a file, not a database
 * - Ideal for small amounts of preference data
 * - Also Flow-based, so it's reactive like Room
 *
 * Key differences vs SharedPreferences:
 * - Fully coroutines-based (no blocking reads)
 * - Type-safe keys using preferencesKey<T>()
 * - Handles write failures gracefully
 * - No apply()/commit() confusion
 */

// Extension property creates one DataStore instance per Context (per app)
private val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val context: Context
) {
    // ── Keys ─────────────────────────────────────────────────────────────────
    // Each key is typed — stringPreferencesKey, booleanPreferencesKey, etc.
    // This is the type safety DataStore gives you over SharedPreferences.
    companion object {
        val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_FONT_SIZE = stringPreferencesKey("font_size")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Maps raw Preferences → typed UserPreferences domain model.
     * The catch block handles IOException from disk read failures gracefully
     * by emitting default preferences instead of crashing.
     */
    val preferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences()) // fall back to defaults on read error
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserPreferences(
                displayName = prefs[KEY_DISPLAY_NAME] ?: "",
                isDarkTheme = prefs[KEY_DARK_THEME] ?: false,
                fontSize = FontSize.valueOf(prefs[KEY_FONT_SIZE] ?: FontSize.MEDIUM.name),
                notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true
            )
        }

    // ── Write ─────────────────────────────────────────────────────────────────

    /**
     * edit() is a suspend function that runs in a transaction.
     * All writes inside the block are atomic — either all succeed or none do.
     */
    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DISPLAY_NAME] = name
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = enabled
        }
    }

    suspend fun setFontSize(size: FontSize) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FONT_SIZE] = size.name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
