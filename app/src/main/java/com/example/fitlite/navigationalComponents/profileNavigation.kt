package com.example.fitlite.navigationalComponents

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitlite.components.GeneralSettings
import com.example.fitlite.components.SyncSpotify
import com.example.fitlite.components.SyncWatch
import com.example.fitlite.components.VoiceFeedback
import com.example.fitlite.components.WorkoutSettings
import com.example.fitlite.pages.ProfilePage

@Composable
fun ProfileNavigationGraph(navController: NavHostController, modifier: Modifier, context: Context) {
    NavHost(navController = navController, startDestination = "profileLayout") {
        composable("profileLayout") { ProfilePage(navController, modifier)}
        composable("workoutSettings") { WorkoutSettings(navController) }
        composable("generalSettings") { GeneralSettings(navController, modifier, context = context) }
        composable("voiceFeedback") { VoiceFeedback(navController) }
        composable("syncWatch") { SyncWatch(navController) }
        composable("syncSpotify") { SyncSpotify(navController) }
    }
}