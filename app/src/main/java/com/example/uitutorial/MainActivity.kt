package com.example.uitutorial

import MyBottomAppBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitutorial.navigation.DrawerScreen
import com.example.uitutorial.pages.HomePage
import com.example.uitutorial.pages.ProfilePage
import com.example.uitutorial.pages.SettingsPage
import com.example.uitutorial.ui.theme.Purple40
import com.example.uitutorial.ui.theme.UITutorialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UITutorialTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen() {
    val bottomScreens = listOf(
        DrawerScreen.Home,
        DrawerScreen.Profile,
        DrawerScreen.Settings,
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
                    containerColor = Purple40, // change color
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            BadgedBox(badge = {
                                Badge { Text("2") }
                            }) {
                                Icon(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { },
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "Person",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            ) },
        bottomBar = {
            MyBottomAppBar(pagerState = pagerState, scope = scope)
        }
        ){padding ->
            HorizontalPager(modifier = Modifier.padding(padding), state = pagerState) { page ->
                when(page){
                    0-> HomePage()
                    1-> SettingsPage()
                    2-> ProfilePage()
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
