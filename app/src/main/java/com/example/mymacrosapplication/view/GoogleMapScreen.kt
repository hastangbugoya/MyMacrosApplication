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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onOpenBottomSheet: (@Composable () -> Unit) -> Unit,
    onCloseBottomSheet: () -> Unit,
) {
    val finePermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val currentLocation by viewModel.currentLocation.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    // Permission handling
    LaunchedEffect(Unit) {
        if (!finePermission.status.isGranted) {
            finePermission.launchPermissionRequest()
        } else {
            viewModel.setPermissionGranted(true)
        }
    }

    LaunchedEffect(finePermission.status.isGranted) {
        viewModel.setPermissionGranted(finePermission.status.isGranted)
    }

    // Update camera when location changes
    LaunchedEffect(currentLocation) {
        currentLocation?.let { latLng ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                durationMs = 1000,
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasPermission),
        )

        // Overlay button at the bottom
        Button(
            onClick = {
                val locationText =
                    currentLocation?.let {
                        "Lat: ${it.latitude}, Lng: ${it.longitude}"
                    } ?: "Location not available"

                onOpenBottomSheet {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                    ) {
                        Text("Current Location:")
                        Text(locationText, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { onCloseBottomSheet() }) {
                            Text("Close")
                        }
                    }
                }
            },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
        ) {
            Text("Show Location Info")
        }
    }
}

/**
 * Helper method to open a bottom sheet with location info.
 */
private fun showLocationBottomSheet(
    latLng: LatLng?,
    onOpenBottomSheet: ((@Composable () -> Unit) -> Unit)?,
    onCloseBottomSheet: (() -> Unit)?,
) {
    if (latLng == null) return

    onOpenBottomSheet?.invoke {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Location Details",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Latitude: ${latLng.latitude}")
            Text(text = "Longitude: ${latLng.longitude}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onCloseBottomSheet?.invoke() }) {
                Text("Close")
            }
        }
    }
}
