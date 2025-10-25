package com.example.mymacrosapplication.view.alerts

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
)
@Composable
fun GoogleMapErrorBottomSheet(onGranted: @Composable () -> Unit) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showSheet by rememberSaveable { mutableStateOf(true) }

    if (cameraPermissionState.status.isGranted) {
        // ✅ Permission granted → show scanner
        onGranted()
    } else {
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        "Map",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        "We need access to your camera to scan barcodes.",
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showSheet = false
                            cameraPermissionState.launchPermissionRequest()
                        },
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}
