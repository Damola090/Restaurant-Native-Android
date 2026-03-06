package com.example.kadaracompose.two.permissions.presentation

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kadaracompose.core.permissions.*

/**
 * Demonstrates all four permission patterns in one screen:
 *
 * 1. Single permission  (Camera)
 * 2. Multiple permissions at once  (Location fine + coarse)
 * 3. Rationale dialog  (auto-shown on first denial)
 * 4. Permanently denied → Settings  (auto-shown on permanent denial)
 *
 * Each card is self-contained — study them independently.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Permissions Experiment") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SectionLabel("Pattern 1 — Single Permission") }
            item { SinglePermissionCard() }

            item { SectionLabel("Pattern 2 — Multiple Permissions") }
            item { MultiplePermissionsCard() }

            item { SectionLabel("Pattern 3 — With Rationale Dialog") }
            item { RationalePermissionCard() }

            item { SectionLabel("Pattern 4 — Permanently Denied → Settings") }
            item { PermanentlyDeniedCard() }
        }
    }
}

// ── Pattern 1: Single Permission ──────────────────────────────────────────────

@Composable
private fun SinglePermissionCard() {
    var resultMessage by remember { mutableStateOf("Not requested yet") }

    val cameraPermission = rememberPermissionManager(
        permission = Manifest.permission.CAMERA,
        onGranted = { resultMessage = "✅ Camera granted" },
        onDenied = { resultMessage = "❌ Camera denied" },
        onPermanentlyDenied = { resultMessage = "🚫 Permanently denied" }
    )

    ExperimentCard(
        title = "Camera",
        description = "Requests a single permission. Check Logcat to see status changes.",
        status = cameraPermission.status,
        result = resultMessage
    ) {
        Button(
            onClick = { cameraPermission.request() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cameraPermission.isGranted) "Permission Granted ✓" else "Request Camera")
        }
    }
}

// ── Pattern 2: Multiple Permissions ──────────────────────────────────────────

@Composable
private fun MultiplePermissionsCard() {
    var resultMessage by remember { mutableStateOf("Not requested yet") }

    val locationPermissions = rememberMultiplePermissionsManager(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onAllGranted = { resultMessage = "✅ All location permissions granted" },
        onResult = { result ->
            resultMessage = buildString {
                if (result.granted.isNotEmpty())
                    appendLine("✅ Granted: ${result.granted.size}")
                if (result.denied.isNotEmpty())
                    appendLine("❌ Denied: ${result.denied.size}")
                if (result.permanentlyDenied.isNotEmpty())
                    appendLine("🚫 Permanently denied: ${result.permanentlyDenied.size}")
            }.trim()
        }
    )

    ExperimentCard(
        title = "Fine + Coarse Location",
        description = "Requests multiple permissions in one system dialog.",
        status = if (locationPermissions.allGranted) PermissionStatus.Granted
        else PermissionStatus.NotRequested,
        result = resultMessage
    ) {
        Button(
            onClick = { locationPermissions.request() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (locationPermissions.allGranted) "All Granted ✓" else "Request Location")
        }
    }
}

// ── Pattern 3: Rationale Dialog ───────────────────────────────────────────────

@Composable
private fun RationalePermissionCard() {
    var resultMessage by remember { mutableStateOf("Not requested yet") }
    var showRationale by remember { mutableStateOf(false) }

    val micPermission = rememberPermissionManager(
        permission = Manifest.permission.RECORD_AUDIO,
        onGranted = { resultMessage = "✅ Microphone granted" },
        onDenied = { shouldShowRationale ->
            if (shouldShowRationale) {
                // Show our rationale dialog before asking again
                showRationale = true
                resultMessage = "⚠️ Denied — showing rationale"
            } else {
                resultMessage = "❌ Denied"
            }
        },
        onPermanentlyDenied = { resultMessage = "🚫 Permanently denied" }
    )

    if (showRationale) {
        PermissionRationaleDialog(
            title = "Microphone Access",
            rationale = "This experiment needs microphone access to demonstrate " +
                    "how to explain permission usage to your users before asking again.",
            onConfirm = { micPermission.request() },
            onDismiss = { showRationale = false }
        )
    }

    ExperimentCard(
        title = "Microphone (with Rationale)",
        description = "Deny once to trigger the rationale dialog. This is best practice " +
                "for permissions that aren't obviously needed.",
        status = micPermission.status,
        result = resultMessage
    ) {
        Button(
            onClick = { micPermission.request() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Request Microphone")
        }
    }
}

// ── Pattern 4: Permanently Denied → Settings ─────────────────────────────────

@Composable
private fun PermanentlyDeniedCard() {
    val context = LocalContext.current
    var resultMessage by remember { mutableStateOf("Deny twice to trigger permanently denied state") }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val contactsPermission = rememberPermissionManager(
        permission = Manifest.permission.READ_CONTACTS,
        onGranted = { resultMessage = "✅ Contacts granted" },
        onDenied = { resultMessage = "❌ Denied — deny again to permanently deny" },
        onPermanentlyDenied = {
            showSettingsDialog = true
            resultMessage = "🚫 Permanently denied — must open Settings"
        }
    )

    if (showSettingsDialog) {
        PermissionSettingsDialog(
            title = "Contacts Permission Required",
            message = "You've permanently denied contacts access. " +
                    "To enable it, go to Settings → Apps → Permissions.",
            onOpenSettings = { context.openAppSettings() },
            onDismiss = { showSettingsDialog = false }
        )
    }

    ExperimentCard(
        title = "Contacts (Permanently Denied)",
        description = "Deny twice (or check 'Don't ask again') to see the Settings flow.",
        status = contactsPermission.status,
        result = resultMessage
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { contactsPermission.request() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Request")
            }
            OutlinedButton(
                onClick = { context.openAppSettings() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null,
                    modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Settings")
            }
        }
    }
}

// ── Shared UI components ──────────────────────────────────────────────────────

@Composable
private fun ExperimentCard(
    title: String,
    description: String,
    status: PermissionStatus,
    result: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                PermissionStatusBadge(status)
            }

            Spacer(Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Result label
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    result,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PermissionStatusBadge(status: PermissionStatus) {
    val (color, icon, label) = when (status) {
        is PermissionStatus.Granted ->
            Triple(MaterialTheme.colorScheme.primaryContainer, Icons.Default.Check, "Granted")
        is PermissionStatus.Denied ->
            Triple(MaterialTheme.colorScheme.errorContainer, Icons.Default.Close, "Denied")
        is PermissionStatus.PermanentlyDenied ->
            Triple(MaterialTheme.colorScheme.errorContainer, Icons.Default.Warning, "Blocked")
        is PermissionStatus.NotRequested ->
            Triple(MaterialTheme.colorScheme.surfaceVariant, Icons.Default.Warning, "Not Asked")
    }

    Surface(color = color, shape = MaterialTheme.shapes.small) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}
