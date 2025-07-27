package com.example.uitutorial.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun WorkoutSettings(navHostController: NavHostController) {
    var selectedSetting by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        WorkoutSettingTile(
            icon = Icons.Default.Person,
            title = "Gender",
            name = "Gender",
            onClick = { selectedSetting = it }
        )
        Divider(color = Color.Gray, thickness = 1.dp)

        WorkoutSettingTile(
            icon = Icons.Default.Coffee,
            title = "Training Rest",
            name = "Training Rest",
            onClick = { selectedSetting = it }
        )
        Divider(color = Color.Gray, thickness = 1.dp)

        WorkoutSettingTile(
            icon = Icons.Default.Timer,
            title = "Timer",
            name = "Timer",
            onClick = { selectedSetting = it }
        )
        Divider(color = Color.Gray, thickness = 1.dp)

        WorkoutSettingTile(
            icon = Icons.AutoMirrored.Filled.VolumeMute,
            title = "Sound Options",
            name = "Sound Options",
            onClick = { selectedSetting = it }
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }

    if (selectedSetting != "") {
        when (selectedSetting) {
            "Gender" -> GenderDialog(onDismiss = { selectedSetting = "" }, onGenderSelected = { _ ->
                selectedSetting = ""
            })
            "Training Rest" -> TrainingRestDialog(
                onDismiss = { selectedSetting = "" },
                onTimeSelected = { _ ->
                    selectedSetting = ""
                }
            )
            "Timer" -> TimerDialog(onDismiss = { selectedSetting = ""},
                onTimeSelected = { _ ->
                    selectedSetting = ""
                })
            "Sound Options" -> SoundOptionsDialog(onDismiss = { selectedSetting = "" })
        }
    }
}

@Composable
fun GenderDialog(onDismiss: () -> Unit, onGenderSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Select Gender") }

    val genderOptions = listOf("Male", "Female", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gender Settings") },
        text = {
            Box {
                Text(
                    text = selectedGender,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(12.dp)
                        .border(
                            BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(4.dp)
                        )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    genderOptions.forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(gender) },
                            onClick = {
                                selectedGender = gender
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedGender != "Select Gender") {
                        onGenderSelected(selectedGender)
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



@Composable
fun TrainingRestDialog(onDismiss: () -> Unit, onTimeSelected: (Int) -> Unit) {
    var expandedMinutes by remember { mutableStateOf(false) }
    var expandedSeconds by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf("0") }
    var selectedSeconds by remember { mutableStateOf("0") }

    val minutesOptions = listOf("0", "5", "10", "15", "20")
    val secondsOptions = listOf("0", "15", "30", "45")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Training Rest Settings") },
        text = {
            Column {
                Text(text = "Select rest time:")
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$selectedMinutes min",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedMinutes = true }
                                .padding(12.dp)
                                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
                        )
                        DropdownMenu(
                            expanded = expandedMinutes,
                            onDismissRequest = { expandedMinutes = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            minutesOptions.forEach { minute ->
                                DropdownMenuItem(
                                    text = { Text("$minute min") },
                                    onClick = {
                                        selectedMinutes = minute
                                        expandedMinutes = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$selectedSeconds sec",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedSeconds = true }
                                .padding(12.dp)
                                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
                        )
                        DropdownMenu(
                            expanded = expandedSeconds,
                            onDismissRequest = { expandedSeconds = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            secondsOptions.forEach { second ->
                                DropdownMenuItem(
                                    text = { Text("$second sec") },
                                    onClick = {
                                        selectedSeconds = second
                                        expandedSeconds = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val totalSeconds = selectedMinutes.toInt() * 60 + selectedSeconds.toInt()
                onTimeSelected(totalSeconds)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun TimerDialog(onDismiss: () -> Unit, onTimeSelected: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Training Rest Settings") },
        text = { Text("Set your rest periods.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun SoundOptionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Training Rest Settings") },
        text = { Text("Set your rest periods.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun WorkoutSettingTile(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: (String)->Unit, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                onClick(name)
            }
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 20.sp)
    }
}


