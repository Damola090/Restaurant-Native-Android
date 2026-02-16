package com.example.kadaracompose

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//"https://restaurant-424f3-default-rtdb.firebaseio.com/restaurants.json"
//https://restaurant-424f3-default-rtdb.firebaseio.com/restaurants.json?orderBy="r_id"&equalTo=2

interface RestaurantsApiService {
    @GET("restaurants.json")
    suspend fun getRestaurants(): List<Restaurant>

    @GET("restaurants.json?orderBy=\"r_id\"")
    suspend fun getRestaurant(@Query("equalTo") id: Int): Map<String, Restaurant>
}
