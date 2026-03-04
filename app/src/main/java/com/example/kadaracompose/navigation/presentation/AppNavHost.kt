package com.example.kadaracompose.navigation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.example.kadaracompose.navigation.routes.*

// ── Restaurants imports ───────────────────────────────────────────────────────
import com.example.kadaracompose.restaurants.presentation.list.RestaurantsScreen
import com.example.kadaracompose.restaurants.presentation.list.RestaurantsViewModel
import com.example.kadaracompose.restaurants.presentation.details.RestaurantDetailsScreen
import com.example.kadaracompose.restaurants.presentation.create.RestaurantCreateScreen
import com.example.kadaracompose.restaurants.presentation.update.RestaurantUpdateScreen

// ── Sensors imports ───────────────────────────────────────────────────────────
import com.example.kadaracompose.sensors.presentation.SensorsScreen

// ── Networking imports ────────────────────────────────────────────────────────
import com.example.kadaracompose.one.networking.presentation.PostsScreen

// ── Storage imports ───────────────────────────────────────────────────────────
import com.example.kadaracompose.one.localStorage.presentation.room.NotesScreen
import com.example.kadaracompose.one.localStorage.presentation.datastore.PreferencesScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {

        // ── Home ──────────────────────────────────────────────────────────────
        composable<Home> {
            HomeScreen(
                onNavigateToRestaurants = { navController.navigate(RestaurantsGraph) },
                onNavigateToSensors     = { navController.navigate(Sensors) },
                onNavigateToPosts       = { navController.navigate(Posts) },
                onNavigateToNotes       = { navController.navigate(Notes) },
                onNavigateToPreferences = { navController.navigate(Preferences) }
            )
        }

        // ── Restaurants nested graph ──────────────────────────────────────────
        // RestaurantsGraph is the container route — different from RestaurantsList
        // which is the startDestination inside the graph. This fixes the crash.
        navigation<RestaurantsGraph>(startDestination = RestaurantsList) {

            composable<RestaurantsList> {
                val viewModel: RestaurantsViewModel = hiltViewModel()
                RestaurantsScreen(
                    state = viewModel.state.value,
                    onCreateClick = { navController.navigate(RestaurantCreate) },
                    onItemClick = { id -> navController.navigate(RestaurantDetail(id)) },
                    onFavoriteClick = { id, oldValue -> viewModel.toggleFavorite(id, oldValue) }
                )
            }

            composable<RestaurantDetail>(
                deepLinks = listOf(
                    navDeepLink<RestaurantDetail>(
                        basePath = "www.restaurantsapp.details.com"
                    )
                )
            ) {
                RestaurantDetailsScreen(
                    onUpdateClick = { id -> navController.navigate(RestaurantUpdate(id)) }
                )
            }

            composable<RestaurantCreate> {
                RestaurantCreateScreen()
            }

            composable<RestaurantUpdate> {
                RestaurantUpdateScreen()
            }
        }

        // ── Sensors ───────────────────────────────────────────────────────────
        composable<Sensors> {
            SensorsScreen()
        }

        // ── Networking ────────────────────────────────────────────────────────
        composable<Posts> {
            PostsScreen()
        }

        // ── Storage ───────────────────────────────────────────────────────────
        composable<Notes> {
            NotesScreen()
        }

        composable<Preferences> {
            PreferencesScreen()
        }
    }
}