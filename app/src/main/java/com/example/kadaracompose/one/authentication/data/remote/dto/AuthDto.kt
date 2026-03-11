package com.example.kadaracompose.one.authentication.data.remote.dto

import com.example.kadaracompose.one.authentication.domain.model.AuthTokens
import com.example.kadaracompose.one.authentication.domain.model.AuthUser
import com.google.gson.annotations.SerializedName

// ── Request bodies ────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")        val email: String,
    @SerializedName("password")     val password: String,
    @SerializedName("display_name") val displayName: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

// ── Response bodies ───────────────────────────────────────────────────────────

data class AuthResponseDto(
    @SerializedName("access_token")            val accessToken: String,
    @SerializedName("refresh_token")           val refreshToken: String,
    @SerializedName("access_token_expires_in") val expiresInSeconds: Long, // seconds from now
    @SerializedName("user")                    val user: UserDto
) {
    fun toTokens(): AuthTokens = AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        accessTokenExpiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000L)
    )
}

data class UserDto(
    @SerializedName("id")           val id: String,
    @SerializedName("email")        val email: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("avatar_url")   val avatarUrl: String? = null
) {
    fun toDomain(): AuthUser = AuthUser(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl
    )
}

data class RefreshTokenResponseDto(
    @SerializedName("access_token")            val accessToken: String,
    @SerializedName("access_token_expires_in") val expiresInSeconds: Long
)

data class ApiErrorDto(
    @SerializedName("message") val message: String,
    @SerializedName("code")    val code: String? = null
)
