package com.example.fitlite.pages

import android.content.Context
import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitlite.data.PersonViewModel
import com.example.fitlite.navigationalComponents.ExerciseNavigationGraph
import com.example.fitlite.ui.theme.Purple120
import com.example.fitlite.ui.theme.Purple180
import com.example.fitlite.ui.theme.Purple80
import com.example.fitlite.viewModels.HomePageViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Composable
fun HomePage(viewModel: HomePageViewModel, context: Context, navController: NavHostController, modifier: Modifier, authViewModel: PersonViewModel, tts: TextToSpeech) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 8.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Log.d("Where is Waldo", currentRoute.toString())

                if (currentRoute == "exerciseLayout")
                    WeeklyCard(viewModel, context)

                if (currentRoute == "exerciseLayout")
                    Spacer(modifier = Modifier.height(10.dp))

                ExerciseNavigationGraph(navController = navController, modifier, context, tts)

                if (currentRoute == "exerciseLayout")
                    Spacer(modifier = Modifier.height(10.dp))
                if (currentRoute == "exerciseLayout") {
                    MakeDietPlanCard(navController)
                }
            }
        }
    }
}

@Composable
fun MakeDietPlanCard(navController: NavHostController){
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Create Your Plan", style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(all = 5.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAEDFE)),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(all = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            text = "✨",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Ask AI Trainer",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate("MakePlan")
                            }
                        )
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeDietPlan(modifier: Modifier, navController: NavHostController) {
    var inputWeight by remember { mutableStateOf("") }
    var inputHeight by remember { mutableStateOf("") }
    var selectedFocus by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<ExerciseInfo?>(null) }

    val focusOptions = listOf("Abs", "Chest", "Legs", "Arms", "Cardio")

    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = inputHeight,
                onValueChange = { inputHeight = it },
                label = { Text("Enter Your Height (cm)", color = Color.White) },
                textStyle = TextStyle(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = inputWeight,
                onValueChange = { inputWeight = it },
                label = { Text("Enter Your Weight (kg)", color = Color.White) },
                textStyle = TextStyle(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedFocus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Focus Area", color = Color.White) },
                    textStyle = TextStyle(color = Color.White),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    focusOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                selectedFocus = option
                                expanded = false
                            },
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black)
                                        .padding(8.dp)
                                ) {
                                    Text(option, color = Color.White)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (inputHeight.isNotBlank() && inputWeight.isNotBlank() && selectedFocus.isNotBlank()) {
                        val prompt = "Recommend one exercise for a person who is $inputHeight cm tall, weighs $inputWeight kg, and wants to focus on $selectedFocus."
                        coroutineScope.launch {
                            result = makeApiRequest(prompt)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Exercise", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            result?.let { info ->
                Text("Exercise: ${info.name}", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Difficulty: ${info.publisher}", color = Color.White)
                info.steps.forEachIndexed { index, step ->
                    Text("${index + 1}. $step", color = Color.White)
                }
            }
        }
    }
}



@Composable
fun PrettyDietCard(
    dietName: String,
    dietaryType: String,
    oldImage: String,
    navController: NavHostController
) {
    val image = "images/$oldImage"
    val context = LocalContext.current
    val bitmap = remember(image) {
        try {
            val inputStream = context.assets.open(image)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .clickable {
            val temp: String = "1lb $dietName"
            navController.navigate("dietPage/$temp/$oldImage")
        }
    ) {
        bitmap?.let {
            Log.d("Loaded", "img")
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Image Loaded",
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 300f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)

        ) {
            Text(
                text = dietName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dietaryType,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun WeeklyCard(viewModel: HomePageViewModel, context: Context) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val dayWorkOutGoal by viewModel.daysWorkout.collectAsState()
    val workoutDatesThisWeek by viewModel.weekWorkoutDates.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("Days", "workoutDatesThisWeek days $workoutDatesThisWeek")
    }

    val completedDays = workoutDatesThisWeek.size.coerceAtMost(7)

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Weekly goal", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = if(completedDays >= dayWorkOutGoal) Color.Green else Color.Blue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(completedDays.toString())
                    }
                    withStyle(style = SpanStyle(
                        color = if(completedDays >= dayWorkOutGoal) Color.Green else Color.Blue,
                    )) {
                        append("/$dayWorkOutGoal")
                    }
                },
                    modifier = Modifier.clickable {
                        scope.launch {
                            openAlertDialog.value = true
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            CircleChip(
                modifier = Modifier.fillMaxWidth(),
                completedDates = workoutDatesThisWeek
            )
        }
    }

    if (openAlertDialog.value) {
        AlertDialogExample(
            setDays = {
                viewModel.setDaysWorkout(it.toInt())
            },
            onDismissRequest = {
                openAlertDialog.value = false
            },
            onConfirmation = {
                openAlertDialog.value = false
            },
            dialogText = "Set your weekly goal",
            context = context
        )
    }
}

@Composable
fun CircleChip(modifier: Modifier, completedDates: List<String>) {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SUNDAY
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    }

    val weekDaysWithDates = (0..6).map {
        val date = calendar.time
        val formattedDate = formatter.format(date)
        val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        Pair(dayName, formattedDate)
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(weekDaysWithDates) { (dayName, dateString) ->
            val isCompleted = completedDates.contains(dateString)
            Log.d("Worked out", isCompleted.toString())
            val backgroundColor = if (isCompleted) Purple180 else Purple120

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(backgroundColor)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AlertDialogExample(
    setDays: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogText: String,
    context: Context,
) {
    var textFieldValue by remember { mutableStateOf("") }
    AlertDialog(
        title = {
            Text(text = dialogText)
        },
        text = {
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(text = "")
                },
                value = textFieldValue,
                onValueChange = {
                    if (it.toInt() > 7) {
                        Toast.makeText(
                            context,
                            "Select Appropriate Number Of Days",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textFieldValue = it
                        if(it == ""){
                            textFieldValue = "0"
                        }
                    }
                }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                    setDays(textFieldValue)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}