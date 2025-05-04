package com.example.uitutorial.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.uitutorial.R
import com.example.uitutorial.myList
import com.example.uitutorial.pages.loadProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseActivity(navController: NavHostController, modifier: Modifier, dataId : String, context : Context, tts: TextToSpeech) {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    val sheetState = rememberModalBottomSheetState()
    val textCoroutine = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val image = myList[dataId.toInt()].imageFileName
    val bitmap = remember(image){
        try{
            val inputStream = context.assets.open(image)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Image",
                        modifier = Modifier.size(400.dp)
                    )
                }
            }

            item {
                Text(
                    text = myList[dataId.toInt()].name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            val steps = myList[dataId.toInt()].steps

            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Chip("30:00", Icons.Default.Timer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Chip("${myList[dataId.toInt()].steps.size} steps", Icons.Default.FitnessCenter)
                    Spacer(modifier = Modifier.width(120.dp))
                    Box(
                        modifier = Modifier.clickable {
                            textCoroutine.launch {
                                loadProgress(1500) { progress->
                                    currentProgress = progress
                                }
                                for(i in steps.indices){
                                    tts.speak(steps[i], TextToSpeech.QUEUE_FLUSH, null, null)
                                    delay(timeMillis = 7500)
                                }
                            }
                        }
                    ) {
                        Chip("", Icons.Default.MusicNote)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(steps) { step ->
                ExerciseCard(
                    title = step,
                    imageRes = R.drawable.ic_launcher_foreground,
                    navController = navController,
                    dataId = dataId.toInt(),
                    progressTrigger = currentProgress
                )
            }

            item {
                Button(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4FCF))
                ) {
                    Text("Start", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                Log.d("Message", "Back Navigation")
                showBottomSheet = false
                navController.popBackStack()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ready to Start?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showBottomSheet = false
                        navController.navigate(route = "poseCheck")

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Workout")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        showBottomSheet = false
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dismiss", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun Chip(text: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .background(Color(0xFFEDE7F6), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6A4FCF))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

@Composable
fun ExerciseCard(
    title: String,
    imageRes: Int,
    navController: NavHostController,
    dataId: Int,
    progressTrigger: Float
) {
    var currentProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(progressTrigger) {
        currentProgress = progressTrigger
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate("form3DModel/$dataId")
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        LinearProgressIndicator(progress = currentProgress)
    }
}
