package com.example.kadaracompose.two.camera.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.two.camera.domain.model.CameraLens
import com.example.kadaracompose.two.camera.domain.model.CameraState
import com.example.kadaracompose.two.camera.domain.model.Photo
import com.example.kadaracompose.two.camera.domain.usecase.SavePhotoToGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val savePhotoToGallery: SavePhotoToGalleryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    fun onPermissionGranted() {
        _state.update { it.copy(hasPermission = true, error = null) }
    }

    fun onPermissionDenied() {
        _state.update { it.copy(hasPermission = false) }
    }

    fun onFlipCamera() {
        _state.update {
            it.copy(
                activelens = if (it.activelens == CameraLens.BACK) CameraLens.FRONT
                else CameraLens.BACK
            )
        }
    }

    /**
     * Called by the UI after CameraX captures and saves to a temp file.
     * ViewModel then handles saving it to the gallery.
     */
    fun onPhotoCaptured(uri: Uri) {
        _state.update { it.copy(lastPhoto = Photo(uri), isTakingPhoto = false) }
    }

    fun onSaveToGallery() {
        val uri = _state.value.lastPhoto?.uri ?: return
        viewModelScope.launch {
            _state.update { it.copy(isTakingPhoto = true) }
            val savedUri = savePhotoToGallery(uri)
            _state.update {
                if (savedUri != null) {
                    it.copy(
                        isTakingPhoto = false,
                        error = null,
                        lastPhoto = Photo(savedUri)
                    )
                } else {
                    it.copy(isTakingPhoto = false, error = "Failed to save photo to gallery")
                }
            }
        }
    }

    fun onStartTakingPhoto() = _state.update { it.copy(isTakingPhoto = true) }
    fun onError(message: String) = _state.update { it.copy(error = message, isTakingPhoto = false) }
    fun onDismissPhoto() = _state.update { it.copy(lastPhoto = null) }
    fun onErrorShown() = _state.update { it.copy(error = null) }
}
