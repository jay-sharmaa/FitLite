package com.example.uitutorial.navigationalComponents

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uitutorial.ModelRender.FilamentComposeView
import com.example.uitutorial.components.ExerciseActivity
import com.example.uitutorial.components.Exerciselayout

@Composable
fun ExerciseNavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = "exerciseLayout") {
        composable("exerciseLayout") { Exerciselayout(navController) }
        composable("exerciseActivity") { ExerciseActivity(navController, modifier) }
        composable("form3DModel") { FilamentComposeView() }
        composable("poseCheck") {
            Log.d("NotHere", "pass")
            PoseCheck() }
    }
}