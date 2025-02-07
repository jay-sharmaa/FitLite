package com.example.uitutorial.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class to represent screens
sealed class BottomScreens(val route: String, val title: String, val icon: ImageVector) {

    data object Home : BottomScreens(route = "home", title = "Home", Icons.Filled.Home)

    data object Profile : BottomScreens(route = "profile", title = "Profile", Icons.Filled.Person)

    data object Settings : BottomScreens(route = "settings", title = "Settings", Icons.Filled.Settings)
}


