package com.example.kadaracompose.navigation.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Experiment(
    val title: String,
    val description: String,
    val tag: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRestaurants: () -> Unit,
    onNavigateToSensors: () -> Unit,
    onNavigateToPosts: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToCamera: () -> Unit,
) {
    val experiments = listOf(
        Experiment(
            title = "Restaurants",
            description = "Clean Architecture · Room · Retrofit · CRUD",
            tag = "Baseline",
            onClick = onNavigateToRestaurants
        ),
        Experiment(
            title = "Sensors",
            description = "Accelerometer · Gyroscope · Light · Proximity",
            tag = "Hardware",
            onClick = onNavigateToSensors
        ),
        Experiment(
            title = "Networking",
            description = "Retrofit · Interceptors · Loading/Error/Success states",
            tag = "Network",
            onClick = onNavigateToPosts
        ),
        Experiment(
            title = "Storage — Notes",
            description = "Room · Flow · CRUD · Reactive UI",
            tag = "Storage",
            onClick = onNavigateToNotes
        ),
        Experiment(
            title = "Storage — Preferences",
            description = "DataStore · Typed key-value · User settings",
            tag = "Storage",
            onClick = onNavigateToPreferences
        ),
        Experiment(
            title = "Permissions — Media etc",
            description = "Camera · Location contacts",
            tag = "Permission",
            onClick = onNavigateToPermissions
        ),
        Experiment(
            title = "Camera — take picture",
            description = "Camera ·Picture",
            tag = "Camera",
            onClick = onNavigateToCamera
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kadara Sandbox", fontWeight = FontWeight.Bold)
                        Text(
                            "${experiments.size} experiments",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(experiments) { experiment ->
                ExperimentCard(experiment)
            }
        }
    }
}

@Composable
private fun ExperimentCard(experiment: Experiment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { experiment.onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = experiment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = experiment.tag,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = experiment.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
