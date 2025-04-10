package com.example.uitutorial.navigationalComponents

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uitutorial.ModelRender.FilamentComposeView
import com.example.uitutorial.components.ExerciseActivity
import com.example.uitutorial.components.Exerciselayout

@Composable
fun ExerciseNavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = "exerciseLayout",
        modifier = modifier.size(width = 400.dp, height = 800.dp)
    ) {
        composable("exerciseLayout") { Exerciselayout(navController, modifier) }
        composable("exerciseActivity") { ExerciseActivity(navController, modifier) }
        composable("form3DModel") { FilamentComposeView() }
        composable("poseCheck") {
            Log.d("NotHere", "pass")
            PoseCheck()
        }
    }
}
