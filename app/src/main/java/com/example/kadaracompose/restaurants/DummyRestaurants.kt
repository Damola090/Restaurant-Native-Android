package com.example.kadaracompose.restaurants

import com.example.kadaracompose.restaurants.data.remote.RemoteRestaurant
import com.example.kadaracompose.restaurants.domain.Restaurant

object DummyContent {
    fun getDomainRestaurants() = arrayListOf(
        Restaurant(0, "title0", "image" , "description0", false,),
        Restaurant(1, "title1", "image", "description1", false),
        Restaurant(2, "title2", "image", "description2", false),
        Restaurant(3, "title3", "image", "description3", false)
    )
    fun getRemoteRestaurants() = getDomainRestaurants().map {
        RemoteRestaurant(it.id, it.title, image = it.image, it.description)
    }
}
