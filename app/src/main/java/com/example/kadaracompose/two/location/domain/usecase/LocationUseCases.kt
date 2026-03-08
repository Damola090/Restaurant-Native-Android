package com.example.kadaracompose.two.location.domain.usecase

import com.example.kadaracompose.two.location.domain.model.GeocodedAddress
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import com.example.kadaracompose.two.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): LocationCoordinate? {
        return repository.getCurrentLocation()
    }
}

class ObserveLocationUpdatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(
        intervalMs: Long = 3000L,
        minDistanceMetres: Float = 5f
    ): Flow<LocationCoordinate> {
        return repository.observeLocationUpdates(intervalMs, minDistanceMetres)
    }
}

class ReverseGeocodeUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ): GeocodedAddress? {
        return repository.reverseGeocode(latitude, longitude)
    }
}
