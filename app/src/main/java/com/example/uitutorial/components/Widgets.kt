package com.example.uitutorial.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.uitutorial.ui.theme.Gray40
import java.io.IOException

@Composable
fun Widgets(name: String, publisher: String, imageFileName: String, dataId: Int, navController: NavHostController) {
    val context = LocalContext.current
    val bitmap = remember(imageFileName) {
        try {
            val inputStream = context.assets.open(imageFileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }

    Box(
        modifier = Modifier
            .size(width = 200.dp, height = 180.dp)
            .background(color = Gray40)
            .clickable {
                val temp : Int = dataId
                navController.navigate("exerciseActivity/$temp")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = name, fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = publisher, fontSize = 12.sp)
        }
    }
}
