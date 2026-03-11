package com.example.kadaracompose.one.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.one.authentication.domain.model.AuthResult
import com.example.kadaracompose.one.authentication.domain.model.AuthState
import com.example.kadaracompose.one.authentication.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
    private val logout: LogoutUseCase,
    getAuthState: GetAuthStateUseCase
) : ViewModel() {

    // Convert the auth state Flow into a StateFlow so Compose can observe it.
    // WhileSubscribed(5000) keeps the flow alive for 5s after last subscriber
    // disappears — prevents unnecessary restarts on screen rotation.
    val authState: kotlinx.coroutines.flow.StateFlow<AuthState> = getAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

    private val _loginState = MutableStateFlow(LoginScreenState())
    val loginState = _loginState.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileScreenState())
    val profileState = _profileState.asStateFlow()

    // Sync profile state with auth state
    init {
        viewModelScope.launch {
            authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    _profileState.update { it.copy(user = state.user) }
                }
            }
        }
    }

    // ── Login / Register ──────────────────────────────────────────────────────

    fun onSubmit() {
        val current = _loginState.value
        if (!current.isFormValid) return

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }

            val result = if (current.isRegisterMode) {
                register(current.email, current.password, current.displayName)
            } else {
                login(current.email, current.password)
            }

            when (result) {
                is AuthResult.Success -> {
                    // authState Flow updates automatically via TokenStorage —
                    // no manual navigation needed, the NavHost reacts to the state change
                    _loginState.update { it.copy(isLoading = false) }
                }
                is AuthResult.Error -> {
                    _loginState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoggingOut = true) }
            logout()
            _profileState.update { it.copy(isLoggingOut = false, user = null) }
        }
    }

    // ── Form input handlers ───────────────────────────────────────────────────

    fun onEmailChanged(value: String) = _loginState.update { it.copy(email = value, error = null) }
    fun onPasswordChanged(value: String) = _loginState.update { it.copy(password = value, error = null) }
    fun onDisplayNameChanged(value: String) = _loginState.update { it.copy(displayName = value) }
    fun onToggleMode() = _loginState.update {
        it.copy(isRegisterMode = !it.isRegisterMode, error = null)
    }
    fun onErrorShown() = _loginState.update { it.copy(error = null) }
}
