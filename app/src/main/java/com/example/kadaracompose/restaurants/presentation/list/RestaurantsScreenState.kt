package com.example.kadaracompose.restaurants.presentation.list

import com.example.kadaracompose.restaurants.domain.Restaurant


data class RestaurantsScreenState(
    val restaurants: List<Restaurant>,
    val isLoading: Boolean,
    val error: String? = null
)