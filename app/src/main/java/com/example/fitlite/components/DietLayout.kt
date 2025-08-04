package com.example.fitlite.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.fitlite.paging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.IOException
import java.util.Locale

@Composable
fun DietPage(modifier: Modifier, dietName: String, context: Context, oldImage: String) {
    val TAG = "DietPage"
    val coroutineScope = rememberCoroutineScope()

    val image = "images/$oldImage"
    val apiService = provideRetrofit()
    val database = AppDatabase.getDatabase(context)

    val factory = remember(dietName) {
        PagingViewModelFactory(apiService, database, dietName)
    }

    val viewModel: PagingViewModel = viewModel(factory = factory)

    var isRefreshing by remember { mutableStateOf(false) }

    val displayName = remember(dietName) {
        if (dietName.contains(" ")) {
            dietName.substringAfter(" ")
        } else {
            dietName
        }
    }

    var needsForceRefresh by remember { mutableStateOf(false) }

    // Collect the paging items
    val lazyPagingItems = viewModel.posts.collectAsLazyPagingItems()

    // Animation trigger
    var visible by remember { mutableStateOf(false) }

    // Trigger the animation shortly after the page loads
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    // Monitor data loading state for debugging
    LaunchedEffect(lazyPagingItems.loadState.refresh) {
        when (val refreshState = lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                Log.d(TAG, "Refresh loading state for '$dietName'")
                isRefreshing = true
            }
            is LoadState.Error -> {
                Log.e(TAG, "Refresh error for '$dietName': ${refreshState.error.message}")
                isRefreshing = false
                // If we get an error and have no data, we should try force refresh
                if (lazyPagingItems.itemCount == 0) {
                    needsForceRefresh = true
                }
            }
            is LoadState.NotLoading -> {
                isRefreshing = false
                Log.d(TAG, "Refresh complete for '$dietName'. Item count: ${lazyPagingItems.itemCount}")

                // If we still have no data after a normal refresh, try force refresh
                if (lazyPagingItems.itemCount == 0 && needsForceRefresh) {
                    needsForceRefresh = false
                    Log.d(TAG, "No items after refresh, triggering force refresh for '$dietName'")
                }
            }
        }
    }

    // Load the image asset
    val bitmap = remember(image) {
        try {
            val inputStream = context.assets.open(image)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to load image: $image", e)
            null
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Manual refresh button for testing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Diet: ${displayName.uppercase()}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Button(
                onClick = {
                    Log.d(TAG, "Manual refresh requested for '$dietName'")
                    coroutineScope.launch {
                        viewModel.refreshData()
                    }
                }
            ) {
                Text("Refresh Data")
            }
        }

        // Debug info
        Text(
            "Items: ${lazyPagingItems.itemCount} | Loading: ${isRefreshing}",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image at top
            item {
                bitmap?.let {
                    Log.d(TAG, "Displaying image for '$dietName'")
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Diet Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Loading state
            when (lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    if (lazyPagingItems.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Loading nutrition data...",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                is LoadState.Error -> {
                    if (lazyPagingItems.itemCount == 0) {
                        item {
                            val error = (lazyPagingItems.loadState.refresh as LoadState.Error).error
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Error loading nutrition data: ${error.localizedMessage ?: "Check internet connection."}",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    Log.d(TAG, "Retry requested after error for '$dietName'")
                                    lazyPagingItems.refresh()
                                }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
                is LoadState.NotLoading -> {
                    if (lazyPagingItems.itemCount == 0) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No nutrition data found for ${displayName.uppercase()}",
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    Log.d(TAG, "Force refresh requested for '$dietName'")
                                    coroutineScope.launch {
                                        viewModel.refreshData()
                                    }
                                }) {
                                    Text("Force Refresh")
                                }
                            }
                        }
                    }
                }
            }

            // Display nutrition data items
            items(lazyPagingItems.itemCount) { index ->
                val post = lazyPagingItems[index]
                post?.let {
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            spring(stiffness = Spring.StiffnessLow),
                            initialOffsetX = { fullWidth -> fullWidth }
                        )
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Dietary Materials",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    displayName.uppercase(Locale.ROOT),
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fiber: ${it.fiber_g} grams")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Sugar: ${it.sugar_g} grams")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fat: ${it.fat_total_g} grams")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Cholesterol: ${it.cholesterol_mg} mg")
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fat Saturated: ${it.fat_saturated_g} grams")
                            }
                        }
                    }
                }
            }

            // Append loading state (for pagination)
            when (lazyPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
                is LoadState.Error -> {
                    item {
                        val error = (lazyPagingItems.loadState.append as LoadState.Error).error
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Error loading more: ${error.localizedMessage ?: "Check connection"}",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { lazyPagingItems.retry() },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is LoadState.NotLoading -> {

                }
            }
        }
    }
}