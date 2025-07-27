package com.example.uitutorial.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun VoiceFeedback(navHostController: NavHostController) {
    val voiceOptions = listOf("Male", "Female", "Robotic", "Natural")
    val selectedVoice = remember { mutableStateOf(voiceOptions[0]) }
    val voiceEnabled = remember { mutableStateOf(true) }
    val pitch = remember { mutableStateOf(1f) }
    val speed = remember { mutableStateOf(1f) }

    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold{ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Voice Feedback", modifier = Modifier.weight(1f))
                Switch(
                    checked = voiceEnabled.value,
                    onCheckedChange = { voiceEnabled.value = it }
                )
            }

            Text("Select Voice Type", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clickable {  }
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                DropdownMenuBox(
                    options = voiceOptions,
                    selectedOption = selectedVoice.value,
                    onOptionSelected = { selectedVoice.value = it }
                )
            }

            Text("Voice Pitch", fontWeight = FontWeight.Bold)
            Slider(
                value = pitch.value,
                onValueChange = { pitch.value = it },
                valueRange = 0.5f..2f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Voice Speed", fontWeight = FontWeight.Bold)
            Slider(
                value = speed.value,
                onValueChange = { speed.value = it },
                valueRange = 0.5f..2f,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Voice")
            }
        }
    }
}

@Composable
fun DropdownMenuBox(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedOption)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}