package com.example.mymacrosapplication.view

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mymacrosapplication.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val finePermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val currentLocation by viewModel.currentLocation.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    // Ask permission
    LaunchedEffect(Unit) {
        if (!finePermission.status.isGranted) {
            finePermission.launchPermissionRequest()
        } else {
            viewModel.setPermissionGranted(true)
        }
    }

    // Update viewmodel when permission changes
    LaunchedEffect(finePermission.status.isGranted) {
        viewModel.setPermissionGranted(finePermission.status.isGranted)
    }

    // 🧭 Move camera when a new location arrives
    LaunchedEffect(currentLocation) {
        currentLocation?.let { latLng ->
            android.util.Log.d("MapScreen", "Moving camera to ${latLng.latitude}, ${latLng.longitude}")
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                durationMs = 1000,
            )
        }
    }

    // 🗺️ Show map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasPermission),
    ) {
        currentLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "You are here",
            )
        }
    }
}
