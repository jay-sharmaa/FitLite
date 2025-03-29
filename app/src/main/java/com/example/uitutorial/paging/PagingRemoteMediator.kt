package com.example.uitutorial.paging
import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, PostEntity>() {

    private val postDao = database.postDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) 1 else (lastItem.id / state.config.pageSize) + 1
                }
            }

            val response = try {
                apiService.getPosts(page = page, pageSize = state.config.pageSize)
            } catch (e: Exception) {
                Log.e("PostRemoteMediator", "Error fetching from API, checking local DB", e)
                null
            }

            database.withTransaction {
                if (response != null) {
                    Log.d("database", "Data inserted")
                    if (loadType == LoadType.REFRESH) postDao.clearAll()
                    postDao.insertAll(response)
                }
            }

            val localDataExists = postDao.countPosts() > 0

            if (response == null && !localDataExists) {
                return MediatorResult.Error(Exception("No internet and no cached data"))
            }

            MediatorResult.Success(endOfPaginationReached = response?.isEmpty() ?: false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
