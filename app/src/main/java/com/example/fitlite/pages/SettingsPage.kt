package com.example.fitlite.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitlite.addExercise
import com.example.fitlite.components.Widgets
import com.example.fitlite.myList
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

const val API_KEY = ""
const val API_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

data class ExerciseInfo(
    val name: String,
    val publisher: String,
    val steps: List<String>,
)

@Composable
fun ExplorePage(navController: NavHostController) {
    val items = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<String>()
    }

    var exerciseName by rememberSaveable { mutableStateOf("") }
    var exercisePublisher by rememberSaveable { mutableStateOf("") }
    val exerciseSteps = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<String>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        MySearchBar(
            items = items,
            onExerciseInfoReceived = { exerciseInfo ->
                exerciseName = exerciseInfo.name
                exercisePublisher = exerciseInfo.publisher
                exerciseSteps.clear()
                exerciseSteps.addAll(exerciseInfo.steps)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (exerciseName.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = exerciseName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Published by: $exercisePublisher",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Steps:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    exerciseSteps.forEachIndexed { index, step ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(text = "${index + 1}. ", fontWeight = FontWeight.Bold)
                            Text(text = step)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(top = 16.dp)
                    .background(Color(0xFF6200EE), shape = androidx.compose.foundation.shape.CircleShape)
                    .clickable {
                        addExercise(
                            name = exerciseName,
                            publisher = exercisePublisher,
                            steps = exerciseSteps
                        )
                        exerciseName = ""
                        exercisePublisher = ""
                        exerciseSteps.clear()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                state = rememberLazyGridState(),
                modifier = Modifier.fillMaxSize()
            ) {
                items(myList) { data ->
                    Box(modifier = Modifier.padding(12.dp)) {
                        Widgets(data.name, data.publisher, data.imageFileName, data.id, navController)
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    items: SnapshotStateList<String>,
    onExerciseInfoReceived: (ExerciseInfo) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    SearchBar(
        modifier = Modifier
            .fillMaxWidth(),
        query = text,
        onQueryChange = { text = it },
        onSearch = {
            if (text.isNotEmpty()) {
                items.remove(text)
                items.add(0, text)

                active = false

                coroutineScope.launch {
                    try {
                        val exerciseInfo = makeApiRequest(text)
                        onExerciseInfoReceived(exerciseInfo)
                        Log.d("API_RESPONSE", "Exercise Info: $exerciseInfo")
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Error making API request", e)
                    }
                }
            }
        },
        active = active,
        onActiveChange = {
            active = it
            if (active) {
                showHistory = true
            }
        },
        placeholder = { Text("Search for an exercise...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            showHistory = false
                            active = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
    ) {
        if (showHistory) {
            LazyColumn {
                val filteredItems = items.filter { it.contains(text, ignoreCase = true) }

                items(filteredItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                text = item
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item)
                    }
                }
            }
        }
    }
}

suspend fun makeApiRequest(query: String): ExerciseInfo {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val url = URL("$API_URL?key=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Create the request body
            val requestBody = JSONObject().apply {
                put("contents", JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONObject().apply {
                        put(
                            "text",
                            "I will give you a exercise name [if anything other then that is given send msg ignore] it should contain {name, hardness-level, steps[should be three steps on how to do the exercise no more no less should be one liner]} here is the name $query"
                        )
                    })
                })
            }

            val outputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream)
            writer.write(requestBody.toString())
            writer.flush()
            writer.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()

                // Parse the response to extract the text that contains the JSON
                val jsonResponse = JSONObject(response)
                val candidates = jsonResponse.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        val responseText = parts.getJSONObject(0).getString("text")
                        Log.d("API_RAW_RESPONSE", responseText)

                        // Extract the JSON part from the text
                        // We need to find and parse the JSON within the text
                        val jsonPattern = """\{[\s\S]*\}""".toRegex()
                        val jsonMatch = jsonPattern.find(responseText)

                        if (jsonMatch != null) {
                            val jsonString = jsonMatch.value
                            Log.d("API_JSON_EXTRACTED", jsonString)

                            // Parse the exercise info from the JSON
                            val exerciseJson = JSONObject(jsonString)
                            val name = exerciseJson.optString("name", "Unknown Exercise")
                            val publisher = exerciseJson.optString("hardness-level", "Unknown Publisher")

                            val stepsArray = exerciseJson.getJSONArray("steps")
                            val steps = mutableListOf<String>()
                            for (i in 0 until stepsArray.length()) {
                                steps.add(stepsArray.getString(i))
                            }

                            return@withContext ExerciseInfo(name, publisher, steps)
                        } else {
                            Log.e("API_JSON_PARSE", "Could not find JSON in the response")
                            return@withContext ExerciseInfo(
                                "Error",
                                "No JSON found",
                                listOf("Failed to parse exercise information")
                            )
                        }
                    }
                }
                return@withContext ExerciseInfo(
                    "Error",
                    "No content found",
                    listOf("No exercise information found in response")
                )
            } else {
                val errorStream = connection.errorStream
                val error = errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                errorStream?.close()
                Log.e("API_HTTP_ERROR", "Error $responseCode: $error")
                return@withContext ExerciseInfo(
                    "Error",
                    "API Error",
                    listOf("Error $responseCode: $error")
                )
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Exception: ${e.message}", e)
            return@withContext ExerciseInfo("Error", "Exception", listOf("Exception: ${e.message}"))
        }
    }
}