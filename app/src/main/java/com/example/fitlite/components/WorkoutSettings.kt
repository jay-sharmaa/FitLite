package com.example.fitlite.components

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import com.example.fitlite.myDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun WorkoutSettings(navHostController: NavHostController) {
    var selectedSetting by remember {
        mutableStateOf("")
    }

    val SELECTED_GENDER = stringPreferencesKey("selected_gender")
    val SELECTED_REST_TIME = intPreferencesKey("selected_rest_time")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val gender by context.myDataStore.data.map { it[SELECTED_GENDER] ?: "Not set" }
        .collectAsState(initial = "Not set")

    val restTime by context.myDataStore.data.map { it[SELECTED_REST_TIME] ?: 0 }
        .collectAsState(initial = 0)


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        WorkoutSettingTile(
            icon = Icons.Default.Person,
            title = "Gender",
            name = "Gender",
            value = gender,
            onClick = { selectedSetting = it }
        )
        Divider(color = Color.Gray, thickness = 1.dp)

        WorkoutSettingTile(
            icon = Icons.Default.Coffee,
            title = "Training Rest",
            name = "Training Rest",
            value = if (restTime > 0) "${restTime / 60}m ${restTime % 60}s" else "Not set",
            onClick = { selectedSetting = it }
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }

    if (selectedSetting != "") {
        when (selectedSetting) {
            "Gender" -> GenderDialog(
                onDismiss = { selectedSetting = "" },
                onGenderSelected = { genderSelected ->
                    scope.launch {
                        context.myDataStore.edit { prefs ->
                            prefs[SELECTED_GENDER] = genderSelected
                        }
                    }
                    selectedSetting = ""
                }
            )
            "Training Rest" -> TrainingRestDialog(
                onDismiss = { selectedSetting = "" },
                onTimeSelected = { seconds ->
                    scope.launch {
                        context.myDataStore.edit { prefs ->
                            prefs[SELECTED_REST_TIME] = seconds
                        }
                    }
                    selectedSetting = ""
                }
            )
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
fun WorkoutSettingTile(
    icon: ImageVector,
    title: String,
    onClick: (String) -> Unit,
    name: String,
    value: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick(name) },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 20.sp)
        }
        if (!value.isNullOrEmpty()) {
            Text(text = value, fontSize = 16.sp, color = Color.Gray)
        }
    }
}
