package com.example.uitutorial.Auth

import LoginPage
import SignUpPage
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.uitutorial.MainScreen
import com.example.uitutorial.data.PersonViewModel
import com.example.uitutorial.myDataStore
import com.example.uitutorial.viewModels.HomePageViewModel
import kotlinx.coroutines.flow.map

@Composable
fun MyNav(homePageViewModel: HomePageViewModel, context : Context, authViewModel: PersonViewModel){
    val navController = rememberNavController()

    val SAVE_LOGIN_INFO = stringPreferencesKey("login_info")

    val user by context.myDataStore.data
        .map { preferences -> preferences[SAVE_LOGIN_INFO] ?: "" }
        .collectAsState(initial = "")

    Log.d("navHost", user.length.toString())
    val startDestination = if (user.isNotEmpty()) "mainScreen/$user" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        if(user == ""){
            composable("login") { LoginPage(navController, authViewModel, context, user) }
            composable("signup") { SignUpPage(navController, authViewModel, context) }
        }
        composable("mainScreen/{userName}") {
                backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")
            if (userName != null) {
                MainScreen(homePageViewModel = homePageViewModel, context = context, authViewModel = authViewModel, userName = userName)
            }
        }
    }
}
