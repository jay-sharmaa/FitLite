package com.example.uitutorial.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.example.uitutorial.myList

@Composable
fun RowLayout(){
    LazyRow(
        state = rememberLazyListState()
    ) {
        items(myList){data->
            Widgets(data.name, data.publisher, data.imageVector)
        }
    }
}
