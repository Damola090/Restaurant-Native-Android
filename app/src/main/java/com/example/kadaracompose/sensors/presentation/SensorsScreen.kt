package com.example.kadaracompose.sensors.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kadaracompose.sensors.domain.model.SensorData
import com.example.kadaracompose.sensors.domain.model.SensorType

@Composable
fun SensorsScreen(
    viewModel: SensorsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sensors",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sensor type selector
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.availableSensors) { sensorType ->
                FilterChip(
                    selected = state.activeSensor == sensorType,
                    onClick = { viewModel.onSensorSelected(sensorType) },
                    label = { Text(sensorType.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                ErrorCard(message = state.error!!)
            }

            state.sensorData != null -> {
                SensorDataCard(data = state.sensorData!!)
            }

            state.availableSensors.isEmpty() -> {
                Text("No sensors available on this device.")
            }
        }
    }
}

@Composable
private fun SensorDataCard(data: SensorData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = data.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show raw values
            val axisLabels = listOf("X", "Y", "Z")
            data.values.forEachIndexed { index, value ->
                val label = axisLabels.getOrElse(index) { "Value ${index + 1}" }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "%.4f ${data.unit}".format(value),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (index < data.values.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
