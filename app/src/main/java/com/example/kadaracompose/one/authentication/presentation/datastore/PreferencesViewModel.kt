package com.example.kadaracompose.one.authentication.presentation.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getPreferences: GetPreferencesUseCase,
    private val setDisplayName: SetDisplayNameUseCase,
    private val setDarkTheme: SetDarkThemeUseCase,
    private val setFontSize: SetFontSizeUseCase,
    private val setNotificationsEnabled: SetNotificationsEnabledUseCase,
    private val clearPreferences: ClearPreferencesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PreferencesState())
    val state = _state.asStateFlow()

    init {
        // Exactly the same pattern as Room — subscribe once, updates flow in automatically
        getPreferences()
            .onEach { prefs ->
                _state.update {
                    it.copy(
                        preferences = prefs,
                        nameInput = prefs.displayName,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSaveName() {
        viewModelScope.launch {
            setDisplayName(_state.value.nameInput)
            _state.update { it.copy(userMessage = "Name saved") }
        }
    }

    fun onToggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch { setDarkTheme(enabled) }
    }

    fun onFontSizeSelected(size: FontSize) {
        viewModelScope.launch { setFontSize(size) }
    }

    fun onToggleNotifications(enabled: Boolean) {
        viewModelScope.launch { setNotificationsEnabled(enabled) }
    }

    fun onClearPreferences() {
        viewModelScope.launch {
            clearPreferences()
            _state.update { it.copy(userMessage = "Preferences cleared") }
        }
    }

    fun onNameChanged(value: String) = _state.update { it.copy(nameInput = value) }
    fun onMessageShown() = _state.update { it.copy(userMessage = null) }
}
