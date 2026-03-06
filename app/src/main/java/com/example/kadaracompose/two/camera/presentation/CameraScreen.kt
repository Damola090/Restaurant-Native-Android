package com.example.kadaracompose.two.camera.presentation

import android.Manifest
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.kadaracompose.core.permissions.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Permission setup using our reusable core utility ──────────────────────
    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val cameraPermission = rememberPermissionManager(
        permission = Manifest.permission.CAMERA,
        onGranted = { viewModel.onPermissionGranted() },
        onDenied = { shouldShowRationale ->
            if (shouldShowRationale) showRationale = true
            else viewModel.onPermissionDenied()
        },
        onPermanentlyDenied = {
            showSettingsDialog = true
            viewModel.onPermissionDenied()
        }
    )

    // Request permission on first composition
    LaunchedEffect(Unit) {
        if (!cameraPermission.isGranted) cameraPermission.request()
        else viewModel.onPermissionGranted()
    }

    // Show error as snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    // ── Permission dialogs ────────────────────────────────────────────────────
    if (showRationale) {
        PermissionRationaleDialog(
            title = "Camera Access Needed",
            rationale = "This experiment needs the camera to demonstrate CameraX preview, photo capture, and lens switching.",
            onConfirm = { cameraPermission.request() },
            onDismiss = { showRationale = false }
        )
    }

    if (showSettingsDialog) {
        PermissionSettingsDialog(
            title = "Camera Permission Blocked",
            message = "Camera access was permanently denied. Enable it in Settings to use this experiment.",
            onOpenSettings = { context.openAppSettings() },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // ── Main UI ───────────────────────────────────────────────────────────────
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Camera Experiment") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                !state.hasPermission -> NoCameraPermissionUI(
                    onRequestPermission = { cameraPermission.request() }
                )

                state.lastPhoto != null -> PhotoPreviewUI(
                    photoUri = state.lastPhoto!!.uri.toString(),
                    isSaving = state.isTakingPhoto,
                    onSave = { viewModel.onSaveToGallery() },
                    onDiscard = { viewModel.onDismissPhoto() }
                )

                else -> CameraUI(
                    state = state,
                    onFlip = { viewModel.onFlipCamera() },
                    onCapture = { imageCapture ->
                        viewModel.onStartTakingPhoto()
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            executor = ContextCompat.getMainExecutor(context),
                            onSuccess = { uri -> viewModel.onPhotoCaptured(uri) },
                            onError = { msg -> viewModel.onError(msg) }
                        )
                    }
                )
            }
        }
    }
}

// ── Camera viewfinder + controls ──────────────────────────────────────────────

@Composable
private fun CameraUI(
    state: com.example.kadaracompose.two.camera.domain.model.CameraState,
    onFlip: () -> Unit,
    onCapture: (ImageCapture) -> Unit
) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Live camera preview
        CameraPreview(
            lens = state.activelens,
            imageCaptureRef = { imageCapture = it },
            modifier = Modifier.fillMaxSize()
        )

        // Controls overlay at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(24.dp)
        ) {
            // Flip camera button — left
            IconButton(
                onClick = onFlip,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Switch camera",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Shutter button — center
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { imageCapture?.let { onCapture(it) } },
                    enabled = !state.isTakingPhoto && imageCapture != null
                ) {
                    if (state.isTakingPhoto) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                }
            }

            // Lens label — right
            Text(
                text = if (state.activelens == com.example.kadaracompose.two.camera.domain.model.CameraLens.BACK)
                    "BACK" else "FRONT",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

// ── Photo preview after capture ───────────────────────────────────────────────

@Composable
private fun PhotoPreviewUI(
    photoUri: String,
    isSaving: Boolean,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = photoUri,
            contentDescription = "Captured photo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Top bar — discard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDiscard) {
                Icon(Icons.Default.Close, contentDescription = "Discard", tint = Color.White)
            }
            Text("Preview", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(48.dp))
        }

        // Bottom bar — save to gallery
        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Saving...")
            } else {
                Icon(Icons.Default.Done, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save to Gallery")
            }
        }
    }
}

// ── No permission state ───────────────────────────────────────────────────────

@Composable
private fun NoCameraPermissionUI(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Camera Permission Required",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Grant camera access to use this experiment.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}
