package com.example.kadaracompose.one.authentication.data.di

import android.content.Context
//import com.example.kadaracompose.one.authentication.data.interceptor.TokenInterceptor
import com.example.kadaracompose.one.authentication.data.local.TokenStorage
import com.example.kadaracompose.one.authentication.data.remote.api.AuthApi
import com.example.kadaracompose.one.authentication.data.repository.AuthRepositoryImpl
import com.example.kadaracompose.one.authentication.domain.repository.AuthRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
object AuthModule {

    // Auth has its own base URL — add it to CoreNetworkModule pattern
    private const val AUTH_BASE_URL = "https://your-auth-api.com/"

    @Provides
    @Singleton
    fun provideTokenStorage(
        @ApplicationContext context: Context
    ): TokenStorage = TokenStorage(context)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    /**
     * Auth gets its own OkHttpClient with the TokenInterceptor injected.
     * This is separate from CoreNetworkModule's client so only auth-required
     * requests get the token attached — not public endpoints.
     */
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthOkHttpClient(
//        tokenInterceptor: TokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
//            .addInterceptor(tokenInterceptor)
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(
        @Named("auth") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("auth") retrofit: Retrofit
    ): AuthApi = retrofit.create(AuthApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindingModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
