package com.example.uitutorial.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PagingViewModelFactory(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val searchQuery: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PagingViewModel(apiService, database, searchQuery) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
