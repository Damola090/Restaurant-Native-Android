package com.example.kadaracompose.two.location.data.di

import android.content.Context
import com.example.kadaracompose.two.location.data.geocoding.GeocodingService
import com.example.kadaracompose.two.location.data.location.FusedLocationProvider
import com.example.kadaracompose.two.location.data.repository.LocationRepositoryImpl
import com.example.kadaracompose.two.location.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProvider(
        @ApplicationContext context: Context
    ): FusedLocationProvider = FusedLocationProvider(context)

    @Provides
    @Singleton
    fun provideGeocodingService(
        @ApplicationContext context: Context
    ): GeocodingService = GeocodingService(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationBindingModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository
}
