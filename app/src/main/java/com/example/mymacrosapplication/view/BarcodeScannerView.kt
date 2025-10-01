package com.example.mymacrosapplication.view

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.mymacrosapplication.viewmodel.BarcodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun BarcodeScannerScreen(viewModel: BarcodeViewModel = hiltViewModel<BarcodeViewModel>()) {
    val context = LocalContext.current
    val barcodeValue by viewModel.barcodeValue.collectAsState()
    val foodResult by viewModel.foodResult.collectAsState()
    Log.d("Meow", "BarcodeScannerScreen")
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(100.dp).fillMaxWidth())
        CameraPreview(
            context = context,
            onBarcodeDetected = { code ->
                viewModel.setBarcode(code, "NW8f6sDOwtWk6EOjKZLefMR6wO3JSX8KkcRDHBUg")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Scanned : ${barcodeValue ?: "No barcode yet"}")
        Button(onClick = {viewModel.setBarcode("", "NW8f6sDOwtWk6EOjKZLefMR6wO3JSX8KkcRDHBUg")}) {
            Text("Retry")
        }
        Button(onClick = {viewModel.setBarcode("812130020861", "NW8f6sDOwtWk6EOjKZLefMR6wO3JSX8KkcRDHBUg")}) {
            Text("Barcode: 812130020861")
        }
        foodResult?.let {
            Text(text = "First match: ${it.foods.firstOrNull()?.toString() ?: "No match"}", modifier = Modifier.background(
                Color.Yellow).wrapContentHeight())
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    context: Context,
    onBarcodeDetected: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val scanner = BarcodeScanning.getClient()

                analysis.setAnalyzer(Dispatchers.Default.asExecutor()) { imageProxy: ImageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    barcode.rawValue?.let { value ->
                                        Log.d("BarcodeScanner", "Detected: $value")
                                        onBarcodeDetected(value)
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as androidx.lifecycle.LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp)
    )
}
