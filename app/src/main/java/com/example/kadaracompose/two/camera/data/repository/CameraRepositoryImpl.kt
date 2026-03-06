package com.example.kadaracompose.two.camera.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.kadaracompose.two.camera.domain.repository.CameraRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraRepository {

    /**
     * Copies the CameraX output file into the device's photo gallery
     * using MediaStore — the correct way to save public media on Android.
     *
     * MediaStore vs direct file write:
     * - Direct file write: works but bypasses the media scanner,
     *   photo won't show in Gallery app until reboot on older devices.
     * - MediaStore: registers the file with the system immediately,
     *   shows in Gallery, respects scoped storage on Android 10+.
     */
    override suspend fun savePhotoToGallery(sourceUri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val filename = "KadaraSandbox_${System.currentTimeMillis()}.jpg"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10+ — use relative path in scoped storage
                        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/KadaraSandbox")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val destUri = resolver.insert(collection, contentValues) ?: return@withContext null

                // Copy bytes from source to MediaStore URI
                resolver.openOutputStream(destUri)?.use { outputStream ->
                    resolver.openInputStream(sourceUri)?.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                // On Android 10+ — mark as no longer pending so it appears in Gallery
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(destUri, contentValues, null, null)
                }

                destUri
            } catch (e: Exception) {
                null
            }
        }
    }
}
