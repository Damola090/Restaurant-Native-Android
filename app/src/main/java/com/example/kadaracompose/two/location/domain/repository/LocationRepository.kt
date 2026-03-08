package com.example.kadaracompose.two.location.domain.repository

import com.example.kadaracompose.two.location.domain.model.GeocodedAddress
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    /**
     * One-time location fetch — gets the best available current location.
     * Uses the last known location if fresh enough, otherwise requests a new fix.
     */
    suspend fun getCurrentLocation(): LocationCoordinate?

    /**
     * Continuous location updates as a Flow.
     * Emits a new LocationCoordinate every time the device moves.
     * Cancel the flow collector to stop updates — no manual unregistering needed.
     */
    fun observeLocationUpdates(
        intervalMs: Long = 3000L,       // how often to update
        minDistanceMetres: Float = 5f   // minimum movement to trigger update
    ): Flow<LocationCoordinate>

    /**
     * Converts coordinates into a human-readable address using Android's
     * built-in Geocoder — no API key required.
     */
    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): GeocodedAddress?
}
