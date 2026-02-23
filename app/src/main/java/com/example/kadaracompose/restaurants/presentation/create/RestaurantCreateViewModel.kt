package com.example.kadaracompose.restaurants.presentation.create

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.restaurants.data.remote.RestaurantsApiService
import com.example.kadaracompose.restaurants.domain.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestaurantCreateViewModel(private val stateHandle: SavedStateHandle): ViewModel() {
//    private var restInterface: RestaurantsApiService
    val state = mutableStateOf<Restaurant?>(null)

//    init {
//        val retrofit: Retrofit = Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
//            .baseUrl("https://restaurant-424f3-default-rtdb.firebaseio.com/")
//            .build()
//        restInterface = retrofit.create(RestaurantsApiService::class.java)
//
//        val id = stateHandle.get<Int>("restaurant_id") ?: 0
//        viewModelScope.launch {
//            val restaurant = getRemoteRestaurant(id)
//            state.value = restaurant
//        }
//    }

    fun createRestaurant(name: String, description: String, imageUri: Uri?){
        // TODO:  
    }
}