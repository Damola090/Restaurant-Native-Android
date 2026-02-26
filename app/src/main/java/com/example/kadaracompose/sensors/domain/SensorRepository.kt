package com.example.kadaracompose.sensors.domain

import com.example.kadaracompose.sensors.domain.model.SensorData
import com.example.kadaracompose.sensors.domain.model.SensorType
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun observeSensor(type: SensorType): Flow<SensorData>
    fun isSensorAvailable(type: SensorType): Boolean
}