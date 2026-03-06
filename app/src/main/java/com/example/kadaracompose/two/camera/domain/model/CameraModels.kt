package com.example.kadaracompose.two.camera.domain.model

import android.net.Uri

/**
 * Represents a photo taken by the camera.
 * Uri points to the saved file location — either MediaStore or cache.
 */
data class Photo(
    val uri: Uri,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Which camera lens is active.
 * Maps to CameraX's CameraSelector internally — kept abstract in domain.
 */
enum class CameraLens {
    FRONT, BACK
}

/**
 * All states the camera screen can be in.
 */
data class CameraState(
    val hasPermission: Boolean = false,
    val activelens: CameraLens = CameraLens.BACK,
    val lastPhoto: Photo? = null,
    val isTakingPhoto: Boolean = false,
    val error: String? = null
)
