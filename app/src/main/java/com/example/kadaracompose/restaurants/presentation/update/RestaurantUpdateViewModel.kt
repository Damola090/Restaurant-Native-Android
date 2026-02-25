package com.example.kadaracompose.restaurants.presentation.update

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.restaurants.data.remote.RestaurantsApiService
import com.example.kadaracompose.restaurants.domain.CreateRestaurant
import com.example.kadaracompose.restaurants.domain.Restaurant
import com.example.kadaracompose.restaurants.presentation.create.RestaurantCreateState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class RestaurantUpdateViewModel(private val stateHandle: SavedStateHandle): ViewModel() {
    private var restInterface: RestaurantsApiService

    private val _state = mutableStateOf(
        RestaurantUpdateState(
            name = "",
            description = "",
            imageUri = null
        )
    )

    val state: State<RestaurantUpdateState>
        get() = _state

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurant-424f3-default-rtdb.firebaseio.com/")
            .build()
        restInterface = retrofit.create(RestaurantsApiService::class.java)

        val id = stateHandle.get<Int>("restaurant_id") ?: 0
        viewModelScope.launch {
            val restaurant = getRemoteRestaurant(id)
            var restaurantUpdate = RestaurantUpdateState(
                name = restaurant.title,
                description = restaurant.description,
//                imageUri = "restaurant.image.toString()"
            )
            _state.value = restaurantUpdate
        }

    }

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        _state.value = _state.value.copy(error = exception.message, name = "", description = "",  imageUri = null)
    }

    //  This creates a NEW state object every time.
    //  That’s what triggers recomposition.

    fun onNameChange(newName: String) {
        _state.value = _state.value.copy(name = newName)
    }

    fun onDescriptionChange(newDescription: String) {
        _state.value = _state.value.copy(description = newDescription)
    }

    private suspend fun getRemoteRestaurant(id: Int): Restaurant {
        return withContext(Dispatchers.IO) {
            val response =  restInterface.getRestaurant(id)
            return@withContext response.values.first().let {
                Restaurant(id = it.id, title = it.title, image= it.image, description = it.description)
            }
        }
    }

     fun updateRestaurant(name: String, description: String, imageUri: Uri?){
        viewModelScope.launch(errorHandler + Dispatchers.IO) {
            val id = stateHandle.get<Int>("restaurant_id") ?: 0
            val restaurant = restInterface.updateRestaurantWithId(
                id = id,
                restaurant = CreateRestaurant(
                    r_id = id,
                    r_title = name,
                    r_description = description,
                    image = imageUri.toString()
                )
            )
            Log.d("TAG", "$restaurant")
            _state.value = _state.value.copy(
                name = "",
                description = "",
                imageUri = null)
        }
    }
}