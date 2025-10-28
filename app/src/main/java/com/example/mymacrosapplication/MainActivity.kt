package com.example.mymacrosapplication

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymacrosapplication.service.AudioPlayerService
import com.example.mymacrosapplication.ui.theme.MyMacrosApplicationTheme
import com.example.mymacrosapplication.utils.NotificationHelper
import com.example.mymacrosapplication.view.BarcodeScannerScreen
import com.example.mymacrosapplication.view.BottomBarItems
import com.example.mymacrosapplication.view.GoogleMapScreen
import com.example.mymacrosapplication.view.NutrientPreferences
import com.example.mymacrosapplication.view.alerts.BarcodeErrorBottomSheet
import com.example.mymacrosapplication.view.alerts.CameraPermissionBottomSheet
import com.example.mymacrosapplication.view.mediaplayer.AudioFileListScreen
import com.example.mymacrosapplication.view.mediaplayer.AudioPlayerPanel
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
        installSplashScreen()
        checkNotificationPermission()
        readAudioPermission()
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

    private fun readAudioPermission() {
        val context = this
        val activity = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    100,
                )
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    101,
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
        when (requestCode) {
            1001 -> { // handle notification permissions
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // ✅ Test notification
                    notificationHelper.showNotification("Permission Granted", "Notifications enabled!")
                }
            }
            1002 -> { // handle audio media permissions
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notificationHelper.showNotification("Permission Granted", "Audio media enabled!")
                } else {
                    //  Permission denied
                }
            }
            1003 -> { /* handle camera */ }
        }
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
    barcodeViewModel: BarcodeViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val audioServiceConnection = rememberAudioPlayerService(context)
    val barcodeScreenState by barcodeViewModel.state.collectAsStateWithLifecycle()
    val items =
        listOf(
            BottomBarItems.Home,
            BottomBarItems.Search,
            BottomBarItems.Profile,
            BottomBarItems.AudioPlayer,
        )
    var selectedItem by remember { mutableIntStateOf(0) }

    // --- 🟢 Bottom sheet state management ---
    var bottomSheetContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    val openBottomSheet: (@Composable () -> Unit) -> Unit = { content ->
        bottomSheetContent = content
    }
    val closeBottomSheet: () -> Unit = { bottomSheetContent = null }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xfffef2ec)),
        topBar = { SimpleTopBar(items[selectedItem]) },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // ✅ AudioPlayerPanel INSIDE composable hierarchy
                AudioPlayerPanel(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(vertical = 6.dp),
                    title = "Now Playing: Sample Song",
                    artist = "Demo Artist",
                    audioUri = Uri.parse("android.resource://${context.packageName}/raw/sample_song"),
                )
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
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (items[selectedItem]) {
                is BottomBarItems.Home -> NutrientPreferences()
                is BottomBarItems.Search ->
                    CameraPermissionBottomSheet {
                        BarcodeScannerScreen(
                            onOpenBottomSheet = openBottomSheet,
                            onCloseBottomSheet = closeBottomSheet,
                        )
                    }
                is BottomBarItems.Profile ->
                    GoogleMapScreen(
                        onOpenBottomSheet = openBottomSheet,
                        onCloseBottomSheet = closeBottomSheet,
                    )
                is BottomBarItems.AudioPlayer -> {
                    AudioFileListScreen(
//                        onFileClick = {
//                            android.util.Log.d("Meow", "File clicked: $it")
//                        },
                    )
                }
            }
        }

        // 🔧 Error bottomsheet
        if (barcodeScreenState.errorMessage != null) {
            BarcodeErrorBottomSheet(
                barcodeScreenState.errorMessage,
                barcodeScreenState.exception,
                onRetry = { barcodeViewModel.retryLastAction() },
                onDismiss = { barcodeViewModel.clearError() },
            )
        }

        // 🟢 Dynamic bottomsheet
        bottomSheetContent?.let { content ->
            ModalBottomSheet(
                onDismissRequest = { bottomSheetContent = null },
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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

@Composable
fun rememberAudioPlayerService(context: Context): AudioPlayerService? {
    var service: AudioPlayerService? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val connection =
            object : ServiceConnection {
                override fun onServiceConnected(
                    name: ComponentName?,
                    binder: IBinder?,
                ) {
                    val b = binder as AudioPlayerService.LocalBinder
                    service = b.getService()
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    service = null
                }
            }

        val intent = Intent(context, AudioPlayerService::class.java)
        context.startService(intent) // ensures service runs
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        onDispose {
            context.unbindService(connection)
        }
    }

    return service
}
