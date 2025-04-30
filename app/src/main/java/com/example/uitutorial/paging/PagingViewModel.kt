package com.example.uitutorial.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class PagingViewModel(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val searchQuery: String
) : ViewModel() {
    private val temp = searchQuery.substringAfter(" ").lowercase(Locale.ROOT)
    @OptIn(ExperimentalPagingApi::class)
    val posts: Flow<PagingData<PostEntity>> = Pager(
        config = PagingConfig(pageSize = 10),
        remoteMediator = PostRemoteMediator(apiService, database, searchQuery),
        pagingSourceFactory = { database.postDao().getPagedPosts(temp) }
    ).flow.cachedIn(viewModelScope)
}
