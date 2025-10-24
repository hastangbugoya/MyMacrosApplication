package com.example.mymacrosapplication.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mymacrosapplication.viewmodel.map.GoogleMapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*

@Composable
fun GoogleMapScreen(viewModel: GoogleMapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val location by viewModel.currentLocation
    val cameraPositionState = rememberCameraPositionState()

    // Track permission state
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    // Permission launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            hasPermission = granted
            if (granted) viewModel.fetchCurrentLocation()
        }

    // Ask for permission on launch if needed
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            android.util.Log.d("Meow", "Requesting location permission")
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.fetchCurrentLocation()
        }
    }

    // Move camera when location changes
    LaunchedEffect(location) {
        location?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000,
            )
            android.util.Log.d("Meow", "Camera moved to: $it")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true),
        ) {
            location?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "You are here",
                )
            }
        }
    }
}
