package com.example.kadaracompose.one.authentication.presentation

import com.example.kadaracompose.one.authentication.domain.model.AuthState
import com.example.kadaracompose.one.authentication.domain.model.AuthUser


data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isLoginValid: Boolean get() = email.isNotBlank() && password.isNotBlank()
    val isRegisterValid: Boolean get() = isLoginValid && displayName.isNotBlank()
    val isFormValid: Boolean get() = if (isRegisterMode) isRegisterValid else isLoginValid
}

data class ProfileScreenState(
    val user: AuthUser? = null,
    val isLoggingOut: Boolean = false
)
