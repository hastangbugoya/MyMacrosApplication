package com.example.mymacrosapplication

import CarouselPageScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymacrosapplication.ui.theme.MyMacrosApplicationTheme
import com.example.mymacrosapplication.view.BarcodeScannerScreen
import com.example.mymacrosapplication.view.GoogleMapScreen
import com.example.mymacrosapplication.view.GoogleMapTestScreen
import com.example.mymacrosapplication.view.alerts.CameraPermissionBottomSheet
import com.example.mymacrosapplication.view.alerts.ErrorBottomSheet
import com.example.mymacrosapplication.viewmodel.MapViewModel
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeViewModel
import com.google.android.gms.maps.MapView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMacrosApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BarcodeViewModel = hiltViewModel<BarcodeViewModel>(),
    mapViewModel: MapViewModel = hiltViewModel<MapViewModel>(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items = listOf("Home", "Search", "Profile")
    var selectedItem by remember { mutableIntStateOf(0) }
    LocalContext.current
    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xfffef2ec)),
        topBar = {
            SimpleTopBar(items[selectedItem])
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = {
                            when (label) {
                                "Home" -> Icon(Icons.Default.Home, contentDescription = null)
                                "Search" -> Icon(Icons.Default.Search, contentDescription = null)
                                "Profile" -> Icon(Icons.Default.Person, contentDescription = null)
                            }
                        },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
//            Greeting(name = items[selectedItem])
            when (selectedItem) {
                0 -> Greeting(name = items[selectedItem])
                1 -> {
                    CameraPermissionBottomSheet {
                        BarcodeScannerScreen(viewModel)
                    }
                }

                else -> {
                    GoogleMapScreen()
                }
            }
        }
        ErrorBottomSheet(
            state.errorMessage,
            state.exception,
            onRetry = {
                viewModel.retryLastAction()
            },
            onDismiss = {
                viewModel.clearError()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
    selectedItem: String,
    onIconClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = selectedItem,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = "Click me",
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    )
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyMacrosApplicationTheme {
        Greeting("Android")
    }
}
