package com.example.kadaracompose.restaurants.presentation.update

import android.net.Uri

data class RestaurantUpdateState(
    var name: String = "",
    var description: String = "",
    var imageUri: Uri? = null,
    var error: String? = null
)
