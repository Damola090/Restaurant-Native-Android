package com.example.kadaracompose.one.localStorage.presentation.datastore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kadaracompose.one.localStorage.domain.model.FontSize
import com.example.kadaracompose.one.localStorage.presentation.datastore.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(viewModel: PreferencesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("DataStore — Preferences") })
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Display Name ──────────────────────────────────────────────────
            PreferenceSection(title = "Display Name") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.nameInput,
                        onValueChange = viewModel::onNameChanged,
                        label = { Text("Your name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = viewModel::onSaveName,
                        enabled = state.nameInput != state.preferences.displayName
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save name")
                    }
                }
                if (state.preferences.displayName.isNotBlank()) {
                    Text(
                        "Saved: ${state.preferences.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Dark Theme ────────────────────────────────────────────────────
            PreferenceSection(title = "Appearance") {
                PreferenceRow(
                    label = "Dark theme",
                    description = "Switch between light and dark mode"
                ) {
                    Switch(
                        checked = state.preferences.isDarkTheme,
                        onCheckedChange = viewModel::onToggleDarkTheme
                    )
                }
            }

            // ── Font Size ─────────────────────────────────────────────────────
            PreferenceSection(title = "Font Size") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FontSize.entries.forEach { size ->
                        FilterChip(
                            selected = state.preferences.fontSize == size,
                            onClick = { viewModel.onFontSizeSelected(size) },
                            label = { Text(size.label) }
                        )
                    }
                }
                Text(
                    "Preview text at ${state.preferences.fontSize.label} size",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize *
                                state.preferences.fontSize.scale
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Notifications ─────────────────────────────────────────────────
            PreferenceSection(title = "Notifications") {
                PreferenceRow(
                    label = "Enable notifications",
                    description = "Receive updates and alerts"
                ) {
                    Switch(
                        checked = state.preferences.notificationsEnabled,
                        onCheckedChange = viewModel::onToggleNotifications
                    )
                }
            }

            // ── Debug ─────────────────────────────────────────────────────────
            PreferenceSection(title = "Debug") {
                Text(
                    "Raw preferences state:",
                    style = MaterialTheme.typography.labelMedium
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = state.preferences.toString().replace(", ", "\n"),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                OutlinedButton(
                    onClick = viewModel::onClearPreferences,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All Preferences")
                }
            }
        }
    }
}

@Composable
private fun PreferenceSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PreferenceRow(
    label: String,
    description: String,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        control()
    }
}
