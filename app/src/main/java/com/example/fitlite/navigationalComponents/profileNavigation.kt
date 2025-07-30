package com.example.fitlite.navigationalComponents

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitlite.components.ExerciseActivity
import com.example.fitlite.components.Exerciselayout
import com.example.fitlite.components.GeneralSettings
import com.example.fitlite.components.SyncSpotify
import com.example.fitlite.components.SyncWatch
import com.example.fitlite.components.VoiceFeedback
import com.example.fitlite.components.WorkoutSettings
import com.example.fitlite.data.PersonViewModel
import com.example.fitlite.pages.ProfilePage

@Composable
fun ProfileNavigationGraph(navController: NavHostController, modifier: Modifier, authViewModel: PersonViewModel, userName: String, context: Context, tts: TextToSpeech) {
    NavHost(navController = navController, startDestination = "profileLayout") {
        composable("profileLayout") { ProfilePage(navController, authViewModel, userName, modifier)}
        composable("workoutSettings") { WorkoutSettings(navController) }
        composable("generalSettings") { GeneralSettings(navController) }
        composable("voiceFeedback") { VoiceFeedback(navController) }
        composable("syncWatch") { SyncWatch(navController) }
        composable("syncSpotify") { SyncSpotify(navController) }
    }
}