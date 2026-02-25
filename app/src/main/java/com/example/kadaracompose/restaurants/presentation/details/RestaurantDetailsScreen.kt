package com.example.kadaracompose.restaurants.presentation.details


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kadaracompose.restaurants.presentation.list.RestaurantDetails
import com.example.kadaracompose.restaurants.presentation.list.RestaurantIcon



@Composable
fun RestaurantDetailsScreen(onUpdateClick: (id: Int) -> Unit) {
    val viewModel: RestaurantDetailsViewModel = viewModel()
    val item = viewModel.state.value
    if (item != null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {

                AsyncImage(
                    model = item.image,
                    contentDescription = "Restaurant image",
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
                    Alignment.CenterHorizontally,
                    TextAlign.Center
                )
                Text("More info coming soon!")
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            viewModel.deleteRestaurant()
                        },
                        modifier = Modifier
                        .padding(top = 32.dp)
                    ) {
                        Text("Delete Restaurant")
                    }
                    Button(
                        onClick = {
                            onUpdateClick(item.id)
                        },
                        modifier = Modifier
                            .padding(top = 32.dp, start = 8.dp)
                    ) {
                        Text("Update Restaurant")
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    KadaracomposeTheme {
//        Box(Modifier.fillMaxSize()) {
//            AsyncImage(
//                model = "https://images.pexels.com/photos/2574489/pexels-photo-2574489.jpeg",
//                contentDescription = "Network image",
////                    contentScale = ContentScale.Crop
//            )
//        }
//    }
//}