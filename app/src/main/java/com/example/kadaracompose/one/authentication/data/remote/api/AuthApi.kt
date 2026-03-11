package com.example.kadaracompose.one.authentication.data.remote.api

import com.example.kadaracompose.one.authentication.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Auth endpoints.
 *
 * Notice @POST("auth/refresh") has @Headers("No-Auth: true") — this tells
 * the TokenInterceptor to skip adding the Authorization header for this call.
 * You can't refresh a token using the token you're trying to refresh.
 */
interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponseDto>

    /**
     * No-Auth header tells TokenInterceptor to skip auth for this endpoint.
     * Refresh token is sent in the body, not the Authorization header.
     */
    @POST("auth/refresh")
    @retrofit2.http.Headers("No-Auth: true")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponseDto>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
}
