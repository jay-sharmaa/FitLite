package com.example.uitutorial.pages

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

val Purple80 = Color(0xFFE7DDF7)

@Composable
fun SettingsPage() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Share with friends
            Text(
                text = "Share with friends",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Purple80)
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
                    .background(Purple80)
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
                    .background(Purple80)
                    .clickable {
                        val url = "https://example.com/developer"
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SettingsPagePreview() {
    SettingsPage()
}
