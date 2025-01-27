package com.example.uitutorial.viewModels

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DrawerViewModel : ViewModel() {
    // Manage drawer state inside the ViewModel
    var isDrawerOpen by mutableStateOf(false)
        private set

    fun toggleDrawer() {
        isDrawerOpen = !isDrawerOpen
    }

    fun openDrawer() {
        isDrawerOpen = true
    }

    fun closeDrawer() {
        isDrawerOpen = false
    }
}