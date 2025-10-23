package com.example.mymacrosapplication.viewmodel.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoogleMapViewModel
    @Inject
    constructor(
        private val app: Application,
    ) : AndroidViewModel(app) {
        private val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(app)
        private val _currentLocation = mutableStateOf<LatLng?>(null)
        val currentLocation: State<LatLng?> = _currentLocation

        fun fetchCurrentLocation() {
            if (
                ActivityCompat.checkSelfPermission(
                    app,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    app,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _currentLocation.value = LatLng(location.latitude, location.longitude)
                } else {
                    // 🔹 Request a fresh location if cached one is null
                    val request =
                        LocationRequest
                            .Builder(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                1000L,
                            ).setMaxUpdates(1)
                            .build()

                    fusedLocationClient.requestLocationUpdates(
                        request,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                val loc = result.lastLocation
                                if (loc != null) {
                                    _currentLocation.value = LatLng(loc.latitude, loc.longitude)
                                    android.util.Log.d("Meow", "Got fresh location: ${loc.latitude}, ${loc.longitude}")
                                    fusedLocationClient.removeLocationUpdates(this)
                                } else {
                                    android.util.Log.d("Meow", "Still no location from request")
                                }
                            }
                        },
                        Looper.getMainLooper(),
                    )
                }
            }
        }
    }
