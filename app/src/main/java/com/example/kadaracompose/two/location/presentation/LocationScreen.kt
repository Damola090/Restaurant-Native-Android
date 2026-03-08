package com.example.kadaracompose.two.location.presentation

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kadaracompose.core.permissions.*
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import com.example.kadaracompose.two.location.domain.model.LocationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(viewModel: LocationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Permissions — reusing core utility ───────────────────────────────────
    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val locationPermission = rememberMultiplePermissionsManager(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onAllGranted = { viewModel.onPermissionGranted() },
        onResult = { result ->
            if (result.anyPermanentlyDenied) showSettingsDialog = true
            else showRationale = true
        }
    )

    LaunchedEffect(Unit) {
        if (!locationPermission.allGranted) locationPermission.request()
        else viewModel.onPermissionGranted()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    if (showRationale) {
        PermissionRationaleDialog(
            title = "Location Access Needed",
            rationale = "This experiment needs location access to demonstrate " +
                    "GPS coordinates, live tracking, and reverse geocoding.",
            onConfirm = { locationPermission.request() },
            onDismiss = { showRationale = false }
        )
    }

    if (showSettingsDialog) {
        PermissionSettingsDialog(
            title = "Location Permission Blocked",
            message = "Location was permanently denied. Enable it in Settings to use this experiment.",
            onOpenSettings = { context.openAppSettings() },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Location & Maps") }) }
    ) { padding ->
        if (!state.hasLocationPermission) {
            NoPermissionUI(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onRequest = { locationPermission.request() }
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map takes top half
            MapboxMapView(
                currentLocation = state.displayLocation,
                locationHistory = state.locationHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Info panel takes bottom half
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── One-time location ─────────────────────────────────────────
                LocationCard(title = "Current Location (One-time)") {
                    state.currentLocation?.let { CoordinateDisplay(it) }
                        ?: Text(
                            "Not fetched yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.fetchCurrentLocation() },
                        enabled = !state.isLoadingLocation,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoadingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Get My Location")
                    }
                }

                // ── Live tracking ─────────────────────────────────────────────
                LocationCard(title = "Live Location Updates") {
                    if (state.isTrackingLive && state.liveLocation != null) {
                        CoordinateDisplay(state.liveLocation!!)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "📍 ${state.locationHistory.size} positions recorded",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            if (state.isTrackingLive) "Waiting for signal..."
                            else "Not tracking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (state.isTrackingLive) viewModel.stopLiveTracking()
                            else viewModel.startLiveTracking()
                        },
                        colors = if (state.isTrackingLive)
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        else ButtonDefaults.buttonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if (state.isTrackingLive) Icons.Default.Clear
                            else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (state.isTrackingLive) "Stop Tracking" else "Start Live Tracking")
                    }
                }

                // ── Reverse geocoding ─────────────────────────────────────────
                LocationCard(title = "Reverse Geocoding") {
                    when {
                        state.isGeocoding -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Resolving address...", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        state.geocodedAddress != null -> {
                            val addr = state.geocodedAddress!!
                            Text(
                                addr.fullAddress,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (addr.city.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AddressChip(addr.city)
                                    if (addr.state.isNotBlank()) AddressChip(addr.state)
                                    if (addr.country.isNotBlank()) AddressChip(addr.country)
                                }
                            }
                        }
                        else -> Text(
                            "Fetch a location first to see the address",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ── Shared UI components ──────────────────────────────────────────────────────

@Composable
private fun LocationCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun CoordinateDisplay(location: LocationCoordinate) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            CoordinateRow("Latitude", "%.6f°".format(location.latitude))
            CoordinateRow("Longitude", "%.6f°".format(location.longitude))
            CoordinateRow("Accuracy", "±%.1f m".format(location.accuracy))
            if (location.speed > 0f)
                CoordinateRow("Speed", "%.1f m/s".format(location.speed))
        }
    }
}

@Composable
private fun CoordinateRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AddressChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun NoPermissionUI(
    modifier: Modifier = Modifier,
    onRequest: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text("Location Permission Required",
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Grant location access to use this experiment.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequest) { Text("Grant Permission") }
    }
}
