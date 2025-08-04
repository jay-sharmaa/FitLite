package com.example.fitlite.navigationalComponents

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitlite.ModelRender.FilamentComposeView
import com.example.fitlite.components.DietPage
import com.example.fitlite.components.ExerciseActivity
import com.example.fitlite.components.Exerciselayout
import com.example.fitlite.pages.MakeDietPlan

@Composable
fun ExerciseNavigationGraph(navController: NavHostController, modifier: Modifier, context: Context, tts: TextToSpeech) {
    NavHost(
        navController = navController,
        startDestination = "exerciseLayout",
    ) {
        composable("exerciseLayout") { Exerciselayout(navController, modifier) }
        composable(
            route = "exerciseActivity/{temp}",
            arguments = listOf(
                navArgument("temp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dataId = backStackEntry.arguments?.getString("temp")
            ExerciseActivity(navController, Modifier.size(410.dp, 1000.dp), dataId!!, context, tts)
        }
        composable(
            route = "form3DModel/{dataId}",
            arguments = listOf(
                navArgument("dataId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dataId = backStackEntry.arguments?.getString("dataId")
            FilamentComposeView(Modifier.size(410.dp, 1000.dp), dataId!!)
        }
        composable(
            route = "poseCheck/{dataId}",
            arguments = listOf(
                navArgument("dataId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dataId = backStackEntry.arguments?.getString("dataId")
            PoseCheck(
                modifier = Modifier.size(410.dp, 850.dp), navController = navController, dataId = dataId!!,
            )
        }
        composable(
            route = "dietPage/{dietType}/{image}",
            arguments = listOf(
                navArgument("dietType") { type = NavType.StringType },
                navArgument("image") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dietType = backStackEntry.arguments?.getString("dietType")
            val image = backStackEntry.arguments?.getString("image")
            DietPage(modifier = Modifier.size(410.dp, 1000.dp), dietName = dietType!!,
                context = context, oldImage = image!!)
        }
        composable(
            "MakePlan"
        ) {
            MakeDietPlan(modifier = Modifier.size(410.dp, 850.dp, ), navController)
        }
    }
}
