package com.example.fitlite.presentation.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BackGroundViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BackgroundViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BackgroundViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}