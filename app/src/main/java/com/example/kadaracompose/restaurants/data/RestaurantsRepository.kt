package com.example.kadaracompose.restaurants.data


import android.util.Log
import com.example.kadaracompose.RestaurantsApplication
import com.example.kadaracompose.restaurants.data.di.IoDispatcher
import com.example.kadaracompose.restaurants.data.local.LocalRestaurant
import com.example.kadaracompose.restaurants.data.local.PartialLocalRestaurant
import com.example.kadaracompose.restaurants.data.local.RestaurantsDao
import com.example.kadaracompose.restaurants.data.local.RestaurantsDb
import com.example.kadaracompose.restaurants.data.remote.RestaurantsApiService
import com.example.kadaracompose.restaurants.domain.Restaurant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantsRepository @Inject constructor(
    private val restInterface: RestaurantsApiService,
    private val restaurantsDao: RestaurantsDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun toggleFavoriteRestaurant(
        id: Int,
        value: Boolean
    ) = withContext(dispatcher) {
        restaurantsDao.update(
            PartialLocalRestaurant(id = id, isFavorite = value)
        )
    }

    suspend fun getRestaurants() : List<Restaurant> {
        return withContext(dispatcher) {
            return@withContext restaurantsDao.getAll().map {
                Restaurant(it.id, it.title, it.image, it.description, it.isFavorite)
            }
        }
    }

    suspend fun loadRestaurants() {
        return withContext(dispatcher) {
            try {
                refreshCache()
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException,
                    is ConnectException,
                    is HttpException -> {
                        if (restaurantsDao.getAll().isEmpty())
                            throw Exception(
                                "Something went wrong. " +
                                        "We have no data.")
                    }
                    else -> throw e
                }
            }
        }
    }

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        Log.d("TAG", "$remoteRestaurants")
        val favoriteRestaurants = restaurantsDao.getAllFavorited()
        restaurantsDao.addAll(remoteRestaurants.map {
            LocalRestaurant(it.id, it.title, it.description, it.image, false)
        })
        restaurantsDao.updateAll(
            favoriteRestaurants.map {
                PartialLocalRestaurant(id = it.id, isFavorite = true)
            })
    }
}