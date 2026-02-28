package com.example.kadaracompose.one.networking.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Intercepts every outgoing HTTP request and attaches an Authorization header.
 *
 * This is how you handle auth tokens globally — you never have to pass
 * a token into each individual API function.
 *
 * In production you'd inject a TokenProvider that reads from DataStore/SharedPrefs.
 * Here we keep it simple with a hardcoded token to demonstrate the mechanism.
 *
 * OkHttp interceptor chain:
 *   Request → [AuthInterceptor] → [LoggingInterceptor] → Network → Response
 *
 * Interceptors can:
 *   - Read/modify the request before it's sent
 *   - Read/modify the response before it's returned
 *   - Short-circuit the chain entirely (e.g. return cached response)
 */
class AuthInterceptor(
    private val tokenProvider: () -> String   // lambda so token is fetched fresh each time
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Don't add auth to public endpoints
        if (originalRequest.header("No-Auth") != null) {
            val cleanedRequest = originalRequest.newBuilder()
                .removeHeader("No-Auth")
                .build()
            return chain.proceed(cleanedRequest)
        }

        val token = tokenProvider()

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        val response = chain.proceed(authenticatedRequest)

        // Handle token expiry globally — redirect to login without
        // every call site needing to check for 401
        if (response.code == 401) {
            // In a real app: emit to an auth event bus, trigger logout
            // For now we just let it propagate
        }

        return response
    }
}
