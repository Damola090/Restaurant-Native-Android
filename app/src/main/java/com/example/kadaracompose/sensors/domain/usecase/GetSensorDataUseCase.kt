package com.example.kadaracompose.sensors.domain.usecase

import com.example.kadaracompose.sensors.domain.SensorRepository
import com.example.kadaracompose.sensors.domain.model.SensorData
import com.example.kadaracompose.sensors.domain.model.SensorType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSensorDataUseCase @Inject constructor(
    private val repository: SensorRepository
) {
    operator fun invoke(type: SensorType): Flow<SensorData> {
        return repository.observeSensor(type)
    }

    fun isAvailable(type: SensorType): Boolean {
        return repository.isSensorAvailable(type)
    }
}