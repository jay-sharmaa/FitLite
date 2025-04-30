package com.example.uitutorial.paging

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val query: String,
) : RemoteMediator<Int, PostEntity>() {

    private val postDao = database.postDao()

    // Normalize the query to match how data is stored
    private val normalizedQuery: String = normalizeQuery(query)

    companion object {
        private val initializedQueries = mutableSetOf<String>()

        // Helper function to normalize query strings
        private fun normalizeQuery(query: String): String {
            // Remove quantity patterns (numbers followed by units)
            val withoutQuantity = query.replace(Regex("\\d+\\s*(?:lb|kg|g|oz|ml|l|dozen|pack|box)s?\\b"), "").trim()

            // Remove any extra spaces and trim
            val singleSpaced = withoutQuantity.replace(Regex("\\s+"), " ").trim()

            // If we got an empty string after removing quantities, return original trimmed
            if (singleSpaced.isEmpty()) return query.trim()

            // Convert the first character to lowercase and keep the rest as is
            return if (singleSpaced.isNotEmpty()) {
                singleSpaced.first().lowercase() + singleSpaced.substring(1)
            } else {
                singleSpaced
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        Log.d("PostRemoteMediator", "Initialize called for: $query (normalized: $normalizedQuery)")

        val localCount = postDao.countByName(normalizedQuery)
        Log.d("PostRemoteMediator", "Data count for $normalizedQuery: $localCount")

        return if (initializedQueries.contains(normalizedQuery) || localCount > 0) {
            Log.d("PostRemoteMediator", "Skipping initial refresh for: $normalizedQuery")
            initializedQueries.add(normalizedQuery)
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            Log.d("PostRemoteMediator", "Launching initial refresh for: $normalizedQuery")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>,
    ): MediatorResult {
        return try {
            Log.d("PostRemoteMediator", "Load called with type: $loadType for query: $query (normalized: $normalizedQuery)")

            // Check if this is a refresh and we need to fetch data
            if (loadType == LoadType.REFRESH) {
                val localCount = postDao.countByName(normalizedQuery)
                Log.d("PostRemoteMediator", "Current data count for $normalizedQuery: $localCount")

                // If we have data locally, skip API call
                if (localCount > 0) {
                    Log.d("PostRemoteMediator", "Data already exists for: $normalizedQuery. Skipping API.")
                    initializedQueries.add(normalizedQuery)
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                // Add to initialized queries to prevent future refresh calls
                initializedQueries.add(normalizedQuery)

                // If we got here, we need to fetch data from API
                val response = try {
                    Log.d("PostRemoteMediator", "Fetching data from API for: $normalizedQuery")
                    // You might want to use the original query for API call if needed
                    apiService.getPosts(query = normalizedQuery)
                } catch (e: Exception) {
                    Log.e("PostRemoteMediator", "Error fetching from API for $normalizedQuery", e)
                    null
                }

                database.withTransaction {
                    if (response != null) {
                        Log.d("PostRemoteMediator", "Inserting data into database for: $normalizedQuery")
                        postDao.insertAll(response)
                    }
                }

                delay(300)
                return MediatorResult.Success(endOfPaginationReached = true)
            } else if (loadType == LoadType.APPEND || loadType == LoadType.PREPEND) {
                // No more pages
                Log.d("PostRemoteMediator", "APPEND/PREPEND called, returning success with endReached=true")
                return MediatorResult.Success(endOfPaginationReached = true)
            } else {
                // Should not reach here, but just in case
                Log.d("PostRemoteMediator", "Unknown load type: $loadType")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            Log.e("PostRemoteMediator", "Error in load", e)
            MediatorResult.Error(e)
        }
    }
}