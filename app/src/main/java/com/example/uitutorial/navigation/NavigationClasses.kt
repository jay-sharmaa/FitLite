package com.example.uitutorial.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class to represent screens
sealed class DrawerScreen(val route: String, val title: String, val icon: ImageVector) {

    data object Home : DrawerScreen(route = "home", title = "Home", Icons.Filled.Home)

    data object Profile : DrawerScreen(route = "profile", title = "Profile", Icons.Filled.Person)

    data object Settings : DrawerScreen(route = "app_settings", title = "App Settings", Icons.Filled.Home)

    data object HelpFeedback : DrawerScreen(route = "helpFeedback", title = "Help Feedback", Icons.Filled.Person)
}


