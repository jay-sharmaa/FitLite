package com.example.fitlite.presentation.utils

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
fun MyNav(backgroundViewModel: BackgroundViewModel, context : Context){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeLayout(context = context, navController = navController) }
        composable("heartRate") { HeartRatePage(context = context, navController = navController) }
        composable("music") { Music(context = context, navController = navController) }
        composable("oxygen") { Oxygen(context = context, navController = navController) }
        composable("exercise") { Exercise(context, navController = navController) }
        composable("run") { Run(context, navController = navController) }
        composable("walk") { Walk(context, navController = navController) }
    }
}