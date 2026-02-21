package com.example.kadaracompose.restaurants.data.remote

import com.google.gson.annotations.SerializedName

data class RemoteRestaurant(
    @SerializedName("r_id")
    val id: Int,
    @SerializedName("r_title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("r_description")
    val description: String)