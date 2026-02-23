package com.example.kadaracompose.restaurants.presentation.create

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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class RestaurantCreateViewModel(private val stateHandle: SavedStateHandle): ViewModel() {
    private var restInterface: RestaurantsApiService

    private val _state = mutableStateOf(
        RestaurantCreateState(
            name = "",
            description = "",
            imageUri = null)
        )

    val state: State<RestaurantCreateState>
        get() = _state

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurant-424f3-default-rtdb.firebaseio.com/")
            .build()
        restInterface = retrofit.create(RestaurantsApiService::class.java)

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

     fun createRestaurant(name: String, description: String, imageUri: Uri?){
        viewModelScope.launch(errorHandler + Dispatchers.IO) {
            val restaurant = restInterface.createRestaurantWithId(
                id = 14,
                restaurant = CreateRestaurant(
                    r_id = 14,
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