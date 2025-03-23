package com.example.uitutorial.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.uitutorial.api.ApiViewModel
import com.example.uitutorial.api.Post
import com.example.uitutorial.dietList
import com.example.uitutorial.myList
import com.example.uitutorial.pages.PrettyDietCard
import com.example.uitutorial.ui.theme.Gray40

@Composable
fun Exerciselayout(navController: NavHostController) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = Gray40
        )
    ) {
        Column {
            Text(text = "Recommended Exercise", style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(all = 5.dp))
            Divider(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(fraction = 0.9f))

            LazyRow(
                state = rememberLazyListState()
            ) {
                items(myList) { data ->
                    Widgets(data.name, data.publisher, data.imageVector, navController)
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DietLayout(){
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Gray40
        ),
        modifier = Modifier.height(200.dp)
    ) {
        Column {
            Text(
                "Dietary Preferences", style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(all = 5.dp)
            )
            Divider(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(fraction = 0.9f)
            )

            LazyRow(
                state = rememberLazyListState()
            ) {
                items(dietList) { diet ->
                    PrettyDietCard(
                        dietName = diet.dietName,
                        dietaryType = diet.dietaryType,
                        image = diet.imageVector
                    )
                }
            }

        }
    }
}