package com.example.kadaracompose.sensors.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.kadaracompose.sensors.domain.SensorRepository
import com.example.kadaracompose.sensors.domain.model.SensorData
import com.example.kadaracompose.sensors.domain.model.SensorType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SensorRepositoryImpl @Inject constructor(
    private val sensorManager: SensorManager
) : SensorRepository {

    override fun isSensorAvailable(type: SensorType): Boolean {
        return sensorManager.getDefaultSensor(type.toAndroidType()) != null
    }

    override fun observeSensor(type: SensorType): Flow<SensorData> = callbackFlow {
        val androidSensorType = type.toAndroidType()
        val sensor = sensorManager.getDefaultSensor(androidSensorType)

        if (sensor == null) {
            close(IllegalStateException("Sensor ${type.label} not available on this device"))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(
                    SensorData(
                        name = type.label,
                        values = event.values.toList(),
                        unit = type.unit(),
                        description = type.axisDescription()
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) { /* no-op */ }
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

        // When the flow is cancelled (e.g. screen leaves composition), unregister
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    private fun SensorType.toAndroidType(): Int = when (this) {
        SensorType.ACCELEROMETER   -> Sensor.TYPE_ACCELEROMETER
        SensorType.GYROSCOPE       -> Sensor.TYPE_GYROSCOPE
        SensorType.LIGHT           -> Sensor.TYPE_LIGHT
        SensorType.PRESSURE        -> Sensor.TYPE_PRESSURE
        SensorType.PROXIMITY       -> Sensor.TYPE_PROXIMITY
        SensorType.MAGNETIC_FIELD  -> Sensor.TYPE_MAGNETIC_FIELD
    }

    private fun SensorType.unit(): String = when (this) {
        SensorType.ACCELEROMETER  -> "m/s²"
        SensorType.GYROSCOPE      -> "rad/s"
        SensorType.LIGHT          -> "lx"
        SensorType.PRESSURE       -> "hPa"
        SensorType.PROXIMITY      -> "cm"
        SensorType.MAGNETIC_FIELD -> "µT"
    }

    private fun SensorType.axisDescription(): String = when (this) {
        SensorType.ACCELEROMETER  -> "x, y, z axes"
        SensorType.GYROSCOPE      -> "x, y, z rotation"
        SensorType.LIGHT          -> "ambient light level"
        SensorType.PRESSURE       -> "atmospheric pressure"
        SensorType.PROXIMITY      -> "distance from screen"
        SensorType.MAGNETIC_FIELD -> "x, y, z magnetic field"
    }
}
