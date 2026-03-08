package com.example.kadaracompose.two.location.data.repository

import com.example.kadaracompose.two.location.data.geocoding.GeocodingService
import com.example.kadaracompose.two.location.data.location.FusedLocationProvider
import com.example.kadaracompose.two.location.domain.model.GeocodedAddress
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import com.example.kadaracompose.two.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationProvider: FusedLocationProvider,
    private val geocodingService: GeocodingService
) : LocationRepository {

    override suspend fun getCurrentLocation(): LocationCoordinate? {
        return fusedLocationProvider.getCurrentLocation()
    }

    override fun observeLocationUpdates(
        intervalMs: Long,
        minDistanceMetres: Float
    ): Flow<LocationCoordinate> {
        return fusedLocationProvider.observeLocationUpdates(intervalMs, minDistanceMetres)
    }

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): GeocodedAddress? {
        return geocodingService.reverseGeocode(latitude, longitude)
    }
}
