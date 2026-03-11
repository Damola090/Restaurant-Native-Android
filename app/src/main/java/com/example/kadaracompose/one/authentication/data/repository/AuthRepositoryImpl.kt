package com.example.kadaracompose.one.authentication.data.repository

import com.example.kadaracompose.one.authentication.data.local.TokenStorage
import com.example.kadaracompose.one.authentication.data.remote.api.AuthApi
import com.example.kadaracompose.one.authentication.data.remote.dto.LoginRequest
import com.example.kadaracompose.one.authentication.data.remote.dto.RefreshTokenRequest
import com.example.kadaracompose.one.authentication.data.remote.dto.RegisterRequest
import com.example.kadaracompose.one.authentication.domain.model.AuthResult
import com.example.kadaracompose.one.authentication.domain.model.AuthState
import com.example.kadaracompose.one.authentication.domain.model.AuthTokens
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
    private val gson: Gson
) : com.example.kadaracompose.one.authentication.domain.repository.AuthRepository {

    /**
     * Combines the stored tokens + user into a single AuthState Flow.
     * The NavHost observes this to decide which screens to show.
     *
     * Loading → check storage → Authenticated or Unauthenticated
     */
    override fun observeAuthState(): Flow<AuthState> {
        return combine(tokenStorage.tokens, tokenStorage.user) { tokens, user ->
            when {
                tokens == null || user == null -> AuthState.Unauthenticated
                tokens.isAccessTokenExpired && tokens.refreshToken.isBlank() ->
                    AuthState.Unauthenticated
                else -> AuthState.Authenticated(user = user, tokens = tokens)
            }
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return safeAuthCall {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenStorage.saveTokens(body.toTokens())
                tokenStorage.saveUser(body.user.toDomain())
                AuthResult.Success(body.user.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): AuthResult {
        return safeAuthCall {
            val response = api.register(RegisterRequest(email, password, displayName))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenStorage.saveTokens(body.toTokens())
                tokenStorage.saveUser(body.user.toDomain())
                AuthResult.Success(body.user.toDomain())
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        }
    }

    override suspend fun refreshToken(): AuthTokens? {
        return try {
            val refreshToken = tokenStorage.tokens.firstOrNull()?.refreshToken
                ?: return null
            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                val body = response.body()!!
                val expiresAt = System.currentTimeMillis() + (body.expiresInSeconds * 1000L)
                tokenStorage.updateAccessToken(body.accessToken, expiresAt)
                tokenStorage.tokens.firstOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout() {
        try { api.logout() } catch (e: Exception) { /* best effort */ }
        tokenStorage.clearAll()
    }

    override suspend fun getStoredTokens(): AuthTokens? {
        return tokenStorage.tokens.firstOrNull()
    }

    private suspend fun safeAuthCall(call: suspend () -> AuthResult): AuthResult {
        return try {
            call()
        } catch (e: IOException) {
            AuthResult.Error("No internet connection")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unexpected error occurred")
        }
    }

    private fun parseError(errorBody: String?): String {
        return try {
            gson.fromJson(errorBody, com.example.kadaracompose.one.authentication.data.remote.dto.ApiErrorDto::class.java)?.message
                ?: "An error occurred"
        } catch (e: Exception) {
            "An error occurred"
        }
    }
}
