package com.example.fitlite.presentation.utils

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitlite.presentation.ViewModel.BackgroundViewModel
import com.example.fitlite.presentation.pages.Exercise
import com.example.fitlite.presentation.pages.HeartRatePage
import com.example.fitlite.presentation.pages.HomeLayout
import com.example.fitlite.presentation.pages.Music
import com.example.fitlite.presentation.pages.Oxygen
import com.example.fitlite.presentation.pages.Run
import com.example.fitlite.presentation.pages.Walk

@Composable
fun MyNav(backgroundViewModel: BackgroundViewModel, context: Context, application: Application){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeLayout(context = context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
        composable("heartRate") { HeartRatePage(context = context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
        composable("music") { Music(context = context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
        composable("oxygen") { Oxygen(context = context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
        composable("exercise") { Exercise(context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
        composable("run") { Run(context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
        composable("walk") { Walk(context,
            navController = navController, backgroundViewModel = BackgroundViewModel(application)) }
    }
}