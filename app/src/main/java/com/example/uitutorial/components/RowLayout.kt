package com.example.uitutorial.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitutorial.myList
import com.example.uitutorial.ui.theme.Gray40
import com.example.uitutorial.viewModels.HomePageViewModel

@Composable
fun RowLayout(viewModel : HomePageViewModel){
    val daysWorkout : Int = viewModel.daysWorkout.value
    ElevatedCard(
        modifier = Modifier.height(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = Gray40
        )
    ) {
        Column {
            Text(text = "7x${daysWorkout} Weekly Challenge", style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(all = 5.dp))
            Divider(modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(fraction = 0.9f))

            LazyRow(
                state = rememberLazyListState()
            ) {
                items(myList) { data ->
                    Widgets(data.name, data.publisher, data.imageVector)
                }
            }
        }
    }
}