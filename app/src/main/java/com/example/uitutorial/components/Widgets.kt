package com.example.uitutorial.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.uitutorial.ui.theme.Gray40

@Composable
fun Widgets(name: String, publisher: String, imageVector: Int, navController: NavHostController) {
    Box(
        modifier = Modifier
            .size(width = 200.dp, height = 180.dp)
            .background(color = Gray40)
            .clickable {
                navController.navigate("exerciseActivity")
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = imageVector),
                contentDescription = "Image Loaded"
            )
            Column(
                modifier = Modifier.width(200.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("$name ", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "$publisher ", fontSize = 12.sp)
            }
        }
    }
}