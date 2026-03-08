package com.example.kadaracompose.two.location.domain.model

/**
 * Clean domain model for a location coordinate.
 * No Android framework types here — just data.
 */
data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,       // metres
    val altitude: Double = 0.0,     // metres above sea level
    val speed: Float = 0f,          // metres/second
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Result of reverse geocoding a coordinate into a human-readable address.
 */
data class GeocodedAddress(
    val fullAddress: String,
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val postalCode: String = ""
)

/**
 * Wraps the entire location screen state.
 */
data class LocationState(
    // Permissions
    val hasLocationPermission: Boolean = false,

    // Current one-time location
    val currentLocation: LocationCoordinate? = null,
    val isLoadingLocation: Boolean = false,

    // Live updates
    val isTrackingLive: Boolean = false,
    val liveLocation: LocationCoordinate? = null,
    val locationHistory: List<LocationCoordinate> = emptyList(),

    // Geocoding
    val geocodedAddress: GeocodedAddress? = null,
    val isGeocoding: Boolean = false,

    // Error
    val error: String? = null
) {
    // The most recent location — live takes priority over one-time
    val displayLocation: LocationCoordinate?
        get() = liveLocation ?: currentLocation
}
