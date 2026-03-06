package com.example.kadaracompose.two.camera.presentation

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.kadaracompose.two.camera.domain.model.CameraLens
import java.io.File
import java.util.concurrent.Executor

/**
 * CameraX preview composable.
 *
 * CameraX is a Jetpack library that wraps Camera2 — it handles
 * the complex lifecycle management that made Camera2 so painful.
 *
 * Key CameraX concepts:
 * - ProcessCameraProvider: binds the camera to the lifecycle
 * - Preview: streams camera frames to the PreviewView
 * - ImageCapture: use case for taking still photos
 * - CameraSelector: which lens (front/back)
 *
 * AndroidView bridges CameraX's View-based PreviewView into Compose.
 */
@Composable
fun CameraPreview(
    lens: CameraLens,
    imageCaptureRef: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraSelector = remember(lens) {
        when (lens) {
            CameraLens.BACK -> CameraSelector.DEFAULT_BACK_CAMERA
            CameraLens.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    // AndroidView lets us use traditional View-based components inside Compose
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

//                val preview = Preview.Builder().build().also {
//                    it.surfaceProvider = previewView.surfaceProvider
//                }

                val preview = Preview.Builder().build().also { preview ->
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                // Pass ImageCapture back to the caller so they can trigger capture
                imageCaptureRef(imageCapture)

                try {
                    // Unbind all before rebinding — important when switching lenses
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        },
        modifier = modifier
    )
}

/**
 * Triggers CameraX image capture and saves to a temp file.
 * Calls onSuccess(uri) or onError(message) when done.
 */
fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    // Save to app cache first — ViewModel handles moving to gallery
    val photoFile = File(
        context.cacheDir,
        "photo_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val uri = output.savedUri ?: Uri.fromFile(photoFile)
                onSuccess(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception.message ?: "Failed to capture photo")
            }
        }
    )
}
