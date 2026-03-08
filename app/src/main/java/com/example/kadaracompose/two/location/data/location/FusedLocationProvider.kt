package com.example.kadaracompose.two.location.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Wraps Google's FusedLocationProviderClient into coroutine-friendly APIs.
 *
 * FusedLocationProvider vs raw GPS:
 * - Automatically picks the best source (GPS, WiFi, cell towers)
 * - Dramatically better battery life than raw GPS
 * - Falls back gracefully when GPS signal is weak
 *
 * The callbackFlow pattern here is identical to what you saw in SensorsRepositoryImpl —
 * it's the standard way to bridge callback-based Android APIs into Flow.
 */
class FusedLocationProvider @Inject constructor(
    private val context: Context
) {
    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Gets the best last-known location, or requests a fresh one if stale.
     * suspend function — called once, returns immediately.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationCoordinate? {
        return suspendCancellableCoroutine { continuation ->
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                continuation.resume(location?.toDomain())
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    /**
     * Continuous location updates as a Flow.
     *
     * callbackFlow bridges the LocationCallback into a Flow.
     * awaitClose() unregisters the callback when the flow is cancelled —
     * this prevents memory leaks and battery drain automatically.
     */
    @SuppressLint("MissingPermission")
    fun observeLocationUpdates(
        intervalMs: Long,
        minDistanceMetres: Float
    ): Flow<LocationCoordinate> = callbackFlow {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMs
        )
            .setMinUpdateDistanceMeters(minDistanceMetres)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location.toDomain())
                }
            }
        }

        client.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )

        // When the flow collector is cancelled (e.g. screen leaves composition),
        // automatically stop location updates — no manual cleanup needed
        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    private fun Location.toDomain() = LocationCoordinate(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        altitude = altitude,
        speed = speed,
        timestamp = time
    )
}
