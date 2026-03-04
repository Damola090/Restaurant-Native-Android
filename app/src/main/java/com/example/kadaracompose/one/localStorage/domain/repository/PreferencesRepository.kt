package com.example.kadaracompose.one.localStorage.domain.repository

import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * DataStore is also Flow-based — same reactive pattern as Room.
 * The difference: DataStore is for simple key-value preferences,
 * not structured relational data.
 */
interface PreferencesRepository {
    fun getPreferences(): Flow<UserPreferences>
    suspend fun setDisplayName(name: String)
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setFontSize(size: FontSize)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun clearAllPreferences()
}
