package com.example.mymacrosapplication

import CarouselPageScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymacrosapplication.ui.theme.MyMacrosApplicationTheme
import com.example.mymacrosapplication.utils.NotificationHelper
import com.example.mymacrosapplication.view.BarcodeScannerScreen
import com.example.mymacrosapplication.view.BottomBarItems
import com.example.mymacrosapplication.view.GoogleMapScreen
import com.example.mymacrosapplication.view.alerts.ErrorBottomSheet
import com.example.mymacrosapplication.viewmodel.MapViewModel
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()
        setContent {
            MyMacrosApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
//                    MainScreenMaterial3()
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001,
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // ✅ Test notification
            notificationHelper.showNotification("Permission Granted", "Notifications enabled!")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BarcodeViewModel = hiltViewModel<BarcodeViewModel>(),
    mapViewModel: MapViewModel = hiltViewModel<MapViewModel>(),
    onNotify: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items =
        listOf(
            BottomBarItems.Home,
            BottomBarItems.Search,
            BottomBarItems.Profile,
        )
    var selectedItem by remember { mutableIntStateOf(0) }

    // --- 🟢 Bottom sheet state management ---
    var bottomSheetContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    // Function to open bottom sheet with content
    val openBottomSheet: (@Composable () -> Unit) -> Unit = { content ->
        bottomSheetContent = content
    }

    // Function to close bottom sheet
    val closeBottomSheet: () -> Unit = {
        bottomSheetContent = null
    }

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
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                            )
                        },
                        label = { Text(item.label) },
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
            when (items[selectedItem]) {
                is BottomBarItems.Home -> Greeting(name = "Home")
                is BottomBarItems.Search -> BarcodeScannerScreen()
                is BottomBarItems.Profile -> {
                    GoogleMapScreen(
                        onOpenBottomSheet = openBottomSheet,
                        onCloseBottomSheet = closeBottomSheet,
                    )
                }
            }
        }

        // 🧱 Shared Error BottomSheet for errors
        ErrorBottomSheet(
            state.errorMessage,
            state.exception,
            onRetry = { viewModel.retryLastAction() },
            onDismiss = { viewModel.clearError() },
        )

        // 🟣 Shared BottomSheet composable — shows content when not null
        bottomSheetContent?.let { content ->
            ModalBottomSheet(
                onDismissRequest = { bottomSheetContent = null },
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
    selectedItem: BottomBarItems,
    onIconClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = selectedItem.label,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = selectedItem.icon),
                    contentDescription = selectedItem.label,
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
