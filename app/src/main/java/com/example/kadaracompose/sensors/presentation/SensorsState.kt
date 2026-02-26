package com.example.kadaracompose.sensors.presentation

import com.example.kadaracompose.sensors.domain.model.SensorData
import com.example.kadaracompose.sensors.domain.model.SensorType

data class SensorsState(
    val activeSensor: SensorType = SensorType.ACCELEROMETER,
    val sensorData: SensorData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val availableSensors: List<SensorType> = emptyList()
)
