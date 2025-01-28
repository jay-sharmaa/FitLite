package com.example.uitutorial

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// Sealed class to represent screens
sealed class DrawerScreen(val route: String, val title: String, val icon: ImageVector) {
    object Settings : DrawerScreen("settings", "Settings", Icons.Filled.Home)
    object HelpFeedback : DrawerScreen("helpFeedback", "HelpFeedback", Icons.Filled.Person)
}

@Composable
fun SettingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Settings Screen")
    }
}

@Composable
fun HelpFeedbackScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Help Screen")
    }
}
