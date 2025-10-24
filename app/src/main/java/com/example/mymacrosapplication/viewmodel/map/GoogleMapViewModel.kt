package com.example.mymacrosapplication.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        private val fusedLocationClient: FusedLocationProviderClient,
        private val application: Application,
    ) : ViewModel() {
        // ---- STATE ----
        private val _currentLocation = MutableStateFlow<LatLng?>(null)
        val currentLocation: StateFlow<LatLng?> = _currentLocation

        private val _hasPermission = MutableStateFlow(false)
        val hasPermission: StateFlow<Boolean> = _hasPermission

        private val _permissionPermanentlyDenied = MutableStateFlow(false)
        val permissionPermanentlyDenied: StateFlow<Boolean> = _permissionPermanentlyDenied

        // ---- PUBLIC ----

        fun setPermissionGranted(
            granted: Boolean,
            permanentlyDenied: Boolean = false,
        ) {
            _hasPermission.value = granted
            _permissionPermanentlyDenied.value = permanentlyDenied

            if (granted) {
                fetchCurrentLocation()
            }
        }

        fun refreshLocation() {
            viewModelScope.launch {
                if (_hasPermission.value) fetchCurrentLocation()
            }
        }

        fun openAppSettings() {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", application.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            application.startActivity(intent)
        }

        // ---- PRIVATE ----
        @SuppressLint("MissingPermission")
        fun fetchCurrentLocation() {
            val app = application
            if (ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _currentLocation.value = LatLng(location.latitude, location.longitude)
                } else {
                    val request =
                        LocationRequest
                            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                            .setMaxUpdates(1)
                            .build()
                    fusedLocationClient.requestLocationUpdates(
                        request,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                result.lastLocation?.let {
                                    _currentLocation.value = LatLng(it.latitude, it.longitude)
                                }
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        },
                        Looper.getMainLooper(),
                    )
                }
            }
        }
    }
