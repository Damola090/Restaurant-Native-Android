package com.example.kadaracompose.restaurants.domain

import com.example.kadaracompose.restaurants.data.RestaurantsRepository
import javax.inject.Inject


class GetSortedRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantsRepository
) {
    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants()
            .sortedBy { it.title }
    }
}
