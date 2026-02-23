package com.example.kadaracompose.restaurants.presentation.create

import android.net.Uri

data class RestaurantCreateState(
    var name: String = "",
    var description: String = "",
    var imageUri: Uri? = null,
    var error: String? = null
)
