package com.example.kadaracompose.restaurants.presentation.update

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kadaracompose.restaurants.presentation.create.RestaurantCreateViewModel
import com.example.kadaracompose.ui.theme.KadaracomposeTheme

@Composable
fun RestaurantUpdateScreen() {

    val viewModel: RestaurantUpdateViewModel = viewModel()

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }

    Scaffold(
        topBar = {
            SimpleHeader(
                title = "Details",
//                onBackClick = { navController.popBackStack() }
                onBackClick = {}
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

//            Text(
//                text = "Create New Restaurant",
//                style = MaterialTheme.typography.headlineMedium
//            )

                // 🔹 Name Input
                OutlinedTextField(
                    value = viewModel.state.value.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Restaurant Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    singleLine = true
                )

                // 🔹 Description Input
                OutlinedTextField(
                    value = viewModel.state.value.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                // 🔹 Image Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("No image selected")
                    }
                }

                // 🔹 Pick Image Button
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Image")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Submit Button
                Button(
                    onClick = {
                        viewModel.updateRestaurant(
                            name = viewModel.state.value.name,
                            description = viewModel.state.value.description,
                            imageUri = imageUri
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.state.value.name.isNotBlank() && viewModel.state.value.description.isNotBlank()
                ) {
                    Text("Create Restaurant")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleHeader(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KadaracomposeTheme {
        RestaurantUpdateScreen()
    }
}
