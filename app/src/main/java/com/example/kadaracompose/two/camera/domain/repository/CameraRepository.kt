package com.example.kadaracompose.two.camera.domain.repository

import android.net.Uri

interface CameraRepository {
    /**
     * Saves a captured photo to the device gallery (MediaStore).
     * Returns the Uri of the saved image, or null on failure.
     */
    suspend fun savePhotoToGallery(sourceUri: Uri): Uri?
}
