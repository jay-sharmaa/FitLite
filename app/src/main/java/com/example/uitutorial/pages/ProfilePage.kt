package com.example.uitutorial.pages

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.uitutorial.R
import com.example.uitutorial.data.PersonViewModel
import kotlinx.coroutines.delay

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(2)
    }
}

@Composable
fun ProfilePage(navController: NavHostController, authViewModel: PersonViewModel, userName: String, modifier: Modifier) {
    val context = LocalContext.current
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        loadProgress {progress ->
            currentProgress = progress
        }
        loading = false
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
            Text(userName, modifier = Modifier.align(Alignment.BottomCenter), style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black))
            CircularProgressIndicator(progress = currentProgress,
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomCenter),
                strokeWidth = 5.dp)
        }

        ProfileSettingTile(title = "Workout Settings", navController, "workoutSettings")
        Divider()
        ProfileSettingTile(title = "General Settings", navController, "generalSettings")
        Divider()
        ProfileSettingTile(title = "Voice Feedback", navController, "voiceFeedback")
        Divider()
        ProfileSettingTile(title = "Sync Watch", navController, "syncWatch")
        Divider()
        ProfileSettingTile(title = "Sync Spotify", navController, "syncSpotify")

        Column(modifier = Modifier.padding(16.dp)) {

            TextDivider(text = "Support")

        }

        Text(
            text = "Share with friends",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                ) {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
                .padding(16.dp)
        )

        // Feedback
        Text(
            text = "Feedback",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val url = "https://example.com/feedback"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
                .padding(16.dp)
        )

        // More from developer
        Text(
            text = "More from developer",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val url = "https://example.com/developer"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
                .padding(16.dp)
        )

    }
}

@Composable
fun ProfileSettingTile(title: String, navController: NavHostController, route : String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                navController.navigate(route = route)
            }
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 20.sp)
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

@Composable
fun TextDivider(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    thickness: Dp = 1.dp,
    padding: Dp = 8.dp,
    textColor: Color = Color.Gray,
    fontSize: TextUnit = 14.sp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = color,
            thickness = thickness,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = padding)
        )
        Divider(
            color = color,
            thickness = thickness,
            modifier = Modifier.weight(1f)
        )
    }
}
