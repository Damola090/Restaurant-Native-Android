package com.example.kadaracompose.one.authentication.domain.repository

import com.example.kadaracompose.one.authentication.domain.model.AuthResult
import com.example.kadaracompose.one.authentication.domain.model.AuthState
import com.example.kadaracompose.one.authentication.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /**
     * Emits the current auth state and updates whenever it changes.
     * This is what the NavHost observes to protect routes.
     */
    fun observeAuthState(): Flow<AuthState>

    /** Login with email and password — returns tokens on success */
    suspend fun login(email: String, password: String): AuthResult

    /** Register a new account */
    suspend fun register(email: String, password: String, displayName: String): AuthResult

    /** Use the refresh token to get a new access token */
    suspend fun refreshToken(): AuthTokens?

    /** Clear all stored tokens and user data */
    suspend fun logout()

    /** Get stored tokens for use in interceptor */
    suspend fun getStoredTokens(): AuthTokens?
}
