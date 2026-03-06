package com.example.kadaracompose.core.permissions

/**
 * Every possible state a runtime permission can be in.
 *
 * Android's permission system has 4 real states, but the API
 * only gives you a boolean (granted/denied). This sealed class
 * makes all 4 states explicit so the UI can handle each correctly.
 *
 *  ┌─────────────────────────────────────────────────────────────────┐
 *  │                    Permission State Machine                     │
 *  │                                                                 │
 *  │  App installs → [NotRequested]                                  │
 *  │       ↓  user asked                                             │
 *  │  [Granted] ← user says yes                                      │
 *  │  [Denied]  ← user says no once  → ask again → show rationale   │
 *  │  [PermanentlyDenied] ← user says "don't ask again"             │
 *  │       ↓  only way out                                           │
 *  │  Open device Settings → user manually grants                   │
 *  └─────────────────────────────────────────────────────────────────┘
 */
sealed class PermissionStatus {

    /** First launch — permission never been requested yet */
    object NotRequested : PermissionStatus()

    /** User granted the permission */
    object Granted : PermissionStatus()

    /**
     * User denied once but hasn't checked "Don't ask again".
     * You're still allowed to ask again, but should show a rationale first
     * explaining WHY you need it.
     */
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus()

    /**
     * User checked "Don't ask again" OR denied on Android 11+ (where the
     * system auto-sets this after 2 denials).
     * You CANNOT show the permission dialog again — must send to Settings.
     */
    object PermanentlyDenied : PermissionStatus()
}

/**
 * Groups a permission with its human-readable context.
 * Used to display meaningful UI instead of raw permission strings.
 */
data class PermissionInfo(
    val permission: String,              // e.g. Manifest.permission.CAMERA
    val title: String,                   // e.g. "Camera"
    val rationale: String,               // why your app needs it
    val settingsMessage: String,         // shown when permanently denied
    val status: PermissionStatus = PermissionStatus.NotRequested
)

/**
 * Result returned after requesting a group of permissions.
 */
data class PermissionResult(
    val granted: List<String>,
    val denied: List<String>,
    val permanentlyDenied: List<String>
) {
    val allGranted: Boolean get() = denied.isEmpty() && permanentlyDenied.isEmpty()
    val anyPermanentlyDenied: Boolean get() = permanentlyDenied.isNotEmpty()
}
