package com.example.kadaracompose.core.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Reusable permission manager for use across all experiments.
 *
 * Usage — single permission:
 *
 *   val cameraPermission = rememberPermissionManager(
 *       permission = Manifest.permission.CAMERA,
 *       onGranted = { startCamera() },
 *       onDenied = { showRationale() },
 *       onPermanentlyDenied = { showSettingsDialog() }
 *   )
 *   Button(onClick = { cameraPermission.request() }) { Text("Open Camera") }
 *
 * Usage — multiple permissions:
 *
 *   val locationPermissions = rememberMultiplePermissionsManager(
 *       permissions = listOf(
 *           Manifest.permission.ACCESS_FINE_LOCATION,
 *           Manifest.permission.ACCESS_COARSE_LOCATION
 *       ),
 *       onAllGranted = { startLocationUpdates() },
 *       onResult = { result -> handlePartialGrant(result) }
 *   )
 */

// ── Single Permission ─────────────────────────────────────────────────────────

class PermissionManager(
    private val context: Context,
    val permission: String,
    private val onGranted: () -> Unit = {},
    private val onDenied: (shouldShowRationale: Boolean) -> Unit = {},
    private val onPermanentlyDenied: () -> Unit = {},
    private val launcher: (String) -> Unit
) {
    val status: PermissionStatus
        get() = context.getPermissionStatus(permission)

    val isGranted: Boolean
        get() = status is PermissionStatus.Granted

    fun request() {
        when (val current = status) {
            is PermissionStatus.Granted -> onGranted()
            is PermissionStatus.PermanentlyDenied -> onPermanentlyDenied()
            else -> launcher(permission)
        }
    }
}

@Composable
fun rememberPermissionManager(
    permission: String,
    onGranted: () -> Unit = {},
    onDenied: (shouldShowRationale: Boolean) -> Unit = {},
    onPermanentlyDenied: () -> Unit = {}
): PermissionManager {
    val context = LocalContext.current

    // Track whether we've requested before — needed to detect PermanentlyDenied
    var hasRequested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            // If we've requested before AND shouldShowRationale is false
            // → user checked "Don't ask again" → PermanentlyDenied
            val shouldShowRationale = context.shouldShowRationale(permission)
            if (hasRequested && !shouldShowRationale) {
                onPermanentlyDenied()
            } else {
                onDenied(shouldShowRationale)
            }
        }
        hasRequested = true
    }

    return remember(permission) {
        PermissionManager(
            context = context,
            permission = permission,
            onGranted = onGranted,
            onDenied = onDenied,
            onPermanentlyDenied = onPermanentlyDenied,
            launcher = { launcher.launch(it) }
        )
    }
}

// ── Multiple Permissions ──────────────────────────────────────────────────────

class MultiplePermissionsManager(
    private val context: Context,
    val permissions: List<String>,
    private val onAllGranted: () -> Unit = {},
    private val onResult: (PermissionResult) -> Unit = {},
    private val launcher: (Array<String>) -> Unit
) {
    fun statuses(): Map<String, PermissionStatus> =
        permissions.associateWith { context.getPermissionStatus(it) }

    val allGranted: Boolean
        get() = permissions.all { context.isGranted(it) }

    fun request() {
        val notGranted = permissions.filter { !context.isGranted(it) }
        if (notGranted.isEmpty()) {
            onAllGranted()
        } else {
            launcher(notGranted.toTypedArray())
        }
    }
}

@Composable
fun rememberMultiplePermissionsManager(
    permissions: List<String>,
    onAllGranted: () -> Unit = {},
    onResult: (PermissionResult) -> Unit = {}
): MultiplePermissionsManager {
    val context = LocalContext.current
    var hasRequested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.filter { it.value }.keys.toList()
        val denied = results.filter { !it.value }.keys.toList()

        // Split denied into denied vs permanently denied
        val permanentlyDenied = if (hasRequested) {
            denied.filter { !context.shouldShowRationale(it) }
        } else emptyList()

        val trulyDenied = denied - permanentlyDenied.toSet()

        val result = PermissionResult(
            granted = granted,
            denied = trulyDenied,
            permanentlyDenied = permanentlyDenied
        )

        if (result.allGranted) onAllGranted() else onResult(result)
        hasRequested = true
    }

    return remember(permissions) {
        MultiplePermissionsManager(
            context = context,
            permissions = permissions,
            onAllGranted = onAllGranted,
            onResult = onResult,
            launcher = { launcher.launch(it) }
        )
    }
}

// ── Context Extensions ────────────────────────────────────────────────────────

fun Context.isGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.shouldShowRationale(permission: String): Boolean =
    androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
        this as androidx.activity.ComponentActivity,
        permission
    )

fun Context.getPermissionStatus(permission: String): PermissionStatus {
    return when {
        isGranted(permission) -> PermissionStatus.Granted
        shouldShowRationale(permission) -> PermissionStatus.Denied(shouldShowRationale = true)
        else -> PermissionStatus.NotRequested
    }
}

/**
 * Opens the app's page in device Settings.
 * The only way out of PermanentlyDenied state.
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
