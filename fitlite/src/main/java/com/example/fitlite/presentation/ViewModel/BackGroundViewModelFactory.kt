package com.example.fitlite.presentation.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BackGroundViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BackgroundViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BackgroundViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}