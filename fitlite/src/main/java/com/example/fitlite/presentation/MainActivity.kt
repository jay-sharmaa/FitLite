package com.example.fitlite.presentation

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fitlite.presentation.ViewModel.BackGroundViewModelFactory
import com.example.fitlite.presentation.ViewModel.BackgroundViewModel
import com.example.fitlite.presentation.pages.HeartRatePage
import com.example.fitlite.presentation.pages.HomeLayout
import com.example.fitlite.presentation.utils.MyNav

class MainActivity : ComponentActivity() {
    private val backgroundViewModel: BackgroundViewModel by viewModels {
        BackGroundViewModelFactory(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        backgroundViewModel.requestPermissions(this)
        setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MyNav(backgroundViewModel = backgroundViewModel, context = applicationContext, application)
                }
        }
    }
}

@Composable
fun HeartRateMonitorScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    var heartRate by remember { mutableStateOf("No Data") }
    var hasPermission by remember { mutableStateOf(false) }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    // Check permission on launch
    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            heartRate = (60..100).random().toString()  // Random heart rate value
            kotlinx.coroutines.delay(2000)  // Update every 2 seconds
        }
    }

    // Register Sensor Listener
    DisposableEffect(Unit) {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
                    heartRate = event.values.firstOrNull()?.toInt()?.toString() ?: "No Data"
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (hasPermission && heartRateSensor != null) {
            sensorManager.registerListener(sensorEventListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Heart Rate Monitor", style = MaterialTheme.typography.caption1)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Heart Rate: $heartRate BPM", style = MaterialTheme.typography.body1)
    }
}