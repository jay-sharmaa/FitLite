package com.example.fitlite

import ExploreNavigation
import MyBottomAppBar
import com.example.fitlite.Auth.MyNav
import android.Manifest
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import androidx.compose.foundation.layout.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitlite.data.PersonViewModel
import com.example.fitlite.data.ViewModelFactory
import com.example.fitlite.navigation.BottomScreens
import com.example.fitlite.navigationalComponents.PoseCheck
import com.example.fitlite.navigationalComponents.ProfileNavigationGraph
import com.example.fitlite.pages.ExplorePage
import com.example.fitlite.pages.HomePage
import com.example.fitlite.services.RunningApp
import com.example.fitlite.services.RunningServices
import com.example.fitlite.ui.theme.Purple80
import com.example.fitlite.viewModels.HomePageViewModel
import com.example.fitlite.viewModels.HomePageViewModelFactory
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

val Context.myDataStore by preferencesDataStore(name =  "user_info")

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isTTSReady by mutableStateOf(false)
    private val pipModeState = mutableStateOf(false)

    private val homePageViewModel: HomePageViewModel by viewModels {
        HomePageViewModelFactory(applicationContext)
    }

    private val authViewModel: PersonViewModel by viewModels {
        ViewModelFactory((application as RunningApp).repository)
    }

    private fun stopRunningService() {
        val stopIntent = Intent(this, RunningServices::class.java).apply {
            action = RunningServices.Actions.Stop.toString()
        }
        startService(stopIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        setContent {
            MaterialTheme {
                Surface(
                    shadowElevation = 20.dp
                ) {
                    MyNav(
                        homePageViewModel = homePageViewModel,
                        context = applicationContext,
                        authViewModel = authViewModel,
                        tts = tts,
                        pipModeState = pipModeState
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            isTTSReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        pipModeState.value = isInPictureInPictureMode
        if (!isInPictureInPictureMode) {
            stopRunningService()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(homePageViewModel: HomePageViewModel, context : Context, authViewModel: PersonViewModel, userName: String, tts : TextToSpeech, pipModeState: MutableState<Boolean>) {
    Log.d("MainScreen", userName)
    val homeNavController = rememberNavController()
    val profileNavController = rememberNavController()
    val exploreNavController = rememberNavController()
    var openAlertDialog by remember { mutableStateOf("") }
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    val bottomScreens = listOf(
        BottomScreens.Home,
        BottomScreens.Profile,
        BottomScreens.Settings,
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val scope = rememberCoroutineScope()

    val pagerState: PagerState = rememberPagerState (pageCount = {
        bottomScreens.size
    })

    val currentHomeRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
    val currentProfileRoute = profileNavController.currentBackStackEntryAsState().value?.destination?.route
    val currentExploreRoute = exploreNavController.currentBackStackEntryAsState().value?.destination?.route

    val SAVE_LOGIN_INFO = stringPreferencesKey("login_info")

    val loginInfo by context.myDataStore.data
        .map { preferences -> preferences[SAVE_LOGIN_INFO] ?: "" }
        .collectAsState(initial = "")

    var showDialog = loginInfo.isEmpty()

    if (showDialog) {
        AlertDialog(
            title = {
                Text(text = "Save Info")
            },
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openAlertDialog = userName
                        coroutine.launch {
                            context.myDataStore.edit { preferences ->
                                preferences[SAVE_LOGIN_INFO] = userName
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Never")
                }
            }
        )
    }


    if(pipModeState.value){
        PiPScreen()
    }

    else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if ((currentHomeRoute == "exerciseLayout" && currentProfileRoute == null) || (currentProfileRoute == "profileLayout" && currentHomeRoute == null) ||
                    (currentHomeRoute == "exerciseLayout" && currentProfileRoute == "profileLayout")
                ) {
                    TopAppBar(
                        scrollBehavior = scrollBehavior,
                        title = {
                            Text(
                                text = "FitLite",
                                fontStyle = FontStyle.Normal,
                                fontSize = 32.sp,
                                color = Color.Black
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Purple80,
                            titleContentColor = Color.White
                        ),
                        actions = {
                            IconButton(onClick = {
                                val intent = Intent(context, RunningServices::class.java).apply {
                                    action = RunningServices.Actions.Start.toString()
                                }
                                startForegroundService(context, intent)
                                activity?.enterPipMode()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Start",
                                    tint = Color.Black
                                )
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (check(currentHomeRoute, currentProfileRoute, currentExploreRoute)) {
                    Box(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 15.dp, start = 15.dp, end = 15.dp)
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
            }
        ) { padding ->
            Box(
                contentAlignment = Alignment.TopCenter
            ) {
                Log.d("Screen", currentHomeRoute.toString())
                Log.d("Screen", currentExploreRoute.toString())
                Log.d("Screen", currentProfileRoute.toString())
                HorizontalPager(
                    modifier = Modifier.padding(top = padding.calculateTopPadding()),
                    state = pagerState,
                    userScrollEnabled = check(
                        currentHomeRoute,
                        currentProfileRoute,
                        currentExploreRoute
                    )
                ) { page ->
                    when (page) {
                        0 -> HomePage(
                            homePageViewModel,
                            context,
                            homeNavController,
                            Modifier,
                            authViewModel,
                            tts
                        )

                        1 -> ExploreNavigation(
                            navController = exploreNavController,
                            Modifier,
                            context,
                            tts
                        )

                        2 -> ProfileNavigationGraph(
                            profileNavController,
                            modifier = Modifier,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

fun check(currentHomeRoute: String?, currentProfileRoute: String?, currentExploreRoute: String?): Boolean {
    return (
            (currentHomeRoute == "exerciseLayout" && currentProfileRoute == null && currentExploreRoute == null) ||
            (currentHomeRoute == null && currentProfileRoute == "profileLayout" && currentExploreRoute == null) ||
            (currentHomeRoute == "exerciseLayout" && currentProfileRoute == "profileLayout" && currentExploreRoute == null) ||
            (currentHomeRoute == null && currentProfileRoute == null && currentExploreRoute == "explorePage") ||
            (currentHomeRoute == "exerciseLayout" && currentProfileRoute == null && currentExploreRoute == "explorePage") ||
            (currentHomeRoute == null && currentProfileRoute == "profileLayout" && currentExploreRoute == "explorePage") ||
            (currentHomeRoute == "exerciseLayout" && currentProfileRoute == "profileLayout" && currentExploreRoute == "explorePage")
    )
}

fun Activity.enterPipMode() {
    val aspectRatio = Rational(16, 9)
    val pipParams = PictureInPictureParams.Builder()
        .setAspectRatio(aspectRatio)
        .build()
    enterPictureInPictureMode(pipParams)
}

@Composable
fun PiPScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "FitLite is running...",
            fontSize = 18.sp,
            color = Color.White
        )
    }
}
