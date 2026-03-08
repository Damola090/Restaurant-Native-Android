package com.example.kadaracompose.two.location.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.kadaracompose.two.location.domain.model.LocationCoordinate
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun MapboxMapView(
    currentLocation: LocationCoordinate?,
    locationHistory: List<LocationCoordinate>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }

    /**
     * MapView in newer Mapbox SDK versions no longer implements LifecycleObserver.
     * Instead we manually forward lifecycle events to the MapView using
     * LifecycleEventObserver — this tells the map when to start/stop rendering,
     * which is important for battery life and preventing memory leaks.
     */
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START   -> mapView.onStart()
                Lifecycle.Event.ON_STOP    -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else                       -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { mapView },
        update = { view ->
            view.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {

                // Enable the built-in location puck (blue dot)
                view.location.apply {
                    enabled = true
                    pulsingEnabled = true
                }

                // Fly camera to current location
                currentLocation?.let { loc ->
                    view.mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(loc.longitude, loc.latitude))
                            .zoom(15.0)
                            .build()
                    )
                }

                // Draw location history trail as fading circles
                if (locationHistory.isNotEmpty()) {
                    val circleManager = view.annotations.createCircleAnnotationManager()
                    val circleOptions = locationHistory.mapIndexed { index, loc ->
                        val opacity = (index.toFloat() / locationHistory.size).toDouble()
                        CircleAnnotationOptions()
                            .withPoint(Point.fromLngLat(loc.longitude, loc.latitude))
                            .withCircleRadius(4.0)
                            .withCircleColor("#3B82F6")
                            .withCircleOpacity(opacity)
                    }
                    circleManager.create(circleOptions)
                }
            }
        },
        modifier = modifier
    )
}
