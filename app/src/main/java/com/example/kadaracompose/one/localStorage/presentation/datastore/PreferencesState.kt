package com.example.kadaracompose.one.localStorage.presentation.datastore

import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.domain.model.UserPreferences

data class PreferencesState(
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true,
    val nameInput: String = "",
    val userMessage: String? = null
)
