package com.example.uitutorial.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uitutorial.R
import kotlinx.coroutines.delay
@Composable
@Preview(showSystemUi = true)
fun ProfilePage() {
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        loadProgress {progress ->
            currentProgress = progress
        }

        loading = false
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ){
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painterResource(id = R.drawable.background_fitness),
                contentDescription = "Background",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(bottomRoundedShape(100.dp))

            )
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.White)
                    .padding(all = 5.dp)
                    .size(120.dp)
                    .align(Alignment.BottomCenter)
            )
            CircularProgressIndicator(progress = currentProgress,
                modifier =  Modifier.size(130.dp)
                    .align(Alignment.BottomCenter),
                strokeWidth = 5.dp)
        }
        Box(
            modifier = Modifier
                .size(420.dp)
                .background(color = Color.Blue)
        ){

        }
        Box(
            modifier = Modifier
                .size(420.dp)
                .background(color = Color.Red)
        ){

        }
    }
}

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(3)
    }
}

fun bottomRoundedShape(radius: Dp): Shape {
    return RoundedCornerShape(
        topStart = CornerSize(0.dp),
        topEnd = CornerSize(0.dp),
        bottomStart = CornerSize(radius),
        bottomEnd = CornerSize(radius)
    )
}