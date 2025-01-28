package com.example.uitutorial.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class to represent screens
sealed class DrawerScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Settings : DrawerScreen("app_settings", "App Settings", Icons.Filled.Home)
    data object HelpFeedback : DrawerScreen("helpFeedback", "Help Feedback", Icons.Filled.Person)
}


