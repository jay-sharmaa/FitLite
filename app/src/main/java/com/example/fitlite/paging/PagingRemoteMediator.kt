package com.example.fitlite.paging

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val query: String,
    private val forceRefresh: Boolean = false
) : RemoteMediator<Int, PostEntity>() {

    private val postDao = database.postDao()

    // Process the query exactly how it will be stored in the database
    private val processedQuery: String = processQueryForDatabase(query)
    private val TAG = "PostRemoteMediator"

    companion object {
        // Use a set that tracks which queries have been loaded in this session
        private val initializedQueries = mutableSetOf<String>()

        // This function should match exactly how you're transforming the query in ViewModel
        private fun processQueryForDatabase(query: String): String {
            // Extract the part after the first space and convert to lowercase
            return if (query.contains(" ")) {
                query.substringAfter(" ").lowercase()
            } else {
                query.lowercase()
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        Log.d(TAG, "Initialize called for: '$query' (processed: '$processedQuery'), forceRefresh: $forceRefresh")

        val localCount = postDao.countByName(processedQuery)
        Log.d(TAG, "Data count for '$processedQuery': $localCount")

        // If force refresh is enabled or we have no data, trigger refresh
        return if (forceRefresh || localCount == 0) {
            Log.d(TAG, "Launching initial refresh for: '$processedQuery'")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            Log.d(TAG, "Skipping initial refresh for: '$processedQuery'")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>,
    ): MediatorResult {
        return try {
            Log.d(TAG, "Load called with type: $loadType for query: '$query' (processed: '$processedQuery')")

            // For refresh, we always attempt to fetch from API if forceRefresh is true
            if (loadType == LoadType.REFRESH) {
                val localCount = postDao.countByName(processedQuery)
                Log.d(TAG, "Current data count for '$processedQuery': $localCount")

                // Only skip if we have data AND we're not forcing a refresh
                if (localCount > 0 && !forceRefresh) {
                    Log.d(TAG, "Data already exists for: '$processedQuery' and no force refresh. Skipping API.")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                // If we got here, we need to fetch data from API
                Log.d(TAG, "Fetching data from API for query: '$query'")

                val response = try {
                    // Use the ORIGINAL query for the API call, not the processed one
                    val apiResponse = apiService.getPosts(query = query)
                    Log.d(TAG, "API returned ${apiResponse.size} items for '$query'")

                    // Transform the response to match our database expectations
                    val transformedResponse = apiResponse.map { post ->
                        // Set the name field to be the processed query for consistent retrieval
                        post.copy(name = processedQuery)
                    }
                    transformedResponse
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching from API for '$query'", e)
                    null
                }

                if (response != null && response.isNotEmpty()) {
                    database.withTransaction {
                        // If force refreshing or if we have existing data, clear old data first
                        if (forceRefresh && localCount > 0) {
                            Log.d(TAG, "Force refresh: clearing old data for '$processedQuery'")
                            postDao.clearAll() // Using clearAll since you don't have clearByName
                        }

                        Log.d(TAG, "Inserting ${response.size} items into database for: '$processedQuery'")
                        postDao.insertAll(response)
                    }

                    delay(300)
                    return MediatorResult.Success(endOfPaginationReached = true)
                } else {
                    Log.d(TAG, "No data returned from API for '$query'")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            } else if (loadType == LoadType.APPEND || loadType == LoadType.PREPEND) {
                // No more pages
                Log.d(TAG, "APPEND/PREPEND called, returning success with endReached=true")
                return MediatorResult.Success(endOfPaginationReached = true)
            } else {
                // Should not reach here, but just in case
                Log.d(TAG, "Unknown load type: $loadType")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in load", e)
            MediatorResult.Error(e)
        }
    }
}