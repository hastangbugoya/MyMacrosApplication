package com.example.mymacrosapplication.view

import android.Manifest
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center.position
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.mymacrosapplication.viewmodel.map.GoogleMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapScreen(viewModel: GoogleMapViewModel = hiltViewModel()) {
    LocalContext.current
    val locationPermission =
        rememberPermissionState(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
        )
    val location = viewModel.currentLocation.value

    LaunchedEffect(Unit) {
        if (locationPermission.status.isGranted) {
            viewModel.fetchCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location ?: LatLng(37.7749, -122.4194), 12f)
        }

    LaunchedEffect(location) {
        android.util.Log.d("Meow", "Location: $location")
        location?.let {
            android.util.Log.d("Meow", "Location: ${it.latitude}, ${it.longitude}")
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory
                    .newLatLngZoom(it, 15f),
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = locationPermission.status.isGranted),
    ) {
        location?.let {
            Marker(
//                state = MarkerState(position = LatLng(37.7749, -122.4194)),
                state = MarkerState(position = it),
                title = "You are here",
                snippet = "Current location",
            )
        }
    }
}
