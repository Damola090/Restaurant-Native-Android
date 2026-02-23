package com.example.kadaracompose.restaurants.domain

data class CreateRestaurant(
    val r_id: Int,
    val r_title: String,
    val image: String,
    val r_description: String,
)
