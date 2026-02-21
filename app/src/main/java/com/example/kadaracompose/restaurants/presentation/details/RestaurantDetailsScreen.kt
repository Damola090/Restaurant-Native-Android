package com.example.kadaracompose.restaurants.presentation.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kadaracompose.R
import com.example.kadaracompose.restaurants.presentation.list.RestaurantDetails
import com.example.kadaracompose.restaurants.presentation.list.RestaurantIcon
import com.example.kadaracompose.ui.theme.KadaracomposeTheme



@Composable
fun RestaurantDetailsScreen() {
    val viewModel: RestaurantDetailsViewModel = viewModel()
    val item = viewModel.state.value
    if (item != null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            AsyncImage(
                model = item.image,
                contentDescription = "Restaurant image",
//                placeholder = painterResource(R.drawable.placeholder),
//                error = painterResource(R.drawable.placeholder),
//                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Transparent)
            )

            RestaurantIcon(
                Icons.Filled.Place,
                Modifier.padding(top = 32.dp, bottom = 32.dp)
            )
            RestaurantDetails(
                item.title,
                item.description,
                Modifier.padding(bottom = 32.dp),
                Alignment.CenterHorizontally
            )
            Text("More info coming soon!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KadaracomposeTheme {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://images.pexels.com/photos/2574489/pexels-photo-2574489.jpeg",
                contentDescription = "Network image",
//                    contentScale = ContentScale.Crop
            )
        }
    }
}