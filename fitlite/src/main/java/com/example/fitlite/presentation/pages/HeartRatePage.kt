package com.example.fitlite.presentation.pages

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices.WEAR_OS_LARGE_ROUND
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.SampleDataPoint
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.impl.event.MeasureCallbackEvent
import androidx.health.services.client.unregisterMeasureCallback
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.fitlite.R
import com.example.fitlite.presentation.ViewModel.BackgroundViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun HeartRatePage(context : Context, navController: NavController, backgroundViewModel: BackgroundViewModel){
    var isAnimating by remember { mutableStateOf(false) }
    val scale = remember { mutableStateOf(1.0f) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(40.dp))
        Icon(Icons.Filled.Favorite, tint = Color.Red, contentDescription = "heartIcon",
            modifier = Modifier.size(50.dp).scale(scale.value)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                if(!isAnimating) {
                    isAnimating = true
                    coroutineScope.launch {
                        val startTime = System.currentTimeMillis()
                        while(System.currentTimeMillis() - startTime < 15500){
                            scale.value = 1.35f
                            delay(500)
                            scale.value = 1.0f
                            delay(500)
                        }
                        isAnimating = false
                    }
                }

            }
        ){
            Text("Heart Rate", modifier = Modifier.padding(10.dp),)
        }
    }
}


