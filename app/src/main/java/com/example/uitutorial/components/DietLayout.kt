package com.example.uitutorial.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.uitutorial.paging.ApiService
import com.example.uitutorial.paging.AppDatabase
import com.example.uitutorial.paging.PagingViewModel
import com.example.uitutorial.paging.PagingViewModelFactory
import com.example.uitutorial.paging.provideRetrofit
import kotlinx.coroutines.delay
import okio.IOException
import java.util.Locale

@Composable
fun DietPage(modifier: Modifier, dietName: String, context: Context, oldImage: String) {
    val image = "images/$oldImage"
    val apiService = provideRetrofit()
    val database = AppDatabase.getDatabase(context)

    val factory = remember {
        PagingViewModelFactory(apiService, database, dietName)
    }

    val viewModel: PagingViewModel = viewModel(factory = factory)

    val postsFlow = remember { viewModel.posts }
    val lazyPagingItems = postsFlow.collectAsLazyPagingItems()

    // Animation trigger
    var visible by remember { mutableStateOf(false) }

    // Trigger the animation shortly after the page loads
    LaunchedEffect(Unit) {
        Log.d("DietName", dietName)
        delay(200)
        visible = true
    }

    val bitmap = remember(image){
        try{
            val inputStream = context.assets.open(image)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            bitmap?.let {
                Log.d("Loaded", "img")
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Image Loaded",
                    modifier = Modifier.size(400.dp)
                )
            }
        }

        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                if (lazyPagingItems.itemCount == 0) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
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
                                "Error loading data: ${error.localizedMessage ?: "Check internet connection."}",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { lazyPagingItems.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
            is LoadState.NotLoading -> {
                // No action needed for initial load completion
            }
        }

        items(lazyPagingItems.itemCount) { index ->
            val post = lazyPagingItems[index]
            post?.let {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow), initialOffsetX = {fullWidth -> fullWidth})
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Dietary Materials",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it.name.uppercase(Locale.ROOT), fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Fiber: ${it.fiber_g}grams")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sugar: ${it.sugar_g}grams")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Fat: ${it.fat_total_g}grams")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Cholesterol: ${it.cholesterol_mg}grams")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Fat Saturated: ${it.fat_saturated_g}grams")
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

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
