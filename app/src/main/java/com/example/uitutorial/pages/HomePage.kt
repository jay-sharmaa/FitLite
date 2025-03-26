package com.example.uitutorial.pages

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.uitutorial.components.DietLayout
import com.example.uitutorial.data.PersonViewModel
import com.example.uitutorial.navigationalComponents.ExerciseNavigationGraph
import com.example.uitutorial.paging.PagingViewModel
import com.example.uitutorial.ui.theme.Purple120
import com.example.uitutorial.viewModels.HomePageViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.paging.LoadState
import com.example.uitutorial.paging.AppDatabase
import com.example.uitutorial.paging.provideRetrofit

val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Composable
fun HomePage(viewModel: HomePageViewModel, context: Context, navController: NavHostController, modifier: Modifier, authViewModel: PersonViewModel) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val apiService = provideRetrofit()
    val database = AppDatabase.getDatabase(context)
    val pagingviewModel = PagingViewModel(apiService, database)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 8.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize() // LazyColumn handles scrolling automatically
        ) {
            item {
                Log.d("Where is Waldo", currentRoute.toString())

                if (currentRoute == "exerciseLayout")
                    WeeklyCard(viewModel, context)

                if (currentRoute == "exerciseLayout")
                    Spacer(modifier = Modifier.height(10.dp))

                ExerciseNavigationGraph(navController = navController, modifier)

                if (currentRoute == "exerciseLayout")
                    Spacer(modifier = Modifier.height(10.dp))

                if (currentRoute == "exerciseLayout")
                    DietLayout()

                if (currentRoute == "exerciseLayout")
                    PostListScreen(pagingviewModel)
            }
        }
    }
}

@Composable
fun PostListScreen(viewModel: PagingViewModel = viewModel()) {
    val lazyPagingItems = viewModel.posts.collectAsLazyPagingItems()

    LazyColumn {
        items(lazyPagingItems.itemCount) { index ->
            val post = lazyPagingItems[index]
            post?.let {
                Text(text = it.title, modifier = Modifier.padding(8.dp))
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { Text("Loading...", modifier = Modifier.padding(8.dp)) }
                }
                loadState.append is LoadState.Loading -> {
                    item { Text("Loading more...", modifier = Modifier.padding(8.dp)) }
                }
                loadState.refresh is LoadState.Error -> {
                    item { Text("Error loading data", modifier = Modifier.padding(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun PrettyDietCard(
    dietName: String,
    dietaryType: String,
    image: Int,
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Image(
            painter = painterResource(id = image),
            contentDescription = dietName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 300f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = dietName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dietaryType,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun WeeklyCard(viewModel: HomePageViewModel, context: Context) {

    val openAlertDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val dayWorkOut by viewModel.daysWorkout.collectAsState()

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Weekly goal", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(dayWorkOut.toString())
                }
                withStyle(style = SpanStyle()) {
                    append("/7") // out of days
                }
            },
                modifier = Modifier.clickable {
                    scope.launch {
                        openAlertDialog.value = true
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        CircleChip(Modifier.fillMaxWidth())
    }
    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                setDays = {
                    viewModel.setDaysWorkout(it.toInt())
                },
                onDismissRequest = {
                    openAlertDialog.value = false
                },
                onConfirmation = {
                    openAlertDialog.value = false
                },
                dialogText = "Set your weekly goal",
                context = context
            )
        }
    }
}

@Composable
fun CircleChip(modifier: Modifier) {
    var backgroundColor: Color = Purple120
    val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat")
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        items(days.size){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(35.dp))
                    .background(
                        color = backgroundColor
                    )
            ){
                Box(
                    modifier = Modifier
                        .size(width = 50.dp,
                            height = 50.dp)
                ){
                    Text(days[it], modifier = Modifier.align(alignment = Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun AlertDialogExample(
    setDays: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogText: String,
    context: Context,
) {
    var textFieldValue by remember { mutableStateOf("") }
    AlertDialog(
        title = {
            Text(text = dialogText)
        },
        text = {
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(text = "")
                },
                value = textFieldValue,
                onValueChange = {
                    if (it.toInt() > 7) {
                        Toast.makeText(
                            context,
                            "Select Appropriate Number Of Days",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        textFieldValue = it
                    }
                }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                    setDays(textFieldValue)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}