package com.example.uitutorial.paging
import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, PostEntity>() {

    private val postDao = database.postDao()
    private val foodItems = listOf("1lb eggs", "1lb fish", "1lb salad", "1lb fruit salad")

    // Use companion object to maintain state across instances
    companion object {
        private var hasInitializedData = false
    }

    override suspend fun initialize(): InitializeAction {
        // Check if data exists in database
        return if (postDao.countPosts() > 0 || hasInitializedData) {
            // Data exists, skip refresh
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // No data, perform refresh
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        return try {
            // Only fetch from the API on REFRESH and if we haven't initialized data
            if (loadType == LoadType.REFRESH && !hasInitializedData) {
                // Mark as initialized before making API calls to prevent concurrent calls
                hasInitializedData = true

                Log.d("PostRemoteMediator", "Starting data fetch for all food items")

                // Fetch and store data for each food item
                for (foodItem in foodItems) {
                    val response = try {
                        Log.d("PostRemoteMediator", "Fetching data for: $foodItem")
                        apiService.getPosts(query = foodItem)
                    } catch (e: Exception) {
                        Log.e("PostRemoteMediator", "Error fetching from API for $foodItem", e)
                        null
                    }

                    database.withTransaction {
                        if (response != null) {
                            Log.d("database", "Data inserted for: $foodItem")
                            postDao.insertAll(response)
                        }
                    }

                    // Add a small delay between requests
                    delay(500)
                }

                return MediatorResult.Success(endOfPaginationReached = true)
            } else if (loadType == LoadType.APPEND || loadType == LoadType.PREPEND) {
                // No more pages
                return MediatorResult.Success(endOfPaginationReached = true)
            } else {
                // For REFRESH when data exists, skip fetching
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}