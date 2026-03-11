package com.example.kadaracompose.one.authentication.domain.usecase

import com.example.kadaracompose.one.authentication.domain.model.AuthResult
import com.example.kadaracompose.one.authentication.domain.model.AuthState
import com.example.kadaracompose.one.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        // Validation lives here, not in the ViewModel
        if (email.isBlank()) return AuthResult.Error("Email cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Invalid email address")
        }
        if (password.isBlank()) return AuthResult.Error("Password cannot be empty")
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters")

        return repository.login(email.trim(), password)
    }
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): AuthResult {
        if (displayName.isBlank()) return AuthResult.Error("Name cannot be empty")
        if (email.isBlank()) return AuthResult.Error("Email cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Invalid email address")
        }
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters")

        return repository.register(email.trim(), password, displayName.trim())
    }
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}

class GetAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthState> = repository.observeAuthState()
}

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.refreshToken()
}
