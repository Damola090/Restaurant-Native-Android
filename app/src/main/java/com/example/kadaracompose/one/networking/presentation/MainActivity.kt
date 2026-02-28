package com.example.kadaracompose.one.networking.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.navDeepLink
import com.example.kadaracompose.restaurants.presentation.details.RestaurantDetailsScreen
import com.example.kadaracompose.restaurants.presentation.list.RestaurantsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kadaracompose.one.networking.presentation.PostsScreen
import com.example.kadaracompose.restaurants.presentation.create.RestaurantCreateScreen
import com.example.kadaracompose.restaurants.presentation.list.RestaurantsViewModel
import com.example.kadaracompose.restaurants.presentation.update.RestaurantUpdateScreen
import com.example.kadaracompose.ui.theme.KadaracomposeTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KadaracomposeTheme {
//                RestaurantsApp()
                PostsScreen()
            }
        }
    }
}


@Composable
private fun RestaurantsApp() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    )  {
    NavHost(navController, startDestination = "restaurants") {
        composable(route = "restaurants") {
           val viewModel: RestaurantsViewModel = hiltViewModel()
            RestaurantsScreen(
                state = viewModel.state.value,
                onCreateClick = { navController.navigate("restaurants/create") },
                onItemClick = { id ->
                    navController.navigate("restaurants/$id")
                },
                onFavoriteClick = { id, oldValue ->
                    viewModel.toggleFavorite(id, oldValue)
                }
            )
        }

        composable(
            route = "restaurants/{restaurant_id}",
            arguments = listOf(navArgument("restaurant_id") {
                type = NavType.IntType
            }),
            deepLinks = listOf(navDeepLink { uriPattern =
                "www.restaurantsapp.details.com/{restaurant_id}" }),
        ) { RestaurantDetailsScreen(
            onUpdateClick = { id ->
                navController.navigate("restaurants/update/$id")
            }
        ) }

        composable(route = "restaurants/create") {
            val viewModel: RestaurantsViewModel = hiltViewModel()
            RestaurantCreateScreen()
        }

        composable(route = "restaurants/update/{restaurant_id}") {
            RestaurantUpdateScreen()
        }
    }
    }
}