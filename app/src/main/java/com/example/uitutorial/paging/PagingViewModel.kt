package com.example.uitutorial.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

class PagingViewModel(apiService: ApiService, database: AppDatabase): ViewModel() {
    @OptIn(ExperimentalPagingApi::class)
    val posts: Flow<PagingData<PostEntity>> = Pager(
        config = PagingConfig(pageSize = 10),
        remoteMediator = PostRemoteMediator(apiService, database),
        pagingSourceFactory = {database.postDao().getPagedPosts()}
    ).flow.cachedIn(viewModelScope)
}