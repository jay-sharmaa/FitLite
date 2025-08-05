package com.example.fitlite.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.android.fitlite.R
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
import com.example.fitlite.myList
import com.example.fitlite.repository.WorkoutSettingsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestoreSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseActivity(
    navController: NavHostController,
    modifier: Modifier,
    dataId: String,
    context: Context,
    externalTts: TextToSpeech
) {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = WorkoutSettingsRepository(context)

    val tts = externalTts

    val image = myList[dataId.toInt()].imageFileName
    val bitmap = remember(image) {
        try {
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
            modifier = Modifier.fillMaxSize()
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
                    Chip("${steps.size} steps", Icons.Default.FitnessCenter)
                    Spacer(modifier = Modifier.width(120.dp))
                    Box(
                        modifier = Modifier.clickable {
                            if (!isSpeaking) {
                                isSpeaking = true
                                coroutineScope.launch {
                                    for ((i, step) in steps.withIndex()) {
                                        Log.d("TTS", "Speaking: $step")
                                        tts.speak(step, TextToSpeech.QUEUE_FLUSH, null, null)

                                        delay(step.length * 50L + 500)

                                        for (p in 0..100) {
                                            val progress = i + (p / 100f)
                                            currentProgress = progress
                                            delay(15)
                                        }
                                    }
                                    isSpeaking = false
                                }

                            }
                        }
                    ) {
                        Chip("", Icons.Default.MusicNote)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(steps.size) { index ->
                ExerciseCard(
                    title = steps[index],
                    imageRes = R.drawable.ic_launcher_foreground,
                    navController = navController,
                    dataId = dataId.toInt(),
                    progressTrigger = currentProgress,
                    index = index
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
                Text("Ready to Start?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        showBottomSheet = false
                        val Id = dataId.toInt()
                        scope.launch {
                            repository.addDateToWorkoutHistory()
                            navController.navigate("poseCheck/$Id")
                        }
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
        if (text.isNotEmpty()) {
            Text(text, color = Color.Black, fontSize = 14.sp)
        }
    }
}

@Composable
fun ExerciseCard(
    title: String,
    imageRes: Int,
    navController: NavHostController,
    dataId: Int,
    progressTrigger: Float,
    index: Int
) {
    var currentProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(progressTrigger) {
        currentProgress = when {
            progressTrigger < index -> 0f
            progressTrigger >= index + 1 -> 1f
            else -> progressTrigger - index
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
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
            LinearProgressIndicator(
                progress = currentProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
    }
}