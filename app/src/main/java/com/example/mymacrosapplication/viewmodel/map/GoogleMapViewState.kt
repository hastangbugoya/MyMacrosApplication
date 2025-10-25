package com.example.mymacrosapplication.viewmodel.map

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng

data class GoogleMapScreenState(
    val location: LatLng? = null,
    val onOpenBottomSheet: (content: @Composable () -> Unit) -> Unit = {},
    val onCloseBottomSheet: () -> Unit = {},
    val errorMessage: String? = null,
    val exception: Exception? = null,
)
