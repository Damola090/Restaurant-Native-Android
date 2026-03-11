package com.example.kadaracompose.one.authentication.domain.model

/**
 * The authenticated user — what the rest of the app knows about who is logged in.
 * Clean domain model, no JWT or API details here.
 */
data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null
)

/**
 * The JWT token pair returned after a successful login.
 *
 * Access token: short-lived (e.g. 15 minutes), sent with every request.
 * Refresh token: long-lived (e.g. 30 days), used ONLY to get a new access token.
 *
 * This two-token pattern limits the damage if an access token is stolen —
 * it expires quickly. The refresh token is stored more securely and used rarely.
 */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: Long   // epoch milliseconds
) {
    val isAccessTokenExpired: Boolean
        get() = System.currentTimeMillis() >= accessTokenExpiresAt

    // Proactively refresh 60 seconds before expiry to avoid mid-request failures
    val shouldRefresh: Boolean
        get() = System.currentTimeMillis() >= accessTokenExpiresAt - 60_000L
}

/**
 * The overall auth state of the app.
 * Observed by the NavHost to decide whether to show login or protected screens.
 */
sealed class AuthState {
    object Loading : AuthState()          // checking stored tokens on startup
    object Unauthenticated : AuthState()  // no valid session
    data class Authenticated(
        val user: AuthUser,
        val tokens: AuthTokens
    ) : AuthState()
}

/**
 * Result of a login/register attempt.
 */
sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
