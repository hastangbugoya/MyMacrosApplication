package com.example.mymacrosapplication.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.mymacrosapplication.ui.theme.MainButtonColors
import com.example.mymacrosapplication.view.input.SearchBar
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeIntent
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeViewModel
import com.google.android.datatransport.runtime.BuildConfig
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun BarcodeScannerScreen(viewModel: BarcodeViewModel = hiltViewModel<BarcodeViewModel>()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    Log.d("Meow", "BarcodeScannerScreen")
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(10.dp),
    ) {
//        if (state.isLoading) {
//            Box(
//                modifier =
//                    Modifier
//                        .fillMaxSize(),
//                // dim background
//                contentAlignment = Alignment.Center,
//            ) {
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary,
//                )
//            }
//        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(65.dp),
        ) {
            SearchBar(
                label = "Search by description",
                onSearch = { searchString ->
                    viewModel.updateSearchString(searchString)
                    Log.d(
                        "Meow",
                        "SearchBar > callback from searchbar > searchString: $searchString",
                    )
                    scope.launch {
                        viewModel.intent.send(
                            BarcodeIntent.SearchFood(
                                searchString,
                                com.example.mymacrosapplication.BuildConfig.USDA_FDA_API_KEY,
                            ),
                        )
                    }
                },
                onChange = {
                    viewModel.updateSearchString(it)
                },
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxHeight(0.2f)
                    .clipToBounds()
                    .padding(0.dp),
        ) {
            CameraPreview(
                context = context,
                onBarcodeDetected = { code ->
                    scope.launch {
                        if (code.isNotEmpty() && !viewModel.state.value.isLoading) {
                            viewModel.intent.send(
                                BarcodeIntent.SetBarcode(
                                    code,
                                    com.example.mymacrosapplication.BuildConfig.USDA_FDA_API_KEY,
                                ),
                            )
                        }
                    }
                },
            )
            Text(
                text = "${state.barcodeValue ?: "No barcode yet"}",
                modifier =
                    Modifier
                        .background(Color.White.copy(0.25f))
                        .padding(3.dp)
                        .align(Alignment.BottomStart),
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f),
            onClick = {
                scope.launch {
                    viewModel.intent
                        .send(
                            BarcodeIntent.SetBarcode(
                                null,
                                com.example.mymacrosapplication.BuildConfig.USDA_FDA_API_KEY,
                            ),
                        ).also {
                            viewModel.unlockBarcode()
                        }
                }
            },
        ) {
            Text("Scan", textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold)
        }
        Button(
            onClick = {
                scope.launch {
                    viewModel.intent.send(
                        BarcodeIntent.SetBarcode(
                            "812130020861",
                            com.example.mymacrosapplication.BuildConfig.USDA_FDA_API_KEY,
                        ),
                    )
                }
            },
        ) {
            Text("Barcode: 812130020861")
        }

        val myMod =
            Modifier
                .fillMaxWidth()
                .padding(0.dp, 2.dp)

        state.foodResult?.foods?.firstOrNull()?.let {
            with(it) {
                brandName?.let { text ->
                    Text(
                        text,
                        modifier = myMod,
                    )
                }
                subbrandName?.let { text ->
                    Text(
                        text,
                        modifier = myMod,
                    )
                }
                description?.let { text ->
                    Text(
                        text,
                        modifier = myMod,
                    )
                }
                Text(
                    "Serving Size: $servingSize $servingSizeUnit ",
                    modifier = myMod,
                )
                packageWeight?.let { text ->
                    Text(
                        text,
                        modifier = myMod,
                    )
                }
                if (!shortDescription.isNullOrEmpty()) {
                    Text(
                        shortDescription,
                        modifier = myMod,
                    )
                }
            }
        }
        val listState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        state.foodResult?.foods?.firstOrNull()?.foodNutrients?.let { list ->
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(5.dp),
            ) {
                items(list) {
                    with(it) {
                        Card(
                            modifier =
                                Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth()
                                    .shadow(3.dp, RoundedCornerShape(8.dp))
                                    .background(Color(0xfffce3d4)),
                            shape = RoundedCornerShape(8.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = Color(0xfffce3d4),
                                    contentColor = Color(0xff691b1e),
                                    disabledContainerColor = Color(0xfffef2ec),
                                    disabledContentColor = Color(0xffb77678),
                                ),
                        ) {
                            Text(
                                "$nutrientName($nutrientId): $value $unitName",
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    context: Context,
    onBarcodeDetected: (String) -> Unit,
) {
    AndroidView(
        factory = { context ->
            val previewView =
                PreviewView(context).apply {
                    layoutParams =
                        LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview =
                    Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                val analysis =
                    ImageAnalysis
                        .Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                val scanner = BarcodeScanning.getClient()

                analysis.setAnalyzer(Dispatchers.Default.asExecutor()) { imageProxy: ImageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image =
                            InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees,
                            )
                        scanner
                            .process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    barcode.rawValue?.let { value ->
                                        Log.d("BarcodeScanner", "Detected: $value")
                                        onBarcodeDetected(value)
                                    }
                                }
                            }.addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis,
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
    )
}
