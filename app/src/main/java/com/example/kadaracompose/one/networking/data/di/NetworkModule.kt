package com.example.kadaracompose.one.networking.data.di

import com.example.kadaracompose.one.networking.data.remote.api.PostsApi
import com.example.kadaracompose.one.networking.data.remote.interceptor.AuthInterceptor
import com.example.kadaracompose.one.networking.data.repository.PostsRepositoryImpl
import com.example.kadaracompose.one.networking.data.repository.PostsRepository

import dagger.Binds
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /**
     * Provides the auth token.
     * In production this would read from DataStore or a token manager.
     * Named injection lets you have multiple String providers without ambiguity.
     */
    @Provides
    @Named("auth_token")
    fun provideAuthToken(): String = "fake-jwt-token-for-demo"

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @Named("auth_token") token: String
    ): AuthInterceptor = AuthInterceptor(tokenProvider = { token })

    /**
     * OkHttp is the HTTP engine under Retrofit.
     * This is where you configure:
     *   - Timeouts (what happens if server is slow)
     *   - Interceptors (auth, logging, caching)
     *   - SSL / certificate pinning (production security)
     */

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        authInterceptor: AuthInterceptor
//    ): OkHttpClient {
//
//        val loggingInterceptor = HttpLoggingInterceptor().apply {
//            // BODY logs full request + response — only in debug builds
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//
//        return OkHttpClient.Builder()
//            .addInterceptor(authInterceptor)      // auth goes first
//            .addInterceptor(loggingInterceptor)   // logging goes last (sees final request)
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }

    /**
     * Retrofit wraps OkHttp and turns your API interface into real HTTP calls.
     * GsonConverterFactory handles JSON ↔ Kotlin object conversion automatically.
     */

//    @Provides
//    @Singleton
//    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }

    @Provides
    @Singleton
    fun providePostsApi(
        @Named("json_placeholder") retrofit: Retrofit
    ): PostsApi {
        return retrofit.create(PostsApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingModule {

    @Binds
    @Singleton
    abstract fun bindPostsRepository(
        impl: PostsRepositoryImpl
    ): PostsRepository
}
