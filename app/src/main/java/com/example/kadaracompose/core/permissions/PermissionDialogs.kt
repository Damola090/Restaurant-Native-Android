package com.example.kadaracompose.core.permissions

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

/**
 * Reusable dialogs for the two cases where you need to explain yourself
 * to the user before or after a permission request.
 *
 * Show RationaleDialog when: user denied once, shouldShowRationale = true
 * Show SettingsDialog when:  user permanently denied
 */

/**
 * Shown when the user denied once and shouldShowRationale = true.
 * Explains WHY the app needs the permission before asking again.
 *
 * Usage:
 *   if (showRationale) {
 *       PermissionRationaleDialog(
 *           title = "Camera needed",
 *           rationale = "We need the camera to scan QR codes.",
 *           onConfirm = { permissionManager.request() },
 *           onDismiss = { showRationale = false }
 *       )
 *   }
 */
@Composable
fun PermissionRationaleDialog(
    title: String,
    rationale: String,
    confirmText: String = "Grant Permission",
    dismissText: String = "Not Now",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(rationale) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onConfirm()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

/**
 * Shown when the user has permanently denied the permission.
 * The app can no longer show the system dialog — the only path forward
 * is sending the user to device Settings to grant it manually.
 *
 * Usage:
 *   if (permanentlyDenied) {
 *       PermissionSettingsDialog(
 *           title = "Camera permission required",
 *           message = "Please enable camera access in Settings.",
 *           onOpenSettings = { context.openAppSettings() },
 *           onDismiss = { permanentlyDenied = false }
 *       )
 *   }
 */
@Composable
fun PermissionSettingsDialog(
    title: String,
    message: String,
    confirmText: String = "Open Settings",
    dismissText: String = "Cancel",
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onOpenSettings()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
