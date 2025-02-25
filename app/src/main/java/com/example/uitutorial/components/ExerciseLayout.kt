package com.example.uitutorial.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.uitutorial.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseActivity(navController: NavHostController, modifier: Modifier) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Button(
                onClick = { showBottomSheet = true }, // Show bottom modal on click
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4FCF)) // Purple button
            ) {
                Text("Start", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()) // Ensure padding for bottom content visibility
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.background_fitness),
                    contentDescription = "Yoga Workout",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            item {
                Text(
                    text = "Yoga Workout",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Chip("32:00", Icons.Default.Timer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Chip("16 exercises", Icons.Default.FitnessCenter)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(
                listOf(
                    "Reclining to Big Toe" to "12 minutes",
                    "Cow Pose" to "8 minutes",
                    "Warrior II Pose" to "12 minutes"
                )
            ) { (title, duration) ->
                ExerciseCard(title, duration, R.drawable.ic_launcher_foreground)
            }
        }
    }

    // Bottom Modal Sheet
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
                        // Start Workout Logic Here
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

// Helper UI Components
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
fun ExerciseCard(title: String, duration: String, imageRes: Int) {
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
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(duration, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
