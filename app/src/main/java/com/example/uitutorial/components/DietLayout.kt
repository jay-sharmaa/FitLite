package com.example.uitutorial.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DietPage(modifier: Modifier) {
    val dietaryMaterials = listOf("Spinach", "Quinoa", "Almonds", "Greek Yogurt", "Blueberries")
    val benefits = listOf(
        "Rich in vitamins and minerals",
        "High in fiber and protein",
        "Supports heart health",
        "Improves digestion",
        "Boosts immunity"
    )

    // Animation trigger
    var visible by remember { mutableStateOf(false) }

    // Trigger the animation shortly after the page loads
    LaunchedEffect(Unit) {
        delay(200) // Optional: delay to feel more natural
        visible = true
    }

    LazyColumn(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(width = 300.dp, height = 200.dp)
                    .background(Color(0xFF81C784)) // Light green
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(spring(stiffness = Spring.StiffnessLow),initialOffsetX = { fullWidth -> fullWidth })
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
                        dietaryMaterials.forEach {
                            Text("• $it", fontSize = 20.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Benefits of Intake",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        benefits.forEach {
                            Text("• $it", fontSize = 20.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
