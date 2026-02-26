package com.example.kadaracompose.sensors.data.di

import android.content.Context
import android.hardware.SensorManager
import com.example.kadaracompose.sensors.data.SensorRepositoryImpl
import com.example.kadaracompose.sensors.domain.SensorRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorsProviderModule {

    @Provides
    @Singleton
    fun provideSensorManager(
        @ApplicationContext context: Context
    ): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorsBindingModule {

    @Binds
    @Singleton
    abstract fun bindSensorRepository(
        impl: SensorRepositoryImpl
    ): SensorRepository
}
