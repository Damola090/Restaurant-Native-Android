package com.example.kadaracompose.sensors.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.sensors.domain.SensorRepository
import com.example.kadaracompose.sensors.domain.model.SensorType
import com.example.kadaracompose.sensors.domain.usecase.GetSensorDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorsViewModel @Inject constructor(
    private val getSensorData: GetSensorDataUseCase,
    private val repository: SensorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SensorsState())
    val state = _state.asStateFlow()

    private var sensorJob: Job? = null

    init {
        // Check which sensors are available on this device
        val available = SensorType.entries.filter { repository.isSensorAvailable(it) }
        _state.update { it.copy(availableSensors = available) }

        // Start observing the default sensor
        if (available.isNotEmpty()) {
            observeSensor(available.first())
        }
    }

    fun onSensorSelected(type: SensorType) {
        if (_state.value.activeSensor == type) return
        _state.update { it.copy(activeSensor = type, sensorData = null, error = null) }
        observeSensor(type)
    }

    private fun observeSensor(type: SensorType) {
        sensorJob?.cancel()
        sensorJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getSensorData(type)
                .catch { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { data ->
                    _state.update { it.copy(sensorData = data, isLoading = false, error = null) }
                }
        }
    }
}
