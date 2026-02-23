package com.example.kadaracompose.restaurants.data.remote

import com.example.kadaracompose.restaurants.domain.CreateRestaurant
import com.example.kadaracompose.restaurants.domain.Restaurant
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantsApiService {
    @GET("restaurants.json")
    suspend fun getRestaurants(): List<RemoteRestaurant>

    @GET("restaurants.json?orderBy=\"r_id\"")
    suspend fun getRestaurant(@Query("equalTo") id: Int): Map<String, RemoteRestaurant>

    @POST("restaurants/1.json")
    suspend fun createRestaurant(): RemoteRestaurant

    @PUT("restaurants/{id}.json")
    suspend fun createRestaurantWithId(
        @Path("id") id: Int,
        @Body restaurant: CreateRestaurant
    ): RemoteRestaurant
}