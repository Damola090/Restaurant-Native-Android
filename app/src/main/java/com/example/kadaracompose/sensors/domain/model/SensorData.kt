package com.example.kadaracompose.sensors.domain.model

data class SensorData(
    val name: String,
    val values: List<Float>,
    val unit: String,
    val description: String
)

enum class SensorType(val label: String) {
    ACCELEROMETER("Accelerometer"),
    GYROSCOPE("Gyroscope"),
    LIGHT("Light"),
    PRESSURE("Pressure"),
    PROXIMITY("Proximity"),
    MAGNETIC_FIELD("Magnetic Field")
}
