package com.example.kadaracompose.restaurants.domain

import com.example.kadaracompose.restaurants.data.RestaurantsRepository
import javax.inject.Inject


class GetInitialRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantsRepository,
    private val getSortedRestaurantsUseCase: GetSortedRestaurantsUseCase
) {
    suspend operator fun invoke(): List<Restaurant> {
        repository.loadRestaurants()
        return getSortedRestaurantsUseCase()
    }
}

