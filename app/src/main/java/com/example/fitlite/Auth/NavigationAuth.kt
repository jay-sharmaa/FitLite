package com.example.fitlite.Auth

import LoginPage
import SignUpPage
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitlite.MainScreen
import com.example.fitlite.data.PersonViewModel
import com.example.fitlite.myDataStore
import com.example.fitlite.viewModels.HomePageViewModel
import kotlinx.coroutines.flow.map

@Composable
fun MyNav(
    homePageViewModel: HomePageViewModel,
    context: Context,
    authViewModel: PersonViewModel,
    tts: TextToSpeech,
    pipModeState: MutableState<Boolean>
) {
    val navController = rememberNavController()
    val SAVE_LOGIN_INFO = stringPreferencesKey("login_info")

    Log.d("Temp1", SAVE_LOGIN_INFO.toString())

    val user by context.myDataStore.data
        .map { preferences -> preferences[SAVE_LOGIN_INFO] ?: "" }
        .collectAsState(initial = "")

    Log.d("Temp2", user)

    LaunchedEffect(user) {
        if (user.isNotEmpty()) {
            navController.navigate("mainScreen/$user") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {  }

        composable("login") {
            LoginPage(navController, authViewModel, context, user)
        }

        composable("signup") {
            SignUpPage(navController, authViewModel, context)
        }

        composable("mainScreen/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")
            if (userName != null) {
                MainScreen(
                    homePageViewModel = homePageViewModel,
                    context = context,
                    authViewModel = authViewModel,
                    userName = userName,
                    tts = tts,
                    pipModeState = pipModeState
                )
            }
        }
    }
}