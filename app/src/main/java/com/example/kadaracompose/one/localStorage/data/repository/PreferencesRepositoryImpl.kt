package com.example.kadaracompose.one.localStorage.data.repository

import com.example.kadaracompose.one.localStorage.data.local.datastore.UserPreferencesDataSource
import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.domain.model.UserPreferences
import com.example.kadaracompose.one.localStorage.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource
) : PreferencesRepository {

    override fun getPreferences(): Flow<UserPreferences> = dataSource.preferences

    override suspend fun setDisplayName(name: String) = dataSource.setDisplayName(name)

    override suspend fun setDarkTheme(enabled: Boolean) = dataSource.setDarkTheme(enabled)

    override suspend fun setFontSize(size: FontSize) = dataSource.setFontSize(size)

    override suspend fun setNotificationsEnabled(enabled: Boolean) =
        dataSource.setNotificationsEnabled(enabled)

    override suspend fun clearAllPreferences() = dataSource.clearAll()
}
