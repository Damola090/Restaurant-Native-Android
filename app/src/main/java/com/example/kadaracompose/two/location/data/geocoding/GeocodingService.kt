package com.example.kadaracompose.two.location.data.geocoding

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.example.kadaracompose.two.location.domain.model.GeocodedAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Reverse geocoding using Android's built-in Geocoder.
 * No API key required — uses Google's geocoding service on the device.
 *
 * Android 33+ has an async API (getFromLocationAsync).
 * Below Android 33 we use the blocking API on an IO dispatcher.
 */
class GeocodingService @Inject constructor(
    private val context: Context
) {
    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): GeocodedAddress? {
        if (!Geocoder.isPresent()) return null

        val geocoder = Geocoder(context)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ — non-blocking async API
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull()?.toDomain())
                }
            }
        } else {
            // Below Android 13 — blocking call, must run on IO
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                    ?.toDomain()
            }
        }
    }

    private fun android.location.Address.toDomain(): GeocodedAddress {
        val addressLines = (0..maxAddressLineIndex).map { getAddressLine(it) }
        return GeocodedAddress(
            fullAddress = addressLines.joinToString(", "),
            street = thoroughfare ?: subThoroughfare ?: "",
            city = locality ?: subAdminArea ?: "",
            state = adminArea ?: "",
            country = countryName ?: "",
            postalCode = postalCode ?: ""
        )
    }
}
