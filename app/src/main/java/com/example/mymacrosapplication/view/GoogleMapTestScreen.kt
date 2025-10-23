package com.example.mymacrosapplication.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Suppress("ktlint:standard:function-naming")
@Composable
fun GoogleMapTestScreen() {
    val startingLocation = LatLng(37.4219999, -122.0840575) // Google HQ

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(startingLocation, 10f)
        }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        Marker(
            state = MarkerState(position = startingLocation),
            title = "Test Marker",
            snippet = "Google HQ",
        )
    }
}
