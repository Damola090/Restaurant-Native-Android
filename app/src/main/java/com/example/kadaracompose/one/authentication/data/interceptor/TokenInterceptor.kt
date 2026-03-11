//package com.example.kadaracompose.one.authentication.data.interceptor
//
//import com.example.kadaracompose.one.authentication.data.local.TokenStorage
//import com.example.kadaracompose.one.authentication.data.remote.api.AuthApi
//import com.example.kadaracompose.one.authentication.data.remote.dto.RefreshTokenRequest
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.runBlocking
//import okhttp3.Interceptor
//import okhttp3.Response
//import javax.inject.Inject
//import javax.inject.Provider
//
///**
// * Intercepts every outgoing request and:
// *  1. Attaches the access token as a Bearer header
// *  2. If the token is about to expire, refreshes it first
// *  3. If a 401 is returned, attempts one token refresh and retries
// *  4. Skips auth entirely for endpoints marked with "No-Auth: true"
// *
// * We inject AuthApi as Provider<AuthApi> (lazy) to avoid a circular
// * dependency — AuthApi needs OkHttpClient, OkHttpClient needs this
// * interceptor, this interceptor needs AuthApi. Provider<> breaks the cycle
// * by deferring AuthApi creation until it's actually needed.
// */
//class TokenInterceptor @Inject constructor(
//    private val tokenStorage: TokenStorage,
//    private val authApiProvider: Provider<AuthApi>
//) : Interceptor {
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//
//        // Skip auth for endpoints that don't need it (e.g. refresh, public APIs)
//        if (originalRequest.header("No-Auth") != null) {
//            val cleaned = originalRequest.newBuilder()
//                .removeHeader("No-Auth")
//                .build()
//            return chain.proceed(cleaned)
//        }
//
//        // Get stored tokens — runBlocking is acceptable in interceptors
//        // since OkHttp already runs on a background thread
//        val tokens = runBlocking { tokenStorage.tokens.firstOrNull() }
//
//        // Proactively refresh if token is close to expiry
//        val freshToken = if (tokens?.shouldRefresh == true) {
//            runBlocking { refreshToken(tokens.refreshToken) } ?: tokens.accessToken
//        } else {
//            tokens?.accessToken
//        }
//
//        // Attach token to request
//        val authenticatedRequest = originalRequest.newBuilder()
//            .apply {
//                freshToken?.let { header("Authorization", "Bearer $it") }
//            }
//            .build()
//
//        val response = chain.proceed(authenticatedRequest)
//
//        // If 401 — token may have just expired, try one refresh and retry
//        if (response.code == 401) {
//            response.close()
//            val refreshToken = runBlocking { tokenStorage.tokens.firstOrNull()?.refreshToken }
//                ?: return response
//
//            val newAccessToken = runBlocking { refreshToken(refreshToken) }
//                ?: return response
//
//            val retryRequest = originalRequest.newBuilder()
//                .header("Authorization", "Bearer $newAccessToken")
//                .build()
//
//            return chain.proceed(retryRequest)
//        }
//
//        return response
//    }
//
//    /**
//     * Calls the refresh endpoint and stores the new access token.
//     * Returns the new access token string, or null if refresh failed.
//     */
//    private suspend fun refreshToken(refreshToken: String): String? {
//        return try {
//            val response = authApiProvider.get().refreshToken(
//                RefreshTokenRequest(refreshToken)
//            )
//            if (response.isSuccessful) {
//                val body = response.body() ?: return null
//                val newExpiresAt = System.currentTimeMillis() + (body.expiresInSeconds * 1000L)
//                tokenStorage.updateAccessToken(body.accessToken, newExpiresAt)
//                body.accessToken
//            } else null
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
