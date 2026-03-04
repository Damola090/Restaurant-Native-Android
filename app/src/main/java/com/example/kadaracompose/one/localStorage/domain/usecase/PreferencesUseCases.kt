package com.example.kadaracompose.one.localStorage.domain.usecase

import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.domain.model.UserPreferences
import com.example.kadaracompose.one.localStorage.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> = repository.getPreferences()
}

class SetDisplayNameUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(name: String) = repository.setDisplayName(name)
}

class SetDarkThemeUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkTheme(enabled)
}

class SetFontSizeUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(size: FontSize) = repository.setFontSize(size)
}

class SetNotificationsEnabledUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setNotificationsEnabled(enabled)
}

class ClearPreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke() = repository.clearAllPreferences()
}
