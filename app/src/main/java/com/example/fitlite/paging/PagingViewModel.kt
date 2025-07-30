package com.example.fitlite.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import android.util.Log

class PagingViewModel(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val searchQuery: String
) : ViewModel() {
    // Process the query the same way throughout your app
    private val processedQuery = if (searchQuery.contains(" ")) {
        searchQuery.substringAfter(" ").lowercase(Locale.ROOT)
    } else {
        searchQuery.lowercase(Locale.ROOT)
    }

    @OptIn(ExperimentalPagingApi::class)
    val posts: Flow<PagingData<PostEntity>> = Pager(
        config = PagingConfig(pageSize = 10),
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            database = database,
            query = searchQuery,
            forceRefresh = false // Set to true when you need to force refresh
        ),
        pagingSourceFactory = {
            Log.d("PagingViewModel", "Creating paging source for query: '$processedQuery'")
            database.postDao().getPagedPosts(processedQuery)
        }
    ).flow.cachedIn(viewModelScope)

    // Add a function to force refresh data
    @OptIn(ExperimentalPagingApi::class)
    fun refreshData(): Flow<PagingData<PostEntity>> {
        Log.d("PagingViewModel", "Force refreshing data for query: '$searchQuery', processed: '$processedQuery'")
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = PostRemoteMediator(
                apiService = apiService,
                database = database,
                query = searchQuery,
                forceRefresh = true // Force refresh
            ),
            pagingSourceFactory = { database.postDao().getPagedPosts(processedQuery) }
        ).flow.cachedIn(viewModelScope)
    }
}