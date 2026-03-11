package com.example.kadaracompose.two.workmanager.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kadaracompose.two.workmanager.domain.model.WorkInfo
import com.example.kadaracompose.two.workmanager.domain.model.WorkStatus
import com.example.kadaracompose.two.workmanager.presentation.WorkManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkManagerScreen(viewModel: WorkManagerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("WorkManager Experiment") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Pattern 1: One-time ───────────────────────────────────────────
            item {
                SectionLabel("Pattern 1 — One-time Work")
                Spacer(Modifier.height(4.dp))
                WorkCard(
                    title = "Image Compress",
                    description = "Runs once immediately. Watch the progress bar fill as the worker calls setProgress().",
                    workInfo = state.compressWork,
                    onRun = { viewModel.runOneTimeWork() }
                )
            }

            // ── Pattern 2: Chain ──────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                SectionLabel("Pattern 2 — Chained Work (A → B → C)")
                Spacer(Modifier.height(4.dp))
                ChainCard(
                    works = state.chainWorks,
                    onRun = { viewModel.runChain() }
                )
            }

            // ── Pattern 3: Periodic ───────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                SectionLabel("Pattern 3 — Periodic Work")
                Spacer(Modifier.height(4.dp))
                WorkCard(
                    title = "Periodic Sync (every 15 min)",
                    description = "Schedules repeating work. Only ONE instance runs at a time. Toggle to schedule/cancel.",
                    workInfo = state.periodicWork,
                    onRun = { viewModel.togglePeriodicSync() },
                    runLabel = if (state.isPeriodicScheduled) "Cancel Periodic Sync"
                    else "Schedule Periodic Sync",
                    isDestructive = state.isPeriodicScheduled
                )
            }

            // ── Pattern 4: Constraints ────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                SectionLabel("Pattern 4 — Constrained Work")
                Spacer(Modifier.height(4.dp))
                ConstrainedWorkCard(
                    workInfo = state.constrainedWork,
                    onRun = { viewModel.runConstrainedWork() },
                    onCancel = { viewModel.cancelConstrainedWork() }
                )
            }
        }
    }
}

// ── Work card — used for one-time and periodic ────────────────────────────────

@Composable
private fun WorkCard(
    title: String,
    description: String,
    workInfo: WorkInfo?,
    onRun: () -> Unit,
    runLabel: String = "Run Now",
    isDestructive: Boolean = false
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            workInfo?.let { WorkStatusRow(it) }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onRun,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isDestructive)
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                else ButtonDefaults.buttonColors(),
                enabled = workInfo?.status != WorkStatus.RUNNING
            ) {
                Text(runLabel)
            }
        }
    }
}

// ── Chain card — shows all 3 workers in sequence ──────────────────────────────

@Composable
private fun ChainCard(
    works: List<WorkInfo>,
    onRun: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Compress → Upload → Notify",
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Three workers in sequence. Each worker's output becomes the next worker's input. " +
                "If any worker fails, the chain stops.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            if (works.isEmpty()) {
                Text("Not started", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                works.forEach { work ->
                    WorkStatusRow(work)
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onRun,
                modifier = Modifier.fillMaxWidth(),
                enabled = works.none { it.status == WorkStatus.RUNNING }
            ) {
                Text("Run Chain")
            }
        }
    }
}

// ── Constrained work card ─────────────────────────────────────────────────────

@Composable
private fun ConstrainedWorkCard(
    workInfo: WorkInfo?,
    onRun: () -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("WiFi + Charging Required",
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Enqueued immediately but only RUNS when device is on WiFi AND charging. " +
                "If constraints aren't met it stays ENQUEUED until they are.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            // Constraint indicator chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ConstraintChip(icon = Icons.Default.Wifi, label = "WiFi Only")
                ConstraintChip(icon = Icons.Default.BatteryChargingFull, label = "Charging")
                ConstraintChip(icon = Icons.Default.BatteryFull, label = "Battery OK")
            }

            Spacer(Modifier.height(12.dp))
            workInfo?.let { WorkStatusRow(it) }
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onRun,
                    modifier = Modifier.weight(1f),
                    enabled = workInfo?.status != WorkStatus.RUNNING &&
                            workInfo?.status != WorkStatus.ENQUEUED
                ) { Text("Enqueue") }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    enabled = workInfo?.status == WorkStatus.ENQUEUED ||
                            workInfo?.status == WorkStatus.RUNNING
                ) { Text("Cancel") }
            }
        }
    }
}

// ── Shared UI components ──────────────────────────────────────────────────────

@Composable
private fun WorkStatusRow(work: WorkInfo) {
    val animatedProgress by animateFloatAsState(
        targetValue = work.progress / 100f,
        label = "progress_${work.tag}"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                work.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            WorkStatusBadge(work.status)
        }

        if (work.status == WorkStatus.RUNNING || work.status == WorkStatus.SUCCEEDED) {
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth(),
                color = if (work.status == WorkStatus.SUCCEEDED)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.tertiary
            )
        }

        work.outputMessage?.let { msg ->
            if (work.status == WorkStatus.SUCCEEDED || work.status == WorkStatus.FAILED) {
                Spacer(Modifier.height(4.dp))
                Text(
                    msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun WorkStatusBadge(status: WorkStatus) {
    val (color, label) = when (status) {
        WorkStatus.IDLE       -> MaterialTheme.colorScheme.surfaceVariant to "Idle"
        WorkStatus.ENQUEUED   -> MaterialTheme.colorScheme.secondaryContainer to "Enqueued"
        WorkStatus.RUNNING    -> MaterialTheme.colorScheme.tertiaryContainer to "Running"
        WorkStatus.SUCCEEDED  -> MaterialTheme.colorScheme.primaryContainer to "Done ✓"
        WorkStatus.FAILED     -> MaterialTheme.colorScheme.errorContainer to "Failed"
        WorkStatus.CANCELLED  -> MaterialTheme.colorScheme.surfaceVariant to "Cancelled"
        WorkStatus.BLOCKED    -> MaterialTheme.colorScheme.secondaryContainer to "Blocked"
    }
    Surface(color = color, shape = MaterialTheme.shapes.small) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ConstraintChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
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
