package com.example.uitutorial.navigationalComponents

import android.speech.tts.Voice
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uitutorial.components.ExerciseActivity
import com.example.uitutorial.components.Exerciselayout
import com.example.uitutorial.components.GeneralSettings
import com.example.uitutorial.components.SyncSpotify
import com.example.uitutorial.components.SyncWatch
import com.example.uitutorial.components.VoiceFeedback
import com.example.uitutorial.components.WorkoutSettings
import com.example.uitutorial.pages.ProfilePage

@Composable
fun ProfileNavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = "profileLayout") {
        composable("profileLayout") { ProfilePage(navController)}
        composable("workoutSettings") { WorkoutSettings() }
        composable("generalSettings") { GeneralSettings() }
        composable("voiceFeedback") { VoiceFeedback() }
        composable("syncWatch") { SyncWatch() }
        composable("syncSpotify") { SyncSpotify() }
    }
}