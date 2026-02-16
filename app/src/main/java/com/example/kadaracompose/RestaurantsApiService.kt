package com.example.kadaracompose

import retrofit2.Call
import retrofit2.http.GET

//"https://restaurant-424f3-default-rtdb.firebaseio.com/restaurants.json"

interface RestaurantsApiService {
    @GET("restaurants.json")
    suspend fun getRestaurants(): List<Restaurant>
}
