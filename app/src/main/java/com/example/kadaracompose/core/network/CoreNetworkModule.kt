package com.example.kadaracompose.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Core network module.
 *
 * OkHttpClient is shared — no reason to have multiple HTTP engines.
 * Retrofit is created per base URL using @Named qualifiers.
 *
 * Adding a new feature with a different base URL?
 * 1. Add a new BASE_URL constant
 * 2. Add a new @Provides @Named("your_api") Retrofit function
 * 3. Inject it with @Named("your_api") in your feature module
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreNetworkModule {

    // ── Base URLs ────────────────────────────────────────────────────────────
    private const val RESTAURANTS_BASE_URL = "https://your-restaurants-api.com/"
    private const val JSON_PLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com/"

    // ── Shared OkHttpClient ──────────────────────────────────────────────────
    // One HTTP engine for the whole app — interceptors, timeouts, SSL config
    // all live here once rather than being duplicated per feature.
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── Named Retrofit instances ─────────────────────────────────────────────

    @Provides
    @Singleton
    @Named("restaurants")
    fun provideRestaurantsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RESTAURANTS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("json_placeholder")
    fun provideJsonPlaceholderRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(JSON_PLACEHOLDER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ── Adding a new API in the future? Just add: ────────────────────────────
    //
    // @Provides
    // @Singleton
    // @Named("maps")
    // fun provideMapsRetrofit(okHttpClient: OkHttpClient): Retrofit {
    //     return Retrofit.Builder()
    //         .baseUrl("https://maps.googleapis.com/")
    //         .client(okHttpClient)
    //         .addConverterFactory(GsonConverterFactory.create())
    //         .build()
    // }
}