package com.example.uitutorial

import MyBottomAppBar
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitutorial.navigation.BottomScreens
import com.example.uitutorial.pages.HomePage
import com.example.uitutorial.pages.ProfilePage
import com.example.uitutorial.pages.SettingsPage
import com.example.uitutorial.ui.theme.Purple40
import com.example.uitutorial.ui.theme.Purple80
import com.example.uitutorial.viewModels.HomePageViewModel
import com.example.uitutorial.viewModels.HomePageViewModelFactory


class MainActivity : ComponentActivity() {
    private val homePageViewModel: HomePageViewModel by viewModels {
        HomePageViewModelFactory(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    shadowElevation = 20.dp
                ) {
                    MainScreen(homePageViewModel, applicationContext)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(homePageViewModel: HomePageViewModel, context : Context) {


    val bottomScreens = listOf(
        BottomScreens.Home,
        BottomScreens.Settings,
        BottomScreens.Profile,
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var active by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val pagerState: PagerState = rememberPagerState (pageCount = {
        bottomScreens.size
    })

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "FitLite",
                        fontStyle = FontStyle.Normal,
                        fontSize = 32.sp,
                        color = Color.Black
                    ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple80,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (!active) {
                        IconButton(onClick = { active = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Black
                            )
                        }
                    }
                }
            ) },
        bottomBar = {
            Box(
                modifier = Modifier.navigationBarsPadding().padding(bottom = 15.dp, start = 15.dp, end = 15.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        shape = RoundedCornerShape(20.dp),
                        width = 0.dp,
                        color = Color.White
                    )
            )
            {
                MyBottomAppBar(pagerState = pagerState, scope = scope)
            }
        }
        ){padding ->
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            HorizontalPager(modifier = Modifier.padding(padding), state = pagerState) { page ->
                when (page) {
                    0 -> HomePage(homePageViewModel, context)
                    1 -> SettingsPage()
                    2 -> ProfilePage()
                }
            }
        }
    }

    if (active) {
        MySearchBar(onClose = { active = false })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(onClose: ()->Unit) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }
    val items = remember {
        mutableStateListOf(
            "Android developer",
            "Flutter developer"
        )
    }
    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = text,
        onQueryChange = { text = it },
        onSearch = {
            if (text.isNotEmpty()) {
                items.add(text)
            }
            onClose()
        },
        active = active,
        onActiveChange = { active = it
            if(!active) onClose()},
        placeholder = { Text("Search...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            onClose()
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
    ) {
        LazyColumn {
            items(items.filter { it.contains(text, ignoreCase = true) }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 14.dp)
                        .clickable {
                            text = item
                            onClose()
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (items.indexOf(item) == 0) {
                            Icons.Default.Search
                        } else {
                            Icons.Default.Refresh
                        },
                        contentDescription = if (items.indexOf(item) == 0) "Search" else "History"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item)
                }
            }
        }
    }
}
