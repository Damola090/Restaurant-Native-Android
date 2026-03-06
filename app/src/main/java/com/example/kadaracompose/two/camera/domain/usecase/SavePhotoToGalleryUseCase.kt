package com.example.kadaracompose.two.camera.domain.usecase

import android.net.Uri
import com.example.kadaracompose.two.camera.domain.repository.CameraRepository
import javax.inject.Inject

class SavePhotoToGalleryUseCase @Inject constructor(
    private val repository: CameraRepository
) {
    suspend operator fun invoke(uri: Uri): Uri? {
        return repository.savePhotoToGallery(uri)
    }
}
