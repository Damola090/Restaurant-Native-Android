package com.example.kadaracompose.restaurants.domain

data class Restaurant(
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val isFavorite: Boolean = false
)