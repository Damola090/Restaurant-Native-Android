package com.example.kadaracompose.two.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import com.example.kadaracompose.two.location.domain.model.LocationState
import com.example.kadaracompose.two.location.domain.usecase.GetCurrentLocationUseCase
import com.example.kadaracompose.two.location.domain.usecase.ObserveLocationUpdatesUseCase
import com.example.kadaracompose.two.location.domain.usecase.ReverseGeocodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getCurrentLocation: GetCurrentLocationUseCase,
    private val observeLocationUpdates: ObserveLocationUpdatesUseCase,
    private val reverseGeocode: ReverseGeocodeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state = _state.asStateFlow()

    private var liveTrackingJob: Job? = null

    fun onPermissionGranted() {
        _state.update { it.copy(hasLocationPermission = true) }
    }

    fun onPermissionDenied() {
        _state.update { it.copy(hasLocationPermission = false) }
    }

    // ── One-time location fetch ───────────────────────────────────────────────

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true, error = null) }

            val location = getCurrentLocation()

            if (location != null) {
                _state.update {
                    it.copy(currentLocation = location, isLoadingLocation = false)
                }
                // Auto-geocode the fetched location
                geocodeLocation(location)
            } else {
                _state.update {
                    it.copy(
                        isLoadingLocation = false,
                        error = "Could not get location. Make sure GPS is enabled."
                    )
                }
            }
        }
    }

    // ── Live location tracking ────────────────────────────────────────────────

    fun startLiveTracking() {
        if (liveTrackingJob?.isActive == true) return

        _state.update { it.copy(isTrackingLive = true, locationHistory = emptyList()) }

        liveTrackingJob = viewModelScope.launch {
            observeLocationUpdates(intervalMs = 3000L, minDistanceMetres = 5f)
                .catch { e ->
                    _state.update {
                        it.copy(isTrackingLive = false, error = e.message)
                    }
                }
                .collect { location ->
                    _state.update {
                        it.copy(
                            liveLocation = location,
                            // Keep last 50 positions for the trail
                            locationHistory = (it.locationHistory + location).takeLast(50)
                        )
                    }
                    geocodeLocation(location)
                }
        }
    }

    fun stopLiveTracking() {
        liveTrackingJob?.cancel()
        liveTrackingJob = null
        _state.update { it.copy(isTrackingLive = false, liveLocation = null) }
    }

    // ── Reverse geocoding ─────────────────────────────────────────────────────

    private fun geocodeLocation(location: LocationCoordinate) {
        viewModelScope.launch {
            _state.update { it.copy(isGeocoding = true) }
            val address = reverseGeocode(location.latitude, location.longitude)
            _state.update { it.copy(geocodedAddress = address, isGeocoding = false) }
        }
    }

    fun onErrorShown() = _state.update { it.copy(error = null) }

    override fun onCleared() {
        super.onCleared()
        liveTrackingJob?.cancel()
    }
}
