package com.example.uitutorial.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer

@Composable
fun WorkoutSettings() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        WorkoutSettingTile(icon = Icons.Default.Person, title = "Gender")
        Divider(color = Color.Gray, thickness = 1.dp)
        WorkoutSettingTile(icon = Icons.Default.Coffee, title = "Training Rest")
        Divider(color = Color.Gray, thickness = 1.dp)
        WorkoutSettingTile(icon = Icons.Default.Timer, title = "Timer")
        Divider(color = Color.Gray, thickness = 1.dp)
        WorkoutSettingTile(icon = Icons.AutoMirrored.Filled.VolumeMute, title = "Sound Options")
        Divider(color = Color.Gray, thickness = 1.dp)
        WorkoutSettingTile(icon = Icons.Default.Refresh, title = "Restart")
    }
}

@Composable
fun WorkoutSettingTile(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { }
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 20.sp)
    }
}
