package com.example.fitlite.presentation.pages

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.fitlite.R
import com.example.fitlite.presentation.ViewModel.BackgroundViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration

@Composable
fun Walk(context: Context, navController: NavController, backgroundViewModel: BackgroundViewModel){
    val scale = remember { mutableStateOf(1.0f) }
    val coroutineScope = rememberCoroutineScope()
    val time = remember { mutableStateOf("00:00") }
    val showDialog = remember { mutableStateOf(false) }
    val myContext = LocalContext.current
    val calendar = Calendar.getInstance()
    val isRunning = remember { mutableStateOf(false) }

    val timePickerDialog = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(5.dp))
        Icon(
            painter = painterResource(id = R.drawable.walf_fitlite),
            tint = Color.Green, contentDescription = "runIcon",
            modifier = Modifier
                .size(30.dp)
                .scale(scale.value)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Selected Time: ${time.value}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                modifier = Modifier.height(32.dp),
                onClick = {
                    timePickerDialog.value = true
                }) {
                Text("Select Time", style = TextStyle(fontSize = 12.sp))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            modifier = Modifier.height(32.dp),
            onClick = {
                coroutineScope.launch {
                    isRunning.value = !isRunning.value

                    var (hour, minutes) = time.value.split(":").map { it.toInt() }
                    while ((hour > 0 || minutes > 0) && isRunning.value) {
                        delay(60000)
                        if (minutes == 0) {
                            if (hour > 0) {
                                hour--
                                minutes = 59
                            }
                        } else {
                            minutes--
                        }
                        time.value = String.format("%02d:%02d", hour, minutes)
                    }
                    if (time.value == "00:00") {
                        showDialog.value = true
                    }
                }
                backgroundViewModel.readWalkingDistance()
            }
        ){
            if(!isRunning.value){
                Text("Walking", style = TextStyle(fontSize = 12.sp))
            }
            else{
                Text("Stop", style = TextStyle(fontSize = 12.sp))
            }
        }
    }

    if(showDialog.value){
        AlertDialogExample(
            onDismissRequest = {
                showDialog.value = false
            },
            onConfirmation = {
                showDialog.value = false
            },
            dialogTitle = "",
            dialogText = ""
        )
    }

    if(timePickerDialog.value){
        TimePickerDialog(
            myContext,
            { _: TimePicker, hour: Int, minute: Int ->
                time.value = String.format("%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()

        timePickerDialog.value = false
    }
}
