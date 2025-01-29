package com.example.uitutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.uitutorial.navigation.DrawerScreen
import com.example.uitutorial.navigation.HelpFeedbackScreen
import com.example.uitutorial.pages.HomePage
import com.example.uitutorial.pages.ProfilePage
import com.example.uitutorial.pages.SettingScreen
import com.example.uitutorial.pages.SettingsPage
import com.example.uitutorial.ui.theme.Purple40
import com.example.uitutorial.ui.theme.UITutorialTheme
import com.example.uitutorial.viewModels.DrawerViewModel
import kotlinx.coroutines.launch

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
    val drawerScreens = listOf(
        DrawerScreen.Home,
        DrawerScreen.Profile,
        DrawerScreen.Settings,
        DrawerScreen.HelpFeedback,
    )

    val navController = rememberNavController()
    var currentScreen by remember { mutableStateOf<DrawerScreen>(DrawerScreen.Home) }
    var intialDrawerState by remember {
        mutableStateOf(0)
    }
    val drawerViewModel: DrawerViewModel = viewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var active by remember {
        mutableStateOf(false)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(drawerViewModel.isDrawerOpen) {
        if(intialDrawerState != 0){
            if (drawerViewModel.isDrawerOpen) {
                drawerState.open()
                drawerViewModel.openDrawer()
            } else {
                drawerState.close()
                drawerViewModel.openDrawer()
            }
        }
        intialDrawerState = 1
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Purple40 // change theme
            ) {
                DrawerHeader()
                Divider()
                DrawerBody(
                    drawerScreens = drawerScreens,
                    onDestinationClicked = { screen ->
                        scope.launch { drawerState.close() }
                        currentScreen = screen
                        navController.navigate(screen.route)
                    }
                )
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = { drawerViewModel.toggleDrawer() }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                        }
                    },
                    title = {
                        Text(
                            text = "FitLite",
                            fontStyle = FontStyle.Normal,
                            fontSize = 32.sp,
                            color = Color.Black
                        )
                    },
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
                )
            },
        ) { paddingValues ->
            // NavHost for managing both drawer and bottom navigation
            NavHost(
                navController = navController,
                startDestination = DrawerScreen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                // Drawer routes
                composable(DrawerScreen.Settings.route) {
                    SettingScreen()
                }
                composable(DrawerScreen.HelpFeedback.route) {
                    HelpFeedbackScreen()
                }

                // Bottom navigation routes
                composable("home") {
                    HomePage()
                }
                composable("profile") {
                    ProfilePage()
                }
                composable("settings") {
                    SettingsPage()
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

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 48.dp)
    ) {
        Text(
            text = "My App",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun DrawerBody(
    drawerScreens: List<DrawerScreen>,
    onDestinationClicked: (DrawerScreen) -> Unit
) {

    drawerScreens.forEach { screen ->
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Box {
                NavigationDrawerItem(
                    modifier = Modifier.fillMaxSize(),
                    icon = {
                        Icon(
                            screen.icon, contentDescription = screen.title, tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = { Text(screen.title, color = Color.Black) },
                    selected = false,
                    onClick = { onDestinationClicked(screen) },
                )
            }
        }
    }
}